package cn.edu.whu.irlab.service.vectormodel;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.whu.irlab.util.ReadStopWords.readStopWords;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-09-08 0:33
 * @desc 查询业务类
 **/
public class Query {
    private String query;
    private Map<String, Double> wordTf = new HashMap<>();
    private double[] queryVector;
    private Map<String,Double> wordVector=new HashMap<>();


    public Query(String query) {
        this.query = query;
    }

    public String getQuery()//输出源文件
    {
        return query;
    }

    public List<Term> segment(){

        HanLP.Config.ShowTermNature = false;//停用词性显示
        List<Term> termList = HanLP.segment(query);
        return termList;

    }//标准分词

    public List<Term> nonPunctuation()//去标点
    {
        List<Term> termListQuery = segment();
        String query=termListQuery.toString();
        query=query.replaceAll("[\\pP\\n\\t\\s]", "");
        query=query.replaceAll("　　", "");
        HanLP.Config.ShowTermNature = false;//停用词性显示
        termListQuery = HanLP.segment(query);
        return termListQuery;
    }//去标点

    public ArrayList removeStopWords()//去停用词
    {

        ArrayList<String> stopWordAL  = readStopWords();
        ArrayList<String> TermList = new ArrayList();
        List<Term> termList =this.nonPunctuation();
        for (int i = 0; i < termList.size(); i++) {
            TermList.add(termList.get(i).toString());
        }
        TermList.removeAll(stopWordAL);
        return TermList;
    }

    public void setWordTf(){
        ArrayList<String> termList = removeStopWords();
        for (String string : termList) {
            if (!wordTf.containsKey(string)) {
                wordTf.put(string, 1.0);
            } else {
                double tf = 1 + Math.log(wordTf.get(string) + 1);
                wordTf.put(string, tf);
            }
        }
    }//计算tf,tf=1+log(tf)

    public Map<String, Double> getWordTf() {
        setWordTf();
        return wordTf;
    }

    public void setQueryVector(Map<String, Double> wordIdf){
        int idfLength = wordIdf.size();
        double[] vector = new double[idfLength + 1];
        int num = 0;
        for (String in : wordIdf.keySet()) {
            if (wordTf.containsKey(in)) {
                double tf = wordTf.get(in);
                double idf = wordIdf.get(in);
                vector[num] = tf * idf;
            } else {
                num++;
            }
        }
        double sum = 0;
        for (double j : vector) {
            sum += Math.pow(j, 2);
        }
        sum = Math.pow(sum, 0.5);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / sum;
        }
    }//求向量

    public double[] getQueryVector() throws FileNotFoundException {
        DocSet docSet=new DocSet(ResourceUtils.getFile("classpath:dataset").getPath());
        setQueryVector(docSet.getWordIdf());
        return queryVector;
    }

    public  void setWordVector(Map<String, Double> wordIdf){

        int idfLength = wordIdf.size();
        double[] vector = new double[idfLength + 1];
        int num = 0;
        for (String in : wordIdf.keySet()) {
            if(wordTf.containsKey(in)){
                double tf = wordTf.get(in);
                double idf = wordIdf.get(in);
                if((tf!=0)&&(idf!=0))
                    vector[num] = tf * idf;
                wordVector.put(in,vector[num]);
            }else {
                num++;
            }
        }
        double sum=0;
        for (String in:wordVector.keySet()) {
            sum+=Math.pow(wordVector.get(in),2);
        }
        sum=Math.pow(sum,0.5);
        for (String in:wordVector.keySet()) {
            double value=wordVector.get(in)/sum;
            wordVector.put(in,value);
        }
        System.out.println(wordVector);
    }

    public Map<String,Double> getWordVector() throws FileNotFoundException {
        DocSet docSet=new DocSet(ResourceUtils.getFile("classpath:dataset").getPath());
        Map<String, Double> wordIdf=docSet.getWordIdf();
        setWordVector(wordIdf);
        System.out.println(wordVector);
        return wordVector;
    }

    public double[] countQueryVector(Map<String, Double> wordIdf){

        double[] queryVector;
        int idfLength = wordIdf.size();
        queryVector = new double[idfLength + 1];
        int num = 0;
        for (String in : wordIdf.keySet()) {
            if (wordTf.containsKey(in)) {
                double tf = wordTf.get(in);
                double idf = wordIdf.get(in);
                queryVector[num] = tf * idf;
            } else {
                num++;
            }
        }
        double sum = 0;
        for (double j : queryVector) {
            sum += Math.pow(j, 2);
        }
        sum = Math.pow(sum, 0.5);
        for (int i = 0; i < queryVector.length; i++) {
            queryVector[i] = queryVector[i] / sum;
        }
        return queryVector;
    }//求向量

}
