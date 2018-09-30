package cn.edu.whu.irlab.service.languagemodel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CollationKey;
import java.text.Collator;
import java.util.*;

public class Unigram {
    static MyComparator comparator = new MyComparator();
    TreeMap resultMap = new TreeMap() ;//建立一个map存放每篇文档的terms、docID和对应的概率
    private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(Locale.CHINA);
    HashMap tfMap = new HashMap();

public Unigram(){
    this.resultMap=new TreeMap();
}

public TreeMap<String, String> getResultMap() {
        return resultMap;
    }

    public HashMap getTfMap(){
        return tfMap;
    }

    //用于计算词的概率
    public void buildResultMap(TreeMap<Integer, ArrayList<String>> documents) {
        MyComparator comparator = new MyComparator();
        resultMap = new TreeMap<String, ArrayList<Integer>>((Comparator<? super String>) comparator);//TreeMap按字典顺序排序

        Iterator<Integer> docIDs = documents.keySet().iterator();
        Integer docID = null;// 文档ID 1
        ArrayList<String> doc = null; // 文档1
        ArrayList printList = new ArrayList();//所有文档内容
        ArrayList<String> terms = new ArrayList<String>();

        while (docIDs.hasNext()) {
            Map<String, Integer> map = new HashMap<String, Integer>();//建立一个map存放terms
            docID = docIDs.next();
            doc = documents.get(docID);
             terms = documents.get(docID);
            StringBuffer docContent = new StringBuffer();
            for (int i = 0; i < terms.size(); i++) {
                docContent.append(terms.get(i));
                docContent.append(" ");
            }
            String term=docContent.toString();
                //将字符串用空格分隔
                String[] ss = term.split("\\s+");
            int count = 0;//单词总数
                for (String s : ss) {
                    if (map.containsKey(s)) {
                        map.put(s, map.get(s) + 1);
                    } else {
                        map.put(s, 1);
                    }
                    count = count + 1;
                }
                Set<String> keys = map.keySet();
                int c = 0;
                String rate = null;
                for (String key : keys) {
                    int j = 0;
                    double a = map.get(key);
                    int tf = map.get(key);
                    double b = count;
                    tfMap.put(key,tf);
                    j++;
                    rate = String.valueOf(a / b);
                    String key1 = key + "--->" + "[" + docID + "--" + a / b + "]";
                    key1 += "\r\n";
                    if (resultMap.containsKey(key)){
                        List dictlist = (List) resultMap.get(key);
                        DocTerm dt = new DocTerm();// 一个词的文档ID、概率
                        dt.setDocId(docID);
                        dt.setTerm(key);
                        dt.setRate(rate);
                        dictlist.add(dt);
                    } else {
                        DocTerm dt = new DocTerm();// 一个词的文档ID、概率
                        dt.setDocId(docID);
                        dt.setTerm(key);
                        dt.setRate(rate);
                        List dictlist = new ArrayList();
                        dictlist.add(dt);
                        resultMap.put(key, dictlist);
                    }
                }
            }
        }


    //将索引写入文件inverstedIndex.txt
    public  void writeIndex(){
        try {
            FileWriter fw = new FileWriter(new File("results//inverstedIndex.txt"));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("terms\tposting lists");
            bw.newLine();
            Iterator<String> it = resultMap.keySet().iterator();
            String term = null;
            ArrayList posting = null;
            while(it.hasNext()) {
                term = it.next();
                posting = (ArrayList) resultMap.get(term);
                StringBuffer docIDs = new StringBuffer();
                docIDs.append(term+"--->[");
                for (int i = 0; i < posting.size(); i++) {
                    DocTerm aa=(DocTerm)posting.get(i);
                    docIDs.append(aa.docId+" "+aa.rate);
                    docIDs.append(", ");
                }
                docIDs.append(']');
                bw.write(docIDs.toString());
                bw.newLine();
            }
            bw.flush();
            fw.close();
            bw.close();
            System.out.println("索引存储完毕");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 按字典顺序排序
     * @author Administrator
     *
     */
    public static class MyComparator implements Comparator {

        //Collator 类执行区分语言环境的 String 比较。使用此类可为自然语言文本构建搜索和排序例程。
        private Collator collator=Collator.getInstance();



        /*o1和o2是含中文的字符串(map中的key),
         * 现在想让o1和o2按中文拼音的先后顺序排序，则根据先后关系分别返回:
         * 负整数、0或正整数
         */
        @Override
        public int compare(Object o1, Object o2) {
            CollationKey key1=collator.getCollationKey(o1.toString());
            CollationKey key2=collator.getCollationKey(o2.toString());
            return key1.compareTo(key2);
        }


    }
}
