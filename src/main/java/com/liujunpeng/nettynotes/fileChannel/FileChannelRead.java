package com.liujunpeng.nettynotes.fileChannel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Description: nio实现文件通道读取
 * @Author: liujunpeng
 * @Date: 2021/7/5 10:22
 * @Version: 1.0
 */
public class FileChannelRead {
    public static void main(String[] args) throws Exception {

        String content = "这个是NiO 使用FileChannel执行读写本地文件！！";

        //文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\test.txt");
        //创建通道
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //把数据写入缓冲区
        byteBuffer.put(content.getBytes());
        //重置索引position进行读取操作
        byteBuffer.flip();
        //把缓冲区数据写入到通道
        fileChannel.write(byteBuffer);
    }
}
