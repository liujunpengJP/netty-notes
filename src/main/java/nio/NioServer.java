package nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Description: nio实现服务端监听
 * @Author: liujunpeng
 * @Date: 2021/7/20 9:35
 * @Version: 1.0
 */
public class NioServer {

    /**
     * selector
     */
    private Selector selector;

    /**
     * 初始化，并且绑定端口
     */
    public void initServer(int port) throws IOException {
        //创建ServerScoketChannel服务通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //服务端绑定ip和端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        //设置服务端通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        //创建Selector路由
        this.selector = Selector.open();
        //把服务端通道注册到selector上，并且监听接受事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("当前selector注册数为：" + selector.keys().size());
    }

    /**
     * 轮训方式，监听selector上是否有时间发生
     */
    public void listen() throws IOException {

        while (true) {
            //未发生事件时为阻塞状态
            selector.select();
            //获取发生事件的SelectionKey
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //判断是否是接受事件
                if (selectionKey.isAcceptable()) {
                    //获取事件的channel
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    //设置非阻塞
                    socketChannel.configureBlocking(false);
                    //向客户端发送消息
                    socketChannel.write(ByteBuffer.wrap(("已经接收到：" + socketChannel.getRemoteAddress() + " 的连接！").getBytes(StandardCharsets.UTF_8)));
                    System.out.println("已经接收到：" + socketChannel.getRemoteAddress() + " 的连接！当前selector连接数：" + selector.keys().size());
                    //并且监听客户端发送消息
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    //读取消息
                    read(selectionKey);
                }
                //删除当前selectionKey方式重复监听
                iterator.remove();
            }
        }
    }

    /**
     * 根据selectionKey读取消息
     *
     * @param selectionKey
     * @throws IOException
     */
    public void read(SelectionKey selectionKey) throws IOException {
        //获取监听的客户端channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建读取数据的缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //读取数据
        socketChannel.read(byteBuffer);
        //接收到客户端发送数据
        System.out.println(new String(byteBuffer.array()).trim());
        //回复客户端
        socketChannel.write(ByteBuffer.wrap("已经成功收到消息".getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.initServer(6666);
        nioServer.listen();
    }
}
