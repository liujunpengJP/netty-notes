package com.liujunpeng.nettynotes.demo.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Description:
 * @Author: liujunpeng
 * @Date: 2022/7/21 15:06
 * @Version: 1.0
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //http解码器
        pipeline.addLast(new HttpServerCodec());

        pipeline.addLast(new HttpTestServerHandler());
    }
}
