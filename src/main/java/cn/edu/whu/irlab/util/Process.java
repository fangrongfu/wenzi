package cn.edu.whu.irlab.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.edu.whu.irlab.util.WordCorrect.correct;
//英文预处理：文档标准化、去停用词、词干还原
public class Process {
    //文档标准化：去除原文档中的数字、连字符、标点符号、大写转小写、拼写检查
    public String Pre_Process(String data) throws IOException { //data：要处理的数据
        ArrayList<String> terms = new ArrayList<String>();//处理后的terms
        Pattern p = Pattern.compile("[\\d\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]");
        Matcher matcher = p.matcher(data);
        data = matcher.replaceAll(" ");//去除原文档中的数字、连字符、标点符号
        // 将文档转成小写，然后切分成单词，存在list中
        data = data.toLowerCase();//大写转小写
        System.out.println(data);//检验效果
        //拼写检查
        String[] words = data.split(" ");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word == null || word.equals(" ") || word.equals("")) {
            } else {
                // System.out.println(word);
                word = word.trim();
                word = word.replace(" ", "");
                //SpellCorrect sc=new SpellCorrect();
               //word=correct(word);
                  // String result= EnWordChecker.getInstance().correct(word);
                //System.out.println("校正后"+word);
                terms.add(correct(word));
            }
        }

        for (int i = 0; i < terms.size(); i++) {
            System.out.println(terms.get(i));
            s.append(terms.get(i));
            s.append(" ");
        }
        String preresult = new String(s);
        System.out.println(preresult);
        return preresult;
    }

    //去停用词
    public String StopwordsRemove(String preresult) throws IOException {
        String dataDir = null;
        try {
            dataDir = ResourceUtils.getFile("classpath:stopwords.txt").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader stopwordsReader = new BufferedReader(new FileReader(dataDir));
        Analyzer a1 = new StandardAnalyzer(stopwordsReader);
        String result = new String();
        result = AnalyzerUtils.displayToken(preresult, a1);//去停用词后的结果
        System.out.println(result);
        return result;
    }

//词干还原
       public ArrayList<String>PorterStem(String result) throws IOException {
        ArrayList<String> porter_Result = new ArrayList<String>();
           String dataDir = null;
           try {
               dataDir = ResourceUtils.getFile("classpath:stopwords.txt").getPath();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
        BufferedReader stopwordsReader = new BufferedReader(new FileReader(dataDir));
        Analyzer a1 = new StandardAnalyzer(stopwordsReader);
        Reader reader = new StringReader(result);
        TokenStream ts = a1.tokenStream(null, reader);
        ts = new PorterStemFilter(ts);
        OffsetAttribute offsetAttribute = ts.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);
        ts.reset();//必须的
        while (ts.incrementToken()) {
            int startOffset = offsetAttribute.startOffset();
            int endOffset = offsetAttribute.endOffset();
            String term = charTermAttribute.toString();
            porter_Result.add(term);
            System.out.println(term);
        }
        System.out.println(porter_Result.size());
        ts.end();
        ts.close();
        return porter_Result;
    }
}
