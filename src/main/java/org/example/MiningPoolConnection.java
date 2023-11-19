package org.example;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;

public class MiningPoolConnection {

	public final static MiningCtx ctx = new MiningCtx();
	public static       NettyClient nettyClient;
	public static       String    address;

	public static final void connecMiningPool(String address,String proxyHost ,int proxyPort) {
		MiningPoolConnection.address = address;
		String targetHost = "solo4.ckpool.org";
		int targetPort = 3333;
		nettyClient = new NettyClient(targetHost, targetPort,proxyHost,proxyPort);

		nettyClient.sendMsg("{\"id\": 1, \"method\": \"mining.subscribe\", \"params\": []}");
		nettyClient.sendMsg("{\"params\": [\"" + address + "\", \"password\"], \"id\": 2, \"method\": \"mining.authorize\"}");
	}

	public static final Integer getCurrentBlockHeight() {
		final String s = HttpUtil.get("https://blockchain.info/latestblock");
		final JSONObject entries = JSONUtil.parseObj(s);
		return entries.getInt("height");
	}

	public static void submitBlock(int workOn, String hash, String blockheader, String nonce, String extranonce2) {
		final String now = DateUtil.now();
		StaticLog.info("[" + now + "] [*] 区块高度 " + (workOn + 1) + " 搜罗到了.");
		StaticLog.info("[*] 区块Hash: " + hash);
		StaticLog.info("[*] 区块头: " + blockheader);
		String payload = String.format("{\"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\"], \"id\": 1, \"method\": \"mining.submit\"}", address, ctx.getJob_id(), extranonce2, ctx.getNtime(), nonce);
		nettyClient.sendMsg(payload);
		StaticLog.info("[" + now + "] [*] Payload: " + payload);
	}
}
