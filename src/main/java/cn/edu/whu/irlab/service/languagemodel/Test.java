package cn.edu.whu.irlab.service.languagemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Test {
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub


        //测试读入文档
        boolean isChinese = true;
        //  TermMap br = new TermMap();
        //  Index index=new Index();
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.fetchDocuments("data", isChinese);//处理文件//
        dataProcessing.writeDocuments();//写入文件
        TreeMap<Integer, ArrayList<String>> documents = dataProcessing.getDocuments();
        HashMap<Integer, String> docID_Content = dataProcessing.getDocID_Contents();

        Unigram unigram=new Unigram();
        Perplexity perplexity=new Perplexity();
        unigram.buildResultMap(documents);//建立倒排索引
        perplexity.buildTermsMap(documents);//平滑
        HashMap termsMap=perplexity.getTermsMap();
        TreeMap resultMap=unigram.getResultMap();
        unigram.writeIndex();//倒排索引写入文件
        Double λ=0.5;//平滑系数
        perplexity.buildPerpleMap(λ,resultMap,documents);//建立平滑索引
        Map perpleMap=perplexity.getPerpleMap();//获得平滑索引
        String data="国家的春天";
        Retrival retrival=new Retrival();//检索
        ArrayList queryterms=retrival.querySolve(data);//预处理
        for (int i=0;i<queryterms.size();i++){//验证处理结果
            System.out.println(queryterms.get(i));}
        ArrayList<Integer>results=retrival.calcRate(queryterms,documents,resultMap,perpleMap);//检索
        System.out.println(results);

    }

}
