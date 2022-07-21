package com.liujunpeng.nettynotes.fileChannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @Description: nio实现文件通道写入
 * @Author: liujunpeng
 * @Date: 2021/7/5 10:22
 * @Version: 1.0
 */
public class FileChannelRead {
    public static void main(String[] args) throws Exception {
        cp();
    }

    /**
     * 读取文件内容到控制台
     */
    private static void fileRead() throws IOException {
        File file = new File("d:\\test.txt");
        //创建文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //创建通道
        FileChannel channel = fileInputStream.getChannel();
        //创建文件大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        //从通道读取数据到缓冲区
        int read = channel.read(byteBuffer);
        //打印缓冲区的数据
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }

    /**
     * 把字符串写入文件
     *
     * @throws IOException
     */
    private static void fileWrite() throws IOException {
        String concat = "Hello Word";
        //创建输出流
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\test.txt");
        //创建通道
        FileChannel channel = fileOutputStream.getChannel();
        //创建大小1024缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //把数据放入缓冲区
        buffer.put(concat.getBytes(StandardCharsets.UTF_8));
        //设置缓冲区读取模式
        buffer.flip();
        //把缓冲区数据写入到通道
        int read = channel.write(buffer);
        fileOutputStream.close();
    }

    /**
     * 文件拷贝
     */
    private static void cp() throws IOException {
        //创建输入流读取文件
        FileInputStream fileInputStream = new FileInputStream("d:\\test.txt");
        //创建输出流写入文件
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\test2.txt");

        //获取相应的通道
        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        //通道拷贝
        outputStreamChannel.transferFrom(inputStreamChannel, 0, inputStreamChannel.size());

        //关闭通道
        inputStreamChannel.close();
        outputStreamChannel.close();
        //关流
        fileInputStream.close();
        fileOutputStream.close();
    }
}
