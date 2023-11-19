package org.example;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.StaticLog;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static org.example.CliPicocliApp.countDownLatch;

@Sharable
public class MsgSimpleChannelInboundHandler extends SimpleChannelInboundHandler<String> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg){
		if (msg.contains("result")) {
			subscribeToMining(msg);
			return;
		}
		if (msg.contains("mining.notify")) {
			authorizeMining(msg);
			return;
		}
	}

	private static void subscribeToMining(String response) {
		StaticLog.info(response);
		JSONObject jsonResponse = new JSONObject(response);
		final Integer id = jsonResponse.getInt("id");
		if (id == 1) {
			JSONArray result = jsonResponse.getJSONArray("result");
			MiningPoolConnection.ctx.setSub_details(result.getStr(0));
			MiningPoolConnection.ctx.setExtranonce1(result.getStr(1));
			MiningPoolConnection.ctx.setExtranonce2_size(result.getInt(2));
		}
	}

	private static void authorizeMining(String response) {
		JSONArray params = new JSONObject(response).getJSONArray("params");
		MiningPoolConnection.ctx.setJob_id(params.getStr(0));
		MiningPoolConnection.ctx.setPrevhash(params.getStr(1));
		MiningPoolConnection.ctx.setCoinb1(params.getStr(2));
		MiningPoolConnection.ctx.setCoinb2(params.getStr(3));
		MiningPoolConnection.ctx.setMerkle_branch(params.getJSONArray(4).toList(String.class));
		MiningPoolConnection.ctx.setVersion(params.getStr(5));
		MiningPoolConnection.ctx.setNbits(params.getStr(6));
		MiningPoolConnection.ctx.setNtime(params.getStr(7));
		MiningPoolConnection.ctx.setClean_jobs(params.getBool(8));
		MiningPoolConnection.ctx.setUpdatedPrevHash(MiningPoolConnection.ctx.getPrevhash());
		if (countDownLatch.getCount() != 0) {
			countDownLatch.countDown();
		}
		StaticLog.info(response);
	}
}
