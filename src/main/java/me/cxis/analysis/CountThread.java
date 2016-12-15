package me.cxis.analysis;

import org.apache.commons.lang3.StringUtils;

import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by cheng.xi on 13/12/2016.
 */
public class CountThread implements Callable<Integer> {
    private CountDownLatch countDoneSingal;
    private long start;
    private long end;
    private Path file;
    private String word;
    private int count = 0;
    private final int DATA_CHUNK = 256 * 1024 * 1024;

    public CountThread(CountDownLatch countDoneSingal, long start, long end, Path file, String word) {
        this.countDoneSingal = countDoneSingal;
        this.start = start;
        this.end = end;
        this.file = file;
        this.word = word;
    }

    @Override
    public Integer call() throws Exception {
        Thread.sleep(3000);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file.toRealPath().toString(),"rw");
        randomAccessFile.seek(start);
        long contentLength = end - start + 1;
        long times = contentLength / DATA_CHUNK + 1;
        byte[] bytes;
        if(contentLength < DATA_CHUNK){
            bytes = new byte[(int)contentLength];
        }else {
            bytes = new byte[DATA_CHUNK];
        }
        for(int i = 0; i < times; i++){
            int read = randomAccessFile.read(bytes);
            if(read < 0){
                break;
            }
            String readStr = new String(bytes,"utf-8");
            int tmpCount = StringUtils.countMatches(readStr,word);
            count += tmpCount;
        }

        countDoneSingal.countDown();
        System.out.println("线程：" + Thread.currentThread().getName() + " 完成统计，数目为：" + count);
        return count;
    }

    public static void main(String[] args) {
    }
}
