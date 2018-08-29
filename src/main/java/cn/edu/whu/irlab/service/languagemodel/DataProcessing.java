package cn.edu.whu.irlab.service.languagemodel;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
//import static languagemodel.Index.Writestring;


public class DataProcessing {
    private TreeMap<Integer, ArrayList<String>> documents;//处理后的文档ID和文档中的词
    private HashMap<Integer, String> docID_Name; //文档ID和文档名
    private HashMap<Integer, String> docID_Contents; //文档ID和文档原始内容
    HashMap<String, String> segments = new HashMap<String, String>();//原始分词结果
    HashMap<String, String> noPunSegments = new HashMap<String, String>();// 去除标点后的分词结果
    HashMap termRate = new HashMap();//词、词所在文档ID和词在文档中的概率

    public HashMap<String, String> getSegments() {
        return segments;
    }


    public HashMap<String, String> getNoPunSegments() {
        return noPunSegments;
    }


    public HashMap getTermRate() {
        return termRate;
    }

    public DataProcessing() {
        // TODO Auto-generated constructor stub
        this.documents = new TreeMap<Integer, ArrayList<String>>();
        this.docID_Name = new HashMap<Integer, String>();
        this.docID_Contents = new HashMap<Integer, String>();
    }


    public TreeMap<Integer, ArrayList<String>> getDocuments() {
        return documents;
    }


    public HashMap<Integer, String> getDocID_Name() {
        return docID_Name;
    }


    public HashMap<Integer, String> getDocID_Contents() {
        return docID_Contents;
    }


    /**
     * 读取文档，依次为文档编写docID,提取文档中的词，存入this.docments
     *
     * @param docDir    存放文档的文件夹地址
     * @param isChinese 文本是否为中文 中文为true 英文为false
     *                  String initDir  NLPIR的lib文件夹路径
     */
    public void fetchDocuments(String docDir, boolean isChinese) throws IOException {
        //System.out.println("开始提取文档词汇");
        // 读取停用词
        File file1 = new File(ResourceUtils.getFile("classpath:stopwords.txt").getPath());
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "GBK"));//构造一个BufferedReader类来读取totalstop文件
        String string1 = null;
        ArrayList<String> stopword = new ArrayList();
        while ((string1 = br1.readLine()) != null) {//使用readLine方法，一次读一行 读取停用词
            stopword.add(string1);
        }
        br1.close();
        //HashSet<String> stopWords = Tokenize.getStopWords();
        File f1 = new File(docDir);
        File[] docs = f1.listFiles();
        int docID = 1;
        String docName = null;
        String seg = null;
        String noPunSeg = null;
        for (File doc : docs) {
            try {
                FileInputStream fin = new FileInputStream(doc);
                InputStreamReader ir = new InputStreamReader(fin, "GBK");
                BufferedReader br = new BufferedReader(ir);
                // 多少文本作为分词的输入，输入如果以一行一行的进行分词，那么换行后的词可能被切开
                // 考虑以标点符号作为一次读入的划分
                // 正则表达式
                ArrayList<String> terms = new ArrayList<String>();//处理后的terms
                String oneLine = null;
                StringBuffer contents = new StringBuffer(); //原始的文本内容
                // 中文分词
                if (isChinese) {
                    // 下面是以一篇文档中的所有文本进行分词的
                    StringBuffer str = new StringBuffer();
                    while (null != (oneLine = br.readLine())) {//读取一篇文档的内容
                        str.append(oneLine);
                        str.append("\n");
                    }
                    contents = str;
                    // 采用中文分词包Hanlp
                    //System.out.println("使用Hanlp前");
                    String data = new String(str);
                    //System.out.println("使用Hanlp之后");
                    HanLP.Config.ShowTermNature = false;//关闭词性显示
                    List<Term> termList = StandardTokenizer.segment(data);
                    List stri = termList;//将分词结果存入数组
                    String strResult = "";
                    for (int i = 0; i < stri.size(); i++) {
                        strResult += stri.get(i) + " ";
                    }
                    seg = strResult;
                    // HanLP.Config.ShowTermNature = false;
                    //带标点的分词结果

                    String splits = strResult.replaceAll("\\pP", "");// 去除标点符号
                    noPunSeg = splits;
                    String[] sWords = splits.split(" ");
                    ArrayList<String> TermList = new ArrayList();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < sWords.length; i++) {
                        TermList.add(sWords[i]);
                    }
                    TermList.removeAll(stopword);//去停用词
                    terms = TermList;

                    docName = doc.getName();//文档名
                    segments.put(docName, seg);
                    noPunSegments.put(docName, noPunSeg);
                    docID_Name.put(docID, docName);
                    docID_Contents.put(docID, contents.toString());
                    documents.put(docID, terms);
                    docID++;
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //  Writestring("term.txt", "term1.txt");

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
            // TODO Auto-generated catch block
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


