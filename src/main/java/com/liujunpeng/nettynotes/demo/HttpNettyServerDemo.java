package com.liujunpeng.nettynotes.demo;

import com.liujunpeng.nettynotes.demo.handler.HttpChannelInitializer;
import com.liujunpeng.nettynotes.demo.handler.HttpTestServerHandler;
import com.sun.javafx.iio.png.PNGImageLoader2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Description:
 * @Author: liujunpeng
 * @Date: 2022/7/21 11:06
 * @Version: 1.0
 */
public class HttpNettyServerDemo {

    public static void main(String[] args) throws Exception {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpTestServerHandler());
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(8100).sync();

        System.out.println("服务端已经启动...");

        channelFuture.channel().closeFuture().sync();

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
