package cn.edu.whu.irlab.service.analysis;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-08-20 14:52
 * @desc 分词算法
 **/
@Service
public class DivideWord {
    public String left_to_right_divide(String sentence,int max) throws IOException{
        List words=new ArrayList();	//用于存放词的集合
        int i=0;
        String word="";
        int maxnum=max;	//保存max的值
        while(i<sentence.length()){
            //截取max长度的词，若总长度不足，则截取剩余部分
            if(i+max<=sentence.length()){
                word=sentence.substring(i,i+max);
            }else{
                word=sentence.substring(i);
            }
            //如果截取的word是个词或者当前max值为1，则将word加入到词集合中，并把指针向前移动i个位
            if(isWord(word)||max==1){
                words.add(word);
                i=i+max;
                max=maxnum;
            }else{
                max--;
            }
        }
        //将集合合成一个字符串返回
        String result="";
        for(int j=0;j<words.size();j++){
            result+=words.get(j).toString()+"/";
        }
        return result;
    }

    public String right_to_left_divide(String sentence,int max) throws IOException{
        List words=new ArrayList();	//用于存放词的集合
        int i=sentence.length();
        String word="";
        int maxnum=max;	//保存max的值
        while(i>0){
            //截取max长度的词，若总长度不足，则截取剩余部分
            if(i-max>=0){
                word=sentence.substring(i-max,i);
            }else{
                word=sentence.substring(0,i);
            }

            //如果截取的word是个词或者当前max值为1，则将word加入到词集合中，并把指针向前移动i个位
            if(isWord(word)||max==1){
                words.add(word);
                i=i-max;
                max=maxnum;
            }else{
                max--;
            }
        }
        //将集合合成一个字符串返回
        String result="";
        for(int j=words.size()-1;j>=0;j--){
            result+=words.get(j).toString()+"/";
        }
        return result;
    }

    //打开词典word.txt，匹配word，判断word是否是一个词
    public boolean isWord(String word) throws IOException{
        boolean isword=false;
        BufferedReader br=new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:dictionary.txt").getPath()));
        String text;
        while((text=br.readLine())!=null){
            String[]key=text.split(",");
//			System.out.println(key[0]);
            if(word.equals(key[0])) {
                isword=true;
            }
        }
        br.close();
        return isword;
    }

    public static void main(String[]args) throws IOException {

    }
}
