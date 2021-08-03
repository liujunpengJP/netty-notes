package com.liujunpeng.nettynotes.fileChannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @Description: nio实现文件拷贝
 * @Author: liujunpeng
 * @Date: 2021/7/6 9:20
 * @Version: 1.0
 */
public class NioCopyChannel {
    public static void main(String[] args) throws Exception {
        //读取1.txt
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        //写入2.txt
        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");

        FileChannel inputChannel = fileInputStream.getChannel();
        FileChannel outChannel = fileOutputStream.getChannel();

        outChannel.transferFrom(inputChannel, 0, inputChannel.size());
    }
}
