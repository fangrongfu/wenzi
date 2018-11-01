package cn.edu.whu.irlab.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;


    /**
     * 中文预处理：分词、去标点、去停用词
     */
public class ChProcess {
    /**
     * 分词
     */
    public String FenCi(String data) throws IOException {
        String seg = null;//带标点的分词结果
        ArrayList<String> termList = new ArrayList<String>();
        Analyzer analyzer = new SmartChineseAnalyzer(); // Lucene智能中文分析器
        TokenStream tokenStream = analyzer.tokenStream("", data);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            termList.add(term);
            System.out.println(term);
        }
        tokenStream.close();
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < termList.size(); i++) {
            sb.append(termList.get(i));
            sb.append(" ");
    }
    seg = String.valueOf(sb);
        return seg;
    }
    /**
     * 去标点
     */
    public String PunRemove(String seg){
        String noPunSeg = null;//去标点后的结果
        String splits = seg.replaceAll("\\pP|\\pS|\\pM|\\pN|\\pC", "");// 去除标点符号
        splits = splits.replaceAll("[a-zA-Z]","" );
        splits = splits.replaceAll(" "," " );
        splits = splits.replaceAll(" "," " );
        splits = splits.replaceAll("  "," " );
        splits = splits.replaceAll("ą"," " );
        splits = splits.replaceAll("ȁ"," " );
        splits = splits.replaceAll("É"," " );
        splits = splits.replaceAll("Ï"," " );
        noPunSeg = splits;
        return noPunSeg;
    }
    /**
     * 去停用词
     */
    public ArrayList<String> StopwordsRemove (String noPunSeg) throws IOException {
        ArrayList<String> terms = new ArrayList<String>();//去停用词后的terms
        //File file1 = new File(ResourceUtils.getFile("StopWordTable.txt").getPath());
        String dataDir = null;
        dataDir = ResourceUtils.getFile("classpath:StopWordTable.txt").getPath();
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(dataDir), "GBK"));//构造一个BufferedReader类来读取totalstop文件
        String string1 = null;
        ArrayList<String> stopword = new ArrayList();
        while ((string1 = br1.readLine()) != null) {//使用readLine方法，一次读一行 读取停用词
            stopword.add(string1);
        }
        br1.close();
        String[] sWords = noPunSeg.split(" ");
        ArrayList<String> TermList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sWords.length; i++) {
            String word=sWords[i];
            if (word==null||word.equals("")||word.equals(" ") || word.equals("  ")){}
            else {
                // System.out.println(word);
                word = word.trim();
                word = word.replace("  ", "");
                TermList.add(word);
            }
        }
        TermList.removeAll(stopword);//去停用词
        terms = TermList;
        return terms;
    }
}
