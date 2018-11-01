package cn.edu.whu.irlab.service.Index;

import cn.edu.whu.irlab.service.languagemodel.DocTerm;
import cn.edu.whu.irlab.service.languagemodel.Unigram;
import cn.edu.whu.irlab.util.ChProcess;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.util.ResourceUtils;
import cn.edu.whu.irlab.util.Process;

import java.io.*;
import java.text.Collator;
import java.util.*;

    /**
     * 建立中文索引
     */
public class ChIndex {
    private TreeMap<Integer, ArrayList<String>> documents;//处理后的文档ID和文档中的词
    private TreeMap<Integer, ArrayList<String>> porterDocuments;//去停用词后的文档ID和文档中的词
    TreeMap<String, String> preDocument = new TreeMap<String, String>();//英文文档标准化的结果
    private HashMap<String, String> docID_Name; //文档ID和文档名
    private HashMap<Integer, String> docID_Contents; //文档ID和文档原始内容
    HashMap<String, String> segments = new HashMap<String, String>();//中文原始分词结果
    HashMap<String, String> noPunSegments = new HashMap<String, String>();// 中文去除标点后的分词结果
    HashMap termRate = new HashMap();//词、词所在文档ID和词在文档中的概率
    static Unigram.MyComparator comparator = new Unigram.MyComparator();
    TreeMap resultMap = new TreeMap();//建立一个map存放每篇文档的terms、docID和对应的概率
    private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(Locale.CHINA);
    // HashMap tfMap = new HashMap();//建立一个map存放每篇文档的terms和出现的频率
    TreeMap<String, ArrayList<Integer>> idMap = new TreeMap<String, ArrayList<Integer>>(comparator); //TreeMap按字典顺序排序=; // 建立一个map存放terms和所在的文档ID
    TreeMap idfMap = new TreeMap<String, ArrayList<Integer>>(comparator); // 建立一个map存放terms和包含它的文档数

    public TreeMap<String, String> getResultMap() { return resultMap; }
    public HashMap<String, String> getSegments() {
        return segments;
    }
    public HashMap<String, String> getNoPunSegments() {
        return noPunSegments;
    }
    public HashMap getTermRate() {
        return termRate;
    }
    public TreeMap<String, ArrayList<Integer>> getIdMap() {
        return idMap;
    }
    public TreeMap<String, ArrayList<Integer>> getIdfMap() { return idfMap; }
    public TreeMap<Integer, ArrayList<String>> getDocuments() {
        return documents;
    }
    public HashMap<String, String> getDocID_Name() {
        return docID_Name;
    }
    public HashMap<Integer, String> getDocID_Contents() { return docID_Contents; }
    public TreeMap<String, String> getPreDocument() {
            return preDocument;
        }
    public TreeMap<Integer, ArrayList<String>> getPorterDocuments() { return porterDocuments; }

    public ChIndex() {
        this.documents = new TreeMap<Integer, ArrayList<String>>();
        this.docID_Name = new HashMap<String, String>();
        this.docID_Contents = new HashMap<Integer, String>();
        this.porterDocuments = new TreeMap<Integer,ArrayList<String>>();
    }
    /**
     * 读取文档，依次为文档编写docID,提取文档中的词，存入this.docments
     *
     * @param docDir    存放文档的文件夹地址
     * @param isChinese 文本是否为中文 中文为true 英文为false
     *                  String initDir  NLPIR的lib文件夹路径
     */
    public void fetchDocuments(String docDir, boolean isChinese) throws IOException {
        File f1 = new File(docDir);
        File[] docs = f1.listFiles();
        int docID = 1;
        String docName = null;
        String seg = null;
        String noPunSeg = null;
        for (File doc : docs) {
            try {
                FileInputStream fin = new FileInputStream(doc);
                InputStreamReader ir = new InputStreamReader(fin, "UTF-8");
                BufferedReader br = new BufferedReader(ir);
                // 多少文本作为分词的输入，输入如果以一行一行的进行分词，那么换行后的词可能被切开
                // 考虑以标点符号作为一次读入的划分
                // 正则表达式
                ArrayList<String> terms = new ArrayList<String>();//处理后的terms
                String oneLine = null;
                StringBuffer contents = new StringBuffer(); //原始的文本内容
                // 中文预处理：分词、去标点、去停用词
                if (isChinese) {
                    // 下面是以一篇文档中的所有文本进行分词的
                    StringBuffer str = new StringBuffer();
                    while (null != (oneLine = br.readLine())) {//读取一篇文档的内容
                        str.append(oneLine);
                        str.append("\n");
                    }
                    contents = str;
                    String data = new String(str);
                    ChProcess chProcess = new ChProcess();
                    seg = chProcess.FenCi(data);//分词
                    noPunSeg = chProcess.PunRemove(seg);//去标点
                    terms = chProcess.StopwordsRemove(noPunSeg);//去停用词
                    String docId= String.valueOf(docID);
                    docName = doc.getName();//文档名
                    segments.put(docName, seg);
                    noPunSegments.put(docName, noPunSeg);
                    docID_Name.put(docId, docName);
                    docID_Contents.put(docID, contents.toString());
                    documents.put(docID, terms);
                    docID++;
                }
                else  {
                    // 下面是以一篇文档中的所有文本进行分词的
                    StringBuffer str = new StringBuffer();
                    while (null != (oneLine = br.readLine())) {//读取一篇文档的内容
                        str.append(oneLine);
                        str.append("\n");
                    }
                    contents = str;
                    String data = new String(str);
                    //文档预处理
                    Process process = new Process();
                    String preresult = process.Pre_Process(data);//文档标准化的结果
                    String result = process.StopwordsRemove(preresult);//去停用词的结果
                    ArrayList resultList = new ArrayList(Collections.singleton(result.split(" "))) ;
                    ArrayList<String> porter_Result = process.PorterStem(result);//porter词干提取结果

                    //存入preDocument map
                    docName = doc.getName();//文档名
                    String docId= String.valueOf(docID);
                    preDocument.put(docName, preresult);
                    docName = doc.getName();//文档名
                    segments.put(docName, seg);
                    noPunSegments.put(docName, noPunSeg);
                    docID_Name.put(docId, docName);
                    docID_Name.put(docId,docName);
                    docID_Contents.put(docID, contents.toString());
                    documents.put(docID, resultList);
                    porterDocuments.put(docID,porter_Result);
                    docID++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取某文件夹中的全部文档名
     *
     * @param docDir 文档文件夹路径
     * @return
     */
    public ArrayList<String> getFileNames(String docDir) {
        ArrayList<String> fileNames = new ArrayList<String>();
        File f = new File(docDir);
        File[] docs = f.listFiles();
        String docName = null;
        for (File doc : docs) {
            docName = doc.getName();
            fileNames.add(docName);
        }
        return fileNames;

    }
    /**
     * 用于计算词的概率
     */
    public void buildResultMap(TreeMap<Integer, ArrayList<String>> documents) {
        Unigram.MyComparator comparator = new Unigram.MyComparator();
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
            String term = docContent.toString();
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
                int k = 0;
                double a = map.get(key);
                int tf = map.get(key);
                //term在文档中的位置
                ArrayList locate=new ArrayList();;//term在文档中的位置
                for (j=0;j<ss.length;j++){
                    if (key.equals(ss[j])){
                        k=j+1;
                        locate.add(k);
                        //locate.add(",");
                    }
                    // j++;
                }
                //   System.out.println(locate);//打印list数组

                double b = count;
                //tfMap.put(key, tf);
                j++;
                rate = String.valueOf(a / b);
                String key1 = key + "--->" + "[" + docID + "--" + a / b + "]";
                key1 += "\r\n";
                if (resultMap.containsKey(key)) {
                    List dictlist = (List) resultMap.get(key);
                    DocTerm dt = new DocTerm();// 一个词的文档ID、概率
                    dt.setDocId(docID);
                    dt.setTerm(key);
                    dt.setRate(rate);
                    dt.setLocate(locate);
                    dt.setTf(tf);
                    dictlist.add(dt);
                } else {
                    DocTerm dt = new DocTerm();// 一个词的文档ID、概率
                    dt.setDocId(docID);
                    dt.setTerm(key);
                    dt.setRate(rate);
                    dt.setLocate(locate);
                    dt.setTf(tf);
                    List dictlist = new ArrayList();
                    dictlist.add(dt);
                    resultMap.put(key, dictlist);
                }
            }
        }
    }

    /**
     *建立idMap、idfMap
     * @param documents 所有处理好的文档
     */
    public void buildIdMap(TreeMap<Integer, ArrayList<String>> documents) {
        // 遍历所有文档数据
        Iterator<Integer> docIDs = documents.keySet().iterator();
        Integer docID = null;// 文档ID 1
        ArrayList<String> doc = null; // 文档1
        while (docIDs.hasNext()) {
            docID = docIDs.next();
            doc = documents.get(docID);
            String term = null; // 文档1中的词
            for (int i = 0; i < doc.size(); i++) {
                term = doc.get(i);
                if (!idMap.containsKey(term)) {// 如果map没有这个term
                    ArrayList<Integer> posting = new ArrayList<Integer>(); // 一个词的倒排记录
                    posting.add(docID);// 将当前的文档ID加入posting
                    idMap.put(term, posting);
                } else {// 如果倒排索引里有这个term
                    ArrayList<Integer> posting = idMap.get(term);// 获取map里该term对应的posting
                    if (!posting.contains(docID)) { // 判断该posting是否含有该文档ID，否则加入（去重操作）
                        posting.add(docID);
                        idMap.put(term, posting);// 更新map里面的值
                    }
                }
            }
        }
    }

    public void buildIdfMap(TreeMap<String, ArrayList<Integer>> idMap){
        Iterator<String> its = idMap.keySet().iterator();
        ArrayList<Integer> docIDs = null; // idMap里的value(文档ID集合)
        String term = null;//idMap里的词

        while (its.hasNext()){
            Integer idf=0;
            term = its.next();
            docIDs=idMap.get(term);
            for (int i = 0; i < docIDs.size(); i++){
                idf++;
            }
            idfMap.put(term,idf);
        }
    }
    /**
     * 将map中的数据以Jason格式输出
     */
    public void writeMap(){
        JSONObject json1 = JSONObject.fromObject(idMap);
        JSONObject json2 = JSONObject.fromObject(idfMap);
        JSONObject json3 = JSONObject.fromObject(resultMap);
        JSONObject json4 = JSONObject.fromObject(docID_Name);
        JSONArray json = new JSONArray();
        json.addAll(Collections.singleton(json1));
        json.addAll(Collections.singleton(json2));
        json.addAll(Collections.singleton(json3));
        //System.out.println("输出的结果是：" + json1);//词项+文档ID
        //System.out.println("输出的结果是：" + json2);
        //System.out.println("输出的结果是：" + json3);
        String result = json3.toString();
        //System.out.println(result);
        int i=0;
        for (i=0;i<json.size();i++){
            //System.out.println(json.get(i));
        }
        //System.out.println("输出的结果是：" + json);

        String fileName = "index.txt";
        File file = new File(fileName);
        try {
            file.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(json3.toString());
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        String fileName1 = "invertedindex.txt";
        File file1 = new File(fileName1);
        try {
            file1.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file1));
            output.write(json.toString());
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        String fileName2 = "term_id.txt";
        File file2 = new File(fileName2);
        try {
            file2.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file2));
            output.write(json1.toString());
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        String fileName3 = "term_idf.txt";
        File file3 = new File(fileName3);
        try {
            file3.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file3));
            output.write(json2.toString());
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        String fileName4 = "docId_Name.txt";
        File file4 = new File(fileName4);
        try {
            file4.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file4));
            output.write(json4.toString());
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将文档ID、文档名、处理好的文档内容写入文件processed.txt中
     */
    public void writeDocuments() {
        Iterator<Integer> it = documents.keySet().iterator();
        int docID = 0;
        String docName = null;
        ArrayList<String> terms = new ArrayList<String>();

        try {
            //System.out.println(Thread.currentThread().getContextClassLoader().getResource("")+"results//inverstedIndex.txt");
            FileWriter fw = new FileWriter(new File("results//processed.txt"));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("docID\t docName\t terms");
            bw.newLine();
            while (it.hasNext()) {
                docID = it.next();
                docName = docID_Name.get(docID);
                terms = documents.get(docID);
                StringBuffer docContent = new StringBuffer();
                docContent.append(docID + "\t ");
                docContent.append(docName + "\t ");
                for (int i = 0; i < terms.size(); i++) {
                    docContent.append(terms.get(i));
                    docContent.append(" ");
                }
                bw.write(docContent.toString());
                String sss=docContent.toString();
                bw.newLine();
            }
            bw.flush();
            fw.close();
            bw.close();
            System.out.println("处理后的文件存储完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String fileName, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true); // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件,false表示覆盖的方式写入
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
