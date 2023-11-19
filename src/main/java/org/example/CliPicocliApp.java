package org.example;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Command (name = "BitcoinMiner",
		  mixinStandardHelpOptions = true,
		  version = "1")
public class CliPicocliApp implements Runnable {
	public final static CountDownLatch countDownLatch = new CountDownLatch(1);
	public final static AtomicBoolean  shoutDownFlag  = new AtomicBoolean(Boolean.FALSE);

	@Option (names = { "-a", "--address" },
			 description = "钱包地址",
			 defaultValue = "")
	private String address;

	@Option (names = { "-t", "--thread" },
			 description = "线程数",
			 defaultValue = "1")
	private int numThread;

	@Option (names = { "-s", "--startIndex" },
			 description = "开始槽位",
			 defaultValue = "0")
	private int startIndex;

	@Option (names = { "-e", "--stepScope" },
			 description = "集群内总共的线程数",
			 defaultValue = "0")
	private int allWorker;

	@Option (names = { "-ph", "--proxyHost" },
			 description = "socks5代理服务器IP",
			 defaultValue = "127.0.0.1")
	private String proxyHost;

	@Option (names = { "-p", "--proxyPort" },
			 description = "socks5代理服务器端口",
			 defaultValue = "10808")
	private int proxyPort;

	private static final Timer TIMER = new Timer("show");

	@Override
	public void run() {
		if(StrUtil.isBlank(address)){
			Console.log("钱包地址不能为空 例如: -a 12dsadd487524187663434");
		}
		if (allWorker == 0) {
			allWorker = Runtime.getRuntime().availableProcessors() / 2;
			startIndex = 0;
		}

		final Double stepScope = NumberUtil.div(BitcoinMiner.MAX * 1D, allWorker);
		MiningPoolConnection.connecMiningPool(address,proxyHost,proxyPort);
		final NettyClient nettyClient = MiningPoolConnection.nettyClient;
		BitcoinMiner[] solo = new BitcoinMiner[numThread];
		for (int i = 0; i < solo.length; i++) {
			final BitcoinMiner bitcoinMiner = new BitcoinMiner(i, countDownLatch, address, startIndex + i, stepScope.intValue());
			bitcoinMiner.start();
			solo[i] = bitcoinMiner;
		}
		TIMER.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < solo.length; i++) {
					sb.append("solo" + i + " : " + solo[i].getSeepd() + " /s");
					if (i != solo.length - 1) {
						sb.append("----");
					}
				}
				System.out.print(sb + "\r");
			}
		}, 1000 * 5, 2000);
		RuntimeUtil.addShutdownHook(() -> {
			shoutDownFlag.set(true);
			nettyClient.close();

		});
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
