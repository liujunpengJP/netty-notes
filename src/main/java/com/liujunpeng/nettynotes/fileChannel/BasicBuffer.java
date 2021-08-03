package com.liujunpeng.nettynotes.fileChannel;

import java.nio.IntBuffer;

/**
 * @Description: nioBuffer
 * @Author: liujunpeng
 * @Date: 2021/7/1 22:16
 * @Version: 1.0
 */
public class BasicBuffer {
    public static void main(String[] args) {

        IntBuffer intBuffer = IntBuffer.allocate(5);

        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }

        intBuffer.flip();

        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
