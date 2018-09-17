package cn.edu.whu.irlab.service.vectormodel;

import cn.edu.whu.irlab.util.ReadDoc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-09-08 0:29
 * @desc 创建索引用的类
 **/
public class DocSet {
    private String folderPath;
    private Map<String, ArrayList<Integer>> indexMap=new HashMap<>();
    private Map<String, Double> wordIdf=new HashMap<>();
    private Map<Integer,Map<String, Double>> docTfs=new HashMap<>();
    private Map<Integer, List> docVectors=new HashMap<>();


    public DocSet(String folderPath){
        this.folderPath=folderPath;
        setIndexMap();
        setWordIdf();
        setDocTfs();
        setDocVectors();
    }

    public String getFolderPath(){
        return folderPath;
    }

    /**
     * 建立倒排索引
     * @return
     */


    public void setIndexMap(){
        File file = new File(folderPath);
        if (file.exists())
        {
            File[] files = file.listFiles();
            int ID=10;
            for (File file2 : files) {
                String FilePath=file2.getAbsolutePath();
                Document doc=new Document(FilePath,ID);
                //doc.countTf();
                ArrayList<String> sw=doc.removeStopWords();
                for (String string : sw) {
                    ArrayList<Integer> list=new ArrayList<>();
                    if (!indexMap.containsKey(string)) {
                        list.add(ID);
                        indexMap.put(string, list);
                    }else {
                        list=indexMap.get(string);
                        if (!list.contains(ID)) {
                            list.add(ID);
                        }
                    }
                }
                ID++;
            }
        }
        try{
            FileWriter inIn = new FileWriter(new File("intertedIndex.txt"));
            inIn.write(indexMap.toString());
            inIn.close();
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }//建立倒排索引

    public Map<String, ArrayList<Integer>> getIndexMap(){
        return indexMap;
    }

    public void setWordIdf(){
        File file = new File(folderPath);
        if (file.exists())
        {
            File[] files = file.listFiles();
            int ID=10;
            for (File file2 : files) {
                String FilePath=file2.getAbsolutePath();
                Document doc=new Document(FilePath,ID);
                ArrayList<String> sw=doc.removeStopWords();
                for (String string : sw) {
                    double count;
                    if (!wordIdf.containsKey(string)) {
                        wordIdf.put(string, 1.0);
                    }else {
                        count=wordIdf.get(string)+1;
                        wordIdf.put(string, count);
                    }
                }
                ID++;
            }
        }
        int N=wordIdf.size();
        double idf;
        for (String key:wordIdf.keySet()) {
            idf=Math.log(N/(wordIdf.get(key)));
            wordIdf.put(key,idf);
        }

        try{
            FileWriter inIn = new FileWriter(new File("idfIndex.txt"));
            inIn.write(wordIdf.toString());
            inIn.close();
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }//求Idf,idf=log(N/df)

    public Map<String, Double> getWordIdf() {
        return wordIdf;
    }

    public Map<Integer,Map<String, Double>> getDocTfs(){
        return docTfs;
    }

    public void setDocTfs(){

        File file = new File(folderPath);

        if (file.exists())
        {
            File[] files = file.listFiles();
            int ID=10;
            for (File file2 : files) {
                String FilePath=file2.getAbsolutePath();
                Document doc=new Document(FilePath,ID);
                Map<String, Double> wordTf=doc.getWordTf();
                docTfs.put(ID,wordTf);
                ID++;
            }
        }
    }

    public Map<Integer,List> getDocVectors(){
        return docVectors;
    }

    public void setDocVectors(){
        File file = new File(folderPath);
        Map<Integer,List> docVectors=new HashMap<>();
        if (file.exists())
        {
            File[] files = file.listFiles();
            int ID=10;
            for (File file2 : files) {
                String FilePath=file2.getAbsolutePath();
                Document doc=new Document(FilePath,ID);
                double[] vector=doc.getDocVector(wordIdf);
                List<Double> vector_List=new ArrayList<>();
                for (int i = 0; i <vector.length ; i++) {
                    vector_List.add(vector[i]);
                }
                docVectors.put(ID,vector_List);
                ID++;
            }
        }
    }

    public double countSimilarity( double[] queryVector,Document doc){
        double[] docVector=doc.getDocVector(wordIdf);
        double similarity=0;
        for (int i = 0; i <docVector.length ; i++) {
            if((docVector[i]!=0)&&(queryVector[i]!=0))
                similarity+=docVector[i]*queryVector[i];
        }
        return similarity;
    }//计算查询与单个文档的相似度

    public Map<String,Double>  Similarities(double[] queryVector) {

        File file = new File(folderPath);
        Map<Integer,Double> id_Similarity=new HashMap<>();
        Map<String,Double> FileName_Similarity=new HashMap<>();

        if (file.exists())
        {
            File[] files = file.listFiles();
            int ID=10;
            double similarity;
            for (File file2 : files) {
                String FilePath=file2.getAbsolutePath();
                String FileName=file2.getName();
                Document doc=new Document(FilePath,ID);
                similarity=countSimilarity(queryVector,doc);
                if(similarity!=0.0){
                    id_Similarity.put(ID,similarity);
                    FileName_Similarity.put(FileName,similarity);
                }
                ID++;
            }
        }

        return FileName_Similarity;
    }//查询与所有文档的相似度

    public List<Map.Entry<String,Double>> sortSimilarities (double[] queryVector){
        Map<String,Double> FileName_Similarity=Similarities(queryVector);
        //将map.entrySet()转换成list
        List<Map.Entry<String, Double>> list = new ArrayList<>(FileName_Similarity.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                //return o1.getValue().compareTo(o2.getValue());
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    public TreeMap<String, String> result(double[] queryVector){
        List<Map.Entry<String,Double>> list=sortSimilarities(queryVector);
        TreeMap<String, String> fileName_value= new TreeMap<String, String>();

        File file = null;
        try {
            file = new File(ResourceUtils.getFile("classpath:dataset").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file.exists())
        {
            File[] files = file.listFiles();
            for (File file2 : files) {
                for (Map.Entry<String,Double> mapping:list) {
                    if(mapping.getKey().equals(file2.getName())){
                        fileName_value.put(file2.getName(), ReadDoc.readDoc(file2.getPath()));
                    }
                }
            }
        }
        return fileName_value;

    }
}
