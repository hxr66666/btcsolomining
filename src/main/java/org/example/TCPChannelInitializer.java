package org.example;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.util.CharsetUtil;

public class TCPChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final ChannelHandler CHANNEL_HANDLER = new MsgSimpleChannelInboundHandler();

	private final Socks5ProxyHandler socks5ProxyHandler;

	public TCPChannelInitializer(Socks5ProxyHandler socks5ProxyHandler) {
		this.socks5ProxyHandler = socks5ProxyHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) {
		final ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(socks5ProxyHandler);
		pipeline.addLast(new LineEncoder());
		pipeline.addLast(new LineBasedFrameDecoder(2048));
		pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
		pipeline.addLast(CHANNEL_HANDLER);


	}

}
