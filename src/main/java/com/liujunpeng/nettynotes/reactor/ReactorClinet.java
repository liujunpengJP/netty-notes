package com.liujunpeng.nettynotes.reactor;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @Description: reactor客户端
 * @Author: liujunpeng
 * @Date: 2021/8/3 17:52
 * @Version: 1.0
 */
public class ReactorClinet {
    public static void main(String[] args) {
        //创建事件循环组
        EventLoopGroup clinetGroup = new NioEventLoopGroup(4);

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(clinetGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //添加自定义处理器
                            ch.pipeline().addLast(new ClinetHandler());
                        }
                    });

            System.out.println("客户端已准备就绪");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clinetGroup.shutdownGracefully();
        }
    }

    static class ClinetHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("Hello 这里是客户端请求！", CharsetUtil.UTF_8));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf byteBuf = (ByteBuf) msg;
            Channel channel = ctx.channel();
            System.out.println("客户端收到服务端" + channel.remoteAddress() + "请求：" + byteBuf.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }
}
