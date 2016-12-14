package me.cxis.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by cheng.xi on 13/12/2016.
 * 首先需要将大文件进行分割，分割成小的文件后，使用多线程分别分析文件，最后汇总结果
 */
public class AnalysisBigFile {

    /**
     * 查找给定字出现的次数
     * @param fileName 要查找的文件
     * @param word 需要查找的词语
     * @return
     */
    public int wordCount(String fileName,String word) throws IOException, ExecutionException, InterruptedException {
        //文件信息
        Path file = Paths.get(fileName);
        System.out.println("文件名：" + file.getFileName());

        long fileSize = new File(fileName).length();
        System.out.println("文件大小：" + fileSize);

        int cpus = Runtime.getRuntime().availableProcessors();
        System.out.println("系统可用处理器：" + cpus);

        //线程数量
        int threadNumber = cpus + 1;
        System.out.println("执行的线程数量：" + threadNumber);

        //每个线程需要读取的字节数
        long perThreadReadNumber = fileSize / threadNumber;
        System.out.println("每个线程需要读取的数量：" + perThreadReadNumber);

        //文件最后剩余的字节数
        long leftReadNumber = fileSize % threadNumber;
        System.out.println("leftReadNumber:" + leftReadNumber);

        //初始化线程池
        ExecutorService pool = Executors.newFixedThreadPool(threadNumber);

        List<Future<Integer>> resultLis = new ArrayList<>();
        CountDownLatch countDoneSingal = new CountDownLatch(threadNumber);
        for(int i = 0; i < threadNumber; i++){
            if(leftReadNumber > 0 && i == threadNumber - 1){//读取最后的数据leftReadNumber
                CountThread countThread = new CountThread(countDoneSingal, i * perThreadReadNumber, (i + 1) * perThreadReadNumber + leftReadNumber, file,word);
                Future<Integer> future = pool.submit(countThread);
                resultLis.add(future);
            }else {//前面的线程，每个线程读取perThreadReadNumber个字节
                CountThread countThread = new CountThread(countDoneSingal,i * perThreadReadNumber,(i + 1) * perThreadReadNumber,file,word);
                Future<Integer> future = pool.submit(countThread);
                resultLis.add(future);
            }
        }

        try {
            System.out.println(Thread.currentThread().getName() + " 线程正在等待汇总结果；");

            //结果总和
            int count = 0;
            for(Future<Integer> future : resultLis){
                count += future.get();
            }
            countDoneSingal.await();
            System.out.println("汇总结果出来了：" + count);
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        AnalysisBigFile analysisBigFile = new AnalysisBigFile();
        try {
            analysisBigFile.wordCount("/xxx/1_1.txt","j");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
