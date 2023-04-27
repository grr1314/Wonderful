package com.lc.nativelib.file;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class MMapFileOperator implements IFileOperator {
    @NonNull
    @Override
    public String readFromFile(@NonNull File file) {
        String result = "";
        try {
            FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(READ_ONLY, 0, file.length());
            if (mappedByteBuffer != null) {
                byte[] bytes = new byte[(int) file.length()];
                mappedByteBuffer.get(bytes);
                String content = new String(bytes, StandardCharsets.UTF_8);
                result = content;
                System.out.println(content);
            }
            fileChannel.close();  //40
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void writeToFile(@NonNull File file, @NonNull String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,
                    0, bytes.length);
            int len = -1;
            while ((len = randomAccessFile.read(bytes)) != -1) {
                mappedByteBuffer.put(bytes, 0, len); // 写入数据
            }
            randomAccessFile.close();
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
