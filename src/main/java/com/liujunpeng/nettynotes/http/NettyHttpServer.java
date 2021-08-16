package com.liujunpeng.nettynotes.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @Description: 使用netty实现http服务端
 * @Author: liujunpeng
 * @Date: 2021/8/13 9:58
 * @Version: 1.0
 */
public class NettyHttpServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup master = new NioEventLoopGroup(4);
        NioEventLoopGroup slave = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(master, slave)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加HttpServerCodec，是netty提供的编解码器
                            pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
                            //添加自定义的handler
                            pipeline.addLast("MyCustomHttpServerHandler", new CustomHttpServerHandler());
                        }
                    });
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            //添加回调的监听
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isDone() && future.isSuccess()) {
                    System.out.println("绑定成功");
                } else {
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                }
            });
            System.out.println("当前服务端地址：" + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            master.shutdownGracefully();
            slave.shutdownGracefully();
        }
    }

    /**
     * 自定义handler
     * SimpleChannelInboundHandler是ChannelInboundHandlerAdapter子类
     * 允许显式仅处理特定类型的消息
     */
    static class CustomHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            System.out.println("msg的实际类型是：" + msg.getClass());
            if (msg instanceof HttpRequest) {
                //打印每次http请求的通道和handler是否为同一个对象
                System.out.println("pipeline hascode:" + ctx.pipeline().hashCode() + " CustomHttpServerHandler hashcode" + this.hashCode());
                System.out.println("当前线程：" + Thread.currentThread().getName());
                System.out.println("访问的客户端地址：" + ctx.channel().remoteAddress());
                HttpRequest httpRequest = (HttpRequest) msg;
                //打印请求的url
                URI uri = new URI(httpRequest.uri());
                System.out.println("访问的地址：" + uri.getPath());
                //回复消息给客户端
                ByteBuf byteBuf = Unpooled.copiedBuffer("Hello 这是服务端！！", CharsetUtil.UTF_8);
                //构造一个httpResponse
                DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                //设置请求头
                defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(defaultFullHttpResponse);
            }
        }
    }
}
