package org.example;

import cn.hutool.log.StaticLog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks5ProxyHandler;

import java.net.InetSocketAddress;

public class NettyClient {

    private final String host;
    private final int port;
    private final String proxyHost;

    private final int proxyPort;

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private SocketChannel channel;

    public NettyClient(String host, int port,String proxyHost ,int proxyPort) {
        this.host = host;
        this.port = port;
        group = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        run();
    }

    private void run(){
        final InetSocketAddress proxyAddress = new InetSocketAddress(proxyHost, proxyPort);
        final Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(proxyAddress);
        bootstrap.group(group)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .option(ChannelOption.AUTO_READ, true)
                 .handler(new TCPChannelInitializer(socks5ProxyHandler));
        // 连接到服务器
        connect();
    }
    private void connect(){
        // 连接到服务器
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel = (SocketChannel)future.channel();
    }

    private void sendMsg0(String msg,int count){
        if (count>=50){
            StaticLog.error("网络不通");
            return;
        }
        if (channel.isWritable()){
            channel.write(msg);
            channel.flush();
        }else {
            connect();
            sendMsg0(msg,count+1);
        }
    }

    public void sendMsg(String msg){
        sendMsg0(msg,0);
    }

    public void close() {
        group.shutdownGracefully();
    }
}
