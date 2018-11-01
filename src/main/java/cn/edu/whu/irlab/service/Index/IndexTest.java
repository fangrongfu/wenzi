package cn.edu.whu.irlab.service.Index;

import cn.edu.whu.irlab.service.languagemodel.DataProcessing;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class IndexTest {
    public static void main(String[] args) throws IOException {
        ChIndex chIndex = new ChIndex();
        boolean isChinese = true;
        //  TermMap br = new TermMap();
        //  Index index=new Index();
        String dataDir = null;
        dataDir = ResourceUtils.getFile("classpath:doc_中文").getPath();
        chIndex.fetchDocuments(dataDir,isChinese);
        TreeMap<Integer, ArrayList<String>> documents = chIndex.getDocuments();
        chIndex.buildResultMap(documents);
        TreeMap resultMap=chIndex.getResultMap();
        chIndex.buildIdMap(documents);
        TreeMap idMap = chIndex.getIdMap();
        chIndex.buildIdfMap(idMap);
        chIndex.writeMap();
        EnIndex enIndex = new EnIndex();
        dataDir = ResourceUtils.getFile("classpath:doc_英文").getPath();
        enIndex.fetchDocuments(dataDir,isChinese);
        TreeMap<Integer, ArrayList<String>> porterDocuments = enIndex.getPorterDocuments();
        enIndex.buildResultMap(porterDocuments);
        resultMap=enIndex.getResultMap();
        enIndex.buildIdMap(porterDocuments);
        idMap = chIndex.getIdMap();
        enIndex.buildIdfMap(idMap);
        enIndex.writeMap();
    }
}
