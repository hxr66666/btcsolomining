package org.example;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.log.StaticLog;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

import static org.example.CliPicocliApp.shoutDownFlag;
import static org.example.MiningPoolConnection.ctx;

public class BitcoinMiner extends Thread {
	public static final Long MAX = 1L << 32;

	private MessageDigest sha256 = null;

	private final CountDownLatch countDownLatch;

	private final   int  startIndex;
	private final   int  stepScope;
	public volatile long seepd = 0;

	public BitcoinMiner(int workIndex, CountDownLatch countDownLatch, String address, int startIndex, int stepScope) {
		super("Miner" + workIndex);
		this.countDownLatch = countDownLatch;
		this.startIndex = startIndex;
		this.stepScope = stepScope;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		solveBlock();
	}

	private void solveBlock() {
		try {
			countDownLatch.await();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!shoutDownFlag.get()) {
			String target = fillZero(ctx.getNbits().substring(2) + "00".repeat(Integer.parseInt(ctx.getNbits().substring(0, 2), 16) - 3), 64);
			final BigInteger bigIntegerTarget = new BigInteger(target, 16);

			String extranonce2 = fillZero(Long.toHexString(new Random().nextLong(MAX - 1)), 2 * ctx.getExtranonce2_size());

			String coinbase = ctx.getCoinb1() + ctx.getExtranonce1() + extranonce2 + ctx.getCoinb2();
			byte[] coinbaseHash = getDoubleSHA256(HexUtil.decodeHex(coinbase));
			String merkleRootStr = getMerkleRootStr(coinbaseHash);

			int workOn = getBlockHeight();

			final Map<Integer, Double> nHeightDiff = ctx.getnHeightDiff();
			nHeightDiff.put(workOn + 1, 0d);

			final BigInteger diff = new BigInteger("00000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

			log("当前高度" + (workOn + 1));
			int startIndexTmp = startIndex + stepScope;
			int endIndex = startIndex + stepScope;
			long startTime = System.currentTimeMillis();
			long work = 0;
			while (startIndexTmp <= endIndex) {
				if (shoutDownFlag.get()) {
					break;
				}
				if (!ctx.getPrevhash().equals(ctx.getUpdatedPrevHash())) {
					log("新区块 " + ctx.getPrevhash() + " detected on network");
					log("重新计算新区块 " + (workOn + 1) + " 难度 " + nHeightDiff.get(workOn + 1));
					ctx.setUpdatedPrevHash(ctx.getPrevhash());
					break;
				}

				String nonce = fillZero(Long.toHexString(startIndex), 8);
				startIndexTmp = startIndexTmp + 1;
				String blockheader = ctx.getVersion() + ctx.getPrevhash() + merkleRootStr + ctx.getNtime() + ctx.getNbits() + nonce + "000000800000000000000000000000000000000000000000000000000000000000000000000000000000000080020000";
				byte[] hashBytes = getDoubleSHA256(HexUtil.decodeHex(blockheader));
				String hash = HexUtil.encodeHexStr(hashBytes);

				if (hash.startsWith("0000000")) {
					log("[" + timer() + "] 新 hash: " + hash + " 在区块 " + (workOn + 1));
				}

				final BigInteger thisHash = new BigInteger(hash, 16);
				double difficulty = diff.divide(thisHash).doubleValue();

				if (nHeightDiff.get(workOn + 1) < difficulty) {
					nHeightDiff.put(workOn + 1, difficulty);
				}
				work += 1;
				seepd = (long) (work * 1000 / ((System.currentTimeMillis() - startTime) + 0.00000000000000001));

				if (thisHash.compareTo(bigIntegerTarget) < 0) {
					MiningPoolConnection.submitBlock(workOn, hash, blockheader, nonce, extranonce2);
					log("命中:" + hash);
					LockSupport.parkUntil(System.currentTimeMillis() + 5000);
					break;
				}
			}
		}
	}

	private String getMerkleRootStr(byte[] coinbaseHash) {
		byte[] merkleRoot = coinbaseHash;
		for (String h : ctx.getMerkle_branch()) {
			byte[] hashBytes = HexUtil.decodeHex(h);
			merkleRoot = getDoubleSHA256(concatenateByteArrays(merkleRoot, hashBytes));
		}
		// 将 Merkle 根转换为小端字节序
		return convertToLittleEndian(merkleRoot);
	}

	private byte[] getDoubleSHA256(byte[] input) {
		sha256.reset();
		final byte[] digest = sha256.digest(input);
		sha256.reset();
		return sha256.digest(digest);
	}

	private static String convertToLittleEndian(byte[] hexString) {
		byte[] littleEndianBytes = new byte[hexString.length];
		for (int i = 0; i < hexString.length; i++) {
			littleEndianBytes[i] = hexString[hexString.length - 1 - i];
		}
		// 将小端字节序转换为十六进制字符串
		return HexUtil.encodeHexStr(littleEndianBytes).toLowerCase();
	}

	private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	private static int getBlockHeight() {
		return MiningPoolConnection.getCurrentBlockHeight();
	}

	private static void log(String message) {
		StaticLog.info(message);
	}

	private static String timer() {
		return DateUtil.now();
	}

	public static String fillZero(String str, int n) {
		String str2 = "";
		StringBuilder str1 = new StringBuilder();
		int length = str.length();
		if (length < n)
			for (int i = 0; i < n - length; i++) {
				str2 = str1.append('0').toString();
			}
		return str2 + str;
	}

	public long getSeepd() {
		return seepd;
	}
}
