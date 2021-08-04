package com.liujunpeng.nettynotes.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description: 单reactor，单线程事件Handler
 * @Author: liujunpeng
 * @Date: 2021/7/29 9:54
 * @Version: 1.0
 */
public class SingleThreadReactor implements Runnable {

    final Selector selector;

    final ServerSocketChannel serverSocketChannel;

    //初始化Reactor属性
    public SingleThreadReactor() throws IOException {
        //初始化selector
        selector = Selector.open();
        //初始化服务器通道
        serverSocketChannel = ServerSocketChannel.open();
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //把服务器端channel注入到Selector中，并且监听accept接收事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    public static void main(String[] args) throws IOException {
        SingleThreadReactor singleThreadReactor = new SingleThreadReactor();
        singleThreadReactor.run();
    }

    @Override
    public void run() {
        try {
            //线程不中断状态，会一直循环监听
            while (!Thread.interrupted()) {
                //阻塞
                selector.select();
                //获取selector发生事件的selectionKey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                //遍历key
                while (iterator.hasNext()) {
                    //调用事件处理方法
                    dispatch(iterator.next());
                    //删除集合当前元素
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //事件处理
    void dispatch(SelectionKey key) throws IOException {
        //发生接收事件
        if (key.isAcceptable()) {
            //获取SocketChannel对象
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println(key);
            System.out.println("已经收到客户端连接：" + socketChannel.getLocalAddress());
            //设置非阻塞
            socketChannel.configureBlocking(false);
            //注册并读取事件
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            Handler handler = new Handler(key);
            handler.run();
        }
    }

    class Handler implements Runnable {

        final SelectionKey key;

        public Handler(SelectionKey key) throws IOException {
            this.key = key;
            System.out.println(key);
        }

        @Override
        public void run() {
            try {
                //发生读取事件
                if (key.isReadable()) {
                    //获取SocketChannel对象
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.configureBlocking(false);
                    //创建缓冲区
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    //读取数据
                    socketChannel.read(byteBuffer);
                    System.out.println(new String(byteBuffer.array()).trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
