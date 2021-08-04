package com.liujunpeng.nettynotes.reactor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @Description: 主从线程池管理Reactor服务端
 * @Author: liujunpeng
 * @Date: 2021/8/3 10:33
 * @Version: 1.0
 */
public class MasterSlaveThreadPoolReactorServer {

    public static void main(String[] args) {
        //创建主从两个线程组,构造函数入参可添加线程数量，不填默认计算机核数*2
        NioEventLoopGroup masterGroup = new NioEventLoopGroup(4);
        NioEventLoopGroup slaveGroup = new NioEventLoopGroup(4);

        try {
            //创建服务端启用对象配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置主和从线程组
            serverBootstrap.group(masterGroup, slaveGroup)
                    //设置需要使用的通道
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列的连接数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活跃连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //设置处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //自定义的handler需要集成netty定义好的
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务端已准备完毕");
            ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();
            //对关闭通道进行监听而非关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭组
            masterGroup.shutdownGracefully();
            slaveGroup.shutdownGracefully();
        }
    }

    /**
     * 服务端事件处理器
     */
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("服务器读取线程：" + Thread.currentThread().getName());
            //获取channel
            Channel channel = ctx.channel();
            //获取管道
            ChannelPipeline pipeline = ctx.pipeline();

            //获取客户端发送的消息
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("接受客户端发送消息：" + byteBuf.toString(CharsetUtil.UTF_8));
            System.out.println("地址：" + channel.remoteAddress());
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("已收到客户端请求", CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            //发生异常
            ctx.close();
        }
    }

}
