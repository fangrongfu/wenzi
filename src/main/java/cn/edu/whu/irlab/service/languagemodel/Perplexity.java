package cn.edu.whu.irlab.service.languagemodel;

import java.io.IOException;
import java.util.*;

public class Perplexity {
    HashMap<String,String> termsMap = new HashMap<String, String>();//建立一个map存放terms和对应的总概率
    public Perplexity(){
        this.termsMap=new HashMap<>();
    }
    public HashMap<String, String> getTermsMap() {
        return termsMap;
    }
    Map perpleMap=new HashMap();
    public Map getPerpleMap() {
        return perpleMap;
    }
//构建map
    public void buildTermsMap(TreeMap<Integer, ArrayList<String>> documents) {
        Map<String, Integer> map = new HashMap<String, Integer>();//建立一个map存放terms
        Iterator<Integer> it = documents.keySet().iterator();
        ArrayList printList = new ArrayList();
        while (it.hasNext()) {
            printList.add(documents.get(it.next()));
        }
        StringBuffer docContent = new StringBuffer();
        String st=new String();
        for (int i = 0; i < printList.size(); i++) {
            docContent.append(printList.get(i));
            docContent.append(" ");
        }
        st = docContent.toString();
        st=st.replaceAll(","," ");//去掉逗号
        int count = 0;//单词总数
        //将字符串用空格分隔
        String[] ss = st.split("\\s+");
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
            DocTerm dt = new DocTerm();
            dt.setTerm(key);
            int i = 0;
            double a = map.get(key);
            double b = count;
            i++;
            rate = String.valueOf(a / b);
            dt.setRate(rate);
            String key1 = key + "--->" + "["  + a / b + "]";
            key1 += "\r\n";
            termsMap.put(key,rate);//将结果存入map
        }
    }
//对数据进行平滑
    public Map buildPerpleMap(Double λ,Map resultMap,TreeMap<Integer, ArrayList<String>> documents) throws IOException {
        //平滑
        HashMap perpleMap = new HashMap();//用于存放平滑后的数据
        List articles = new ArrayList();//存放文档ID
        Iterator it = documents.entrySet().iterator();
        for (Map.Entry entry : documents.entrySet()) {
//将原来MAP的KEY加入list 里面
            articles.add(entry.getKey());
        }
        Iterator itr = resultMap.entrySet().iterator();
        List list = new ArrayList();//存放termsMap里的terms
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            list.add(entry.getKey());
        }
        for (int i = 0; i < list.size(); i++) {
            mergerList(λ,(String) list.get(i),resultMap,termsMap,articles);
        }
//验证
      /*  Iterator it1 = resultMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry entry = (Map.Entry) it1.next();
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }*/
        return resultMap;

    }

    public void mergerList(Double λ,String term,Map resultMap,Map termsMap,List ID ) throws IOException {
        List docIdList =ID;
        List objList = (List)resultMap.get(term);//对象数组
        List newList = new ArrayList();//存放新生成的DocTerm

        for (int i = 0; i < docIdList.size(); i++) {
            Integer docId = (Integer) docIdList.get(i);
            boolean find = false;
            DocTerm newTerm = new DocTerm();

            for (int j = 0; j < objList.size(); j++) {
                DocTerm term1 = (DocTerm) objList.get(j);
                if (term1.getDocId().equals(docId)) {
                    newTerm = term1;
                    find = true;
                    String rate = (String) termsMap.get(term);
                    Double m = new Double(rate);
                    Double n = new Double(term1.getRate());
                    Double tmp = (1 - λ) * n + λ * m;//平滑
                    term1.setRate("" + tmp);//将平滑后的概率存入
                    //计算rate
                    newList.add(term1);
                    perpleMap.put(term,newList);
                    break; //跳出此循环
                }
            }
            if (find == false) {
                newTerm.setDocId(docId);
                String rate = (String) termsMap.get(term);//dt.getRate();
                Double m = new Double(rate);
                Double tmp = λ * m;
                newTerm.setRate("" + tmp);//将平滑后的概率存入
                newTerm.setTerm(term);
            }

            newList.add(newTerm);
        }
        perpleMap.put(term, newList);
    }
}
