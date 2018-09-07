package cn.edu.whu.irlab.service.vectormodel;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.whu.irlab.util.ReadDoc.readDoc;
import static cn.edu.whu.irlab.util.ReadStopWords.readStopWords;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-09-08 0:30
 * @desc 创建索引用的类
 **/
public class Document {

    private String docPath;
    private int docID;
    private Map<String, Double> wordTf = new HashMap<>();
    private double[] docVector;


    public Document(String docPath, int docID) {
        this.docPath = docPath;
        this.docID = docID;
        setWordTf();

    }

    public int getDocID(){
        return docID;
    }

    public void printDocument()//输出源文件
    {


        String doc = readDoc(docPath);
        System.out.println("文章" + docID + "/n");
        System.out.print(doc);
        System.out.println("\n");
    }

    public List<Term> segment(){

        String docs=readDoc(docPath);
        HanLP.Config.ShowTermNature = false;//停用词性显示
        List<Term> termList = HanLP.segment(docs);
        return termList;

    }//标准分词

    public List<Term> nonPunctuation()//去标点
    {
        List<Term> termListDoc = segment();
        String doc=termListDoc.toString();
        doc=doc.replaceAll("[\\pP\\n\\t\\s]", "");
        doc=doc.replaceAll("　　", "");
        HanLP.Config.ShowTermNature = false;//停用词性显示
        termListDoc = HanLP.segment(doc);
        return termListDoc;
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
    }

    public Map<String, Double> getWordTf() {
        setWordTf();
        return wordTf;
    }//计算tf,tf=1+log(tf)

    public void setDocVector(Map<String, Double> wordIdf){
        int idfLength = wordIdf.size();
        docVector=new double[idfLength+1];
        int num = 0;
        for (String in : wordIdf.keySet()) {
            if (wordTf.containsKey(in)) {
                double tf = wordTf.get(in);
                double idf = wordIdf.get(in);
                docVector[num] = tf * idf;
            } else {
                num++;
            }
        }
        double sum = 0;
        for (double j : docVector) {
            sum += Math.pow(j, 2);
        }
        sum = Math.pow(sum, 0.5);
        for (int i = 0; i < docVector.length; i++) {
            docVector[i] = docVector[i] / sum;
        }
    }

    public double[] getDocVector(Map<String, Double> wordIdf)  {
        setDocVector(wordIdf);
        return docVector;
    }
}
