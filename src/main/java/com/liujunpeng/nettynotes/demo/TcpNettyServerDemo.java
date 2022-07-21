package com.liujunpeng.nettynotes.demo;

import com.liujunpeng.nettynotes.demo.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description: netty实现简单tcp传输，服务端
 * @Author: liujunpeng
 * @Date: 2022/7/20 10:09
 * @Version: 1.0
 */
public class TcpNettyServerDemo {
    public static void main(String[] args) throws InterruptedException {
        /*定义服务端*/
        //创建主线程组并设置线程数量
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
        //创建工作线程组并设置线程数量
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);
        //创建服务端启动对象，配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                //设置服务端通道信息
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //设置处理器
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
        //绑定端口
        ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();

        channelFuture.channel().closeFuture().sync();

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
