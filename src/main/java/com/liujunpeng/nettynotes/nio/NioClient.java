package com.liujunpeng.nettynotes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Description: nio实现客户端连接
 * @Author: liujunpeng
 * @Date: 2021/7/21 10:46
 * @Version: 1.0
 */
public class NioClient {


    private Selector selector;

    /**
     * 对selector和 socketChannel进行初始化操作
     * 绑定端口
     *
     * @param ip
     * @param port
     */
    public void initClinet(String ip, int port) throws IOException {
        //获取scoketChannel
        SocketChannel socketChannel = SocketChannel.open();
        //设置socketChannel非阻塞
        socketChannel.configureBlocking(false);
        //获取selector
        this.selector = Selector.open();
        //连接ip和端口
        socketChannel.connect(new InetSocketAddress(ip, port));
        //注册连接事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    /**
     * 使用轮训方式监听selector发生的事件
     */
    public void listen() throws IOException {
        while (true) {
            //没有事件发送就会阻塞在当前位置
            selector.select();
            //获取发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            //遍历
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //判断是否连接事件发生
                if (selectionKey.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //是否正在连接，如果正在连接则完成连接
                    if (socketChannel.isConnectionPending()) {
                        socketChannel.finishConnect();
                    }
                    //设置scoketChannel非阻塞
                    socketChannel.configureBlocking(false);
                    System.out.println("已成功连接服务端！！");
                    //发送消息过去
                    socketChannel.write(ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)));
                    //并且监听读取事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()) {
                    //接受读取事件
                    read(selectionKey);
                }
                iterator.remove();
            }
        }
    }

    /**
     * selectionKey读取内容
     *
     * @param selectionKey
     */
    public void read(SelectionKey selectionKey) throws IOException {
        //根据selectionKey获取socketChannel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建读取数据缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //读取数据
        socketChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()).trim());
    }

    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.initClinet("127.0.0.1", 8888);
        nioClient.listen();
    }
}
