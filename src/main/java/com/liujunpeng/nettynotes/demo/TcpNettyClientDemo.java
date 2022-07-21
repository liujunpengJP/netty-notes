package com.liujunpeng.nettynotes.demo;

import com.liujunpeng.nettynotes.demo.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description: netty实现简单tcp传输，客户端
 * @Author: liujunpeng
 * @Date: 2022/7/20 10:46
 * @Version: 1.0
 */
public class TcpNettyClientDemo {

    public static void main(String[] args) throws InterruptedException {
        //创建一个客户端事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup();
        //创建客户端启动对象
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666);

        channelFuture.channel().closeFuture().sync();
        group.shutdownGracefully();
    }
}
