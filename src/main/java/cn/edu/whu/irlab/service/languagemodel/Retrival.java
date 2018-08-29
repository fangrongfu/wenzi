package cn.edu.whu.irlab.service.languagemodel;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;

public class Retrival {
    //对前端的输入进行预处理（分词、去标点）
    public ArrayList<String> querySolve(String data) throws IOException {
        System.out.println("开始提取文档词汇");
        // 读取停用词
        File file1 = new File(ResourceUtils.getFile("classpath:StopWordTable.txt").getPath());
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "GBK"));//构造一个BufferedReader类来读取totalstop文件
        String string1 = null;
        ArrayList<String> stopword = new ArrayList();
        while ((string1 = br1.readLine()) != null) {//使用readLine方法，一次读一行 读取停用词
            stopword.add(string1);
        }
        br1.close();
        String docName = null;
        String seg = null;
        String noPunSeg = null;
        ArrayList<String> terms = new ArrayList<String>();//处理后的terms
        //System.out.println("使用Hanlp之后");
        HanLP.Config.ShowTermNature = false;//关闭词性显示
        List<Term> termList = StandardTokenizer.segment(data);
        List stri = termList;//将分词结果存入数组
        String strResult = "";
        for (int i = 0; i < stri.size(); i++) {
            strResult += stri.get(i) + " ";
        }
        String splits = strResult.replaceAll("\\pP", "");// 去除标点符号
        // noPunSeg = splits;
        String[] sWords = splits.split(" ");
        ArrayList<String> TermList = new ArrayList();
        //StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sWords.length; i++) {
            TermList.add(sWords[i]);
        }
        TermList.removeAll(stopword);//去停用词
        terms = TermList;
        return terms;
    }

    //对前台的输入进行检索
    public static ArrayList calcRate(ArrayList selectDicts, TreeMap<Integer, ArrayList<String>> documents, Map resultMap,Map perpleMap) throws IOException {
        // List selectDicts = new ArrayList();
        // List articles = new ArrayList();
        ArrayList results = new ArrayList();
        Perplexity p = new Perplexity();
        //Map perpleMap = p.buildPerpleMap(λ);
        //selectDicts = Arrays.asList(queryTerms);
        List articles = new ArrayList();//存放文档ID
        Iterator it = documents.entrySet().iterator();
        for (Map.Entry entry : documents.entrySet()) {
            articles.add(entry.getKey());
            // System.out.println(entry.getKey());
        }
        List term = new ArrayList();//遍历文档中的词
        Iterator itr = resultMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry1 = (Map.Entry) itr.next();
            term.add(entry1.getKey());
        }
        Map finalMap = new HashMap();
        List articlelist=new ArrayList();
        for (int j = 0; j < selectDicts.size(); j++) {
            String dict = (String) selectDicts.get(j);
            if (resultMap.containsKey(dict)) {//判断查询的词在文档集中是否存在
                List objList = (List) resultMap.get(dict);
                for (int k = 0; k < objList.size(); k++) {//根据词取出来的概率做乘法
                    DocTerm dt = (DocTerm) objList.get(k);
                    articlelist.add(dt.getDocId());

                }
            }
        }

        //遍历文章编号
        for (int i = 0; i < articlelist.size(); i++) {
            Integer articleNum = (Integer) articlelist.get(i);
            if (articleNum <= articles.size()) {
                //在文章中遍历选中的词
                for (int j = 0; j < selectDicts.size(); j++) {
                    String dict = (String) selectDicts.get(j);
                    if (perpleMap.containsKey(dict)) {//判断查询的词在文档集中是否存在
                        List objList = (List) perpleMap.get(dict);
                        for (int k = 0; k < objList.size(); k++) {//根据词取出来的概率做乘法
                            DocTerm dt = (DocTerm) objList.get(k);
                            if (dt.getDocId().equals(articleNum)) {
                                double rate = Double.parseDouble(dt.getRate());
                                //如果找到概率做乘法
                                if (null != finalMap.get(articleNum)) {
                                    double tmp = (double) finalMap.get(articleNum);
                                    finalMap.put(articleNum, tmp * rate);
                                } else {
                                    finalMap.put(articleNum, rate);
                                }
                            }
                        }
                    }
                }
            } else return null;
        }

// 然后通过比较器来实现对结果的排序,将结果按降序输出
        List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(finalMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                // return 0;  // 降序
                return o2.getValue().compareTo(o1.getValue()); // 降序
                // return o1.getValue().compareTo(o2.getValue()); // 升序
            }
        });
        for (Map.Entry<Integer, Double> mapping : list) {
            //System.out.println(mapping.getKey() + ":" + mapping.getValue());
            results.addAll(Collections.singleton(mapping.getKey()));
        }
        // System.out.println("查询完毕2。");
        //  System.out.println(results);
        if (results.size() > 0) {
            return results;
        }else return null;
    }
}