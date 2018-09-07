package cn.edu.whu.irlab.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-09-08 0:28
 * @desc 读停用词的工具类
 **/
public class ReadStopWords {

    public static ArrayList<String> readStopWords(){
        ArrayList<String> stopWordAL  = new ArrayList();
        try{
            FileInputStream stopWordFile = new FileInputStream(ResourceUtils.getFile("classpath:guochengrui//ChineseStopWord.txt").getPath());//停用词
            InputStreamReader input=new InputStreamReader(stopWordFile,"GBK");
            BufferedReader stopWordBR = new BufferedReader(input);//构造一个BufferedReader类来读取ChineseStopWord文件
            String stopWord;
            while ((stopWord = stopWordBR.readLine()) != null) {//使用readLine方法，一次读一行 读取停用词
                stopWordAL.add(stopWord);
            }
            stopWordBR.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }//读取停用词表
        return stopWordAL;
    }
}
