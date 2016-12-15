package me.cxis.merge;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by cheng.xi on 15/12/2016.
 */
public class MergeFile {

    public void mergeFile(String originFileNameOrPath,String targetFileNameOrPath) throws IOException {
        Path originFile = Paths.get(originFileNameOrPath);
        Path targetFile = Paths.get(targetFileNameOrPath);
        if(Files.notExists(targetFile)){
            Files.createFile(targetFile);
        }

        long size = (long)2 * 1024 * 1024 * 1024;
        System.out.println(Files.size(targetFile));
        System.out.println(size);
        int i = 0;
        while(Files.size(targetFile) < size){
            i ++;
            System.out.println("第" + i + "次");
            FileInputStream fileInputStream = new FileInputStream(originFile.toFile());
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile.toFile(),true);

            FileChannel readChannel = fileInputStream.getChannel();
            FileChannel writeChannel = fileOutputStream.getChannel();

            MappedByteBuffer mappedByteBuffer = readChannel.map(FileChannel.MapMode.READ_ONLY,0,readChannel.size());
            writeChannel.position(writeChannel.size());
            writeChannel.write(mappedByteBuffer);

            readChannel.close();
            writeChannel.close();
            fileInputStream.close();
            fileOutputStream.close();
        }


    }

    public static void main(String[] args) throws IOException {
        MergeFile mergeFile = new MergeFile();
        mergeFile.mergeFile("/xxx/1_1.txt","/xxx/1_1_big.txt");
    }
}
