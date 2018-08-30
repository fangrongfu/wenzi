package cn.edu.whu.irlab.controller;

import cn.edu.whu.irlab.service.languagemodel.*;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-08-29 10:29
 * @desc 语言检索模型的交互层
 **/
@Controller
@RequestMapping(value = "/language")
public class LanguageModelController {

    @RequestMapping(value = "/index")
    @ResponseBody
    public ModelMap languageIndexController(){
        ModelMap modelMap = new ModelMap();
        Unigram unigram = new Unigram();
        DataProcessing dataProcessing = new DataProcessing();
        boolean isChinese = true;
        String dataDir = null;
        try {
            dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //String dataDir = this.getServletContext().getRealPath("/data");
        try {
            dataProcessing.fetchDocuments(dataDir, isChinese);//处理文件//
        } catch (IOException e) {
            e.printStackTrace();
        }
        TreeMap<Integer, ArrayList<String>> documents = dataProcessing.getDocuments();
        unigram.buildResultMap(documents);//建立倒排索引
        TreeMap resultMap = unigram.getResultMap();
        Iterator<String> it = resultMap.keySet().iterator();
        String term = null;
        ArrayList posting = null;
        StringBuffer docIDs = new StringBuffer();
        while (it.hasNext()) {
            term = it.next();
            posting = (ArrayList) resultMap.get(term);
            docIDs.append(term + "--->[");
            for (int i = 0; i < posting.size(); i++) {
                DocTerm aa = (DocTerm) posting.get(i);
                docIDs.append(aa.docId + "--" + aa.rate);
                docIDs.append(", ");
            }
            docIDs.append(']'+"<br>");
        }
        return modelMap.addAttribute("results",docIDs);
    }

    @RequestMapping(value = "/search")
    @ResponseBody
    public ModelMap languageSearchController(@RequestParam(value = "λ",required = false) String λ,@RequestParam(value = "query",required = false)String query){
        //System.out.println(λ);
        //System.out.println(query);
        ArrayList ResultIDs = null;
        ArrayList ResultRates = null;
        TreeMap<String, String> map1 = new TreeMap<String, String>();
        TreeMap<String, Double> map2 = new TreeMap<String, Double>();
        Map<String,ArrayList> map = new HashMap<>();
        ModelMap modelMap = new ModelMap();
        Perplexity perplexity=new Perplexity();
        String dataDir = null;
        try {
            dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Double aDouble = Double.valueOf(λ);//平滑系数
        //System.out.println(aDouble);
        //TreeMap<String, String> results = new TreeMap<String, String>();
        boolean isChinese = true;
        Unigram unigram = new Unigram();
        Retrival retrival=new Retrival();
        DataProcessing document = new DataProcessing();
        try {
            document.fetchDocuments(dataDir, isChinese);//处理文件//
        } catch (IOException e) {
            e.printStackTrace();
        }
        TreeMap<Integer, ArrayList<String>>documents =document.getDocuments();
        HashMap<Integer, String> docID_Name = document.getDocID_Name();
        HashMap<Integer, String> docID_Content = document.getDocID_Contents();
        unigram.buildResultMap(documents);//建立倒排索引
        TreeMap resultMap = unigram.getResultMap();
        perplexity.buildTermsMap(documents);
        HashMap<String, String> termsMap = perplexity.getTermsMap();
        ArrayList terms= null;
        try {
            terms = retrival.querySolve(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            perplexity.buildPerpleMap(aDouble,resultMap,documents);//建立平滑索引
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map perpleMap=perplexity.getPerpleMap();//获得平滑索引
        try {
            map = retrival.calcRate(terms,documents,resultMap,perpleMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null==map) {//没有结果
            modelMap.addAttribute("results",0);
            return modelMap;
        }else {
            ResultIDs =  map.get("results");
            ResultRates = map.get("resultsRate");
            for (int i = 0; i < ResultIDs.size(); i++) {
                int id = (int) ResultIDs.get(i);
                map1.put(docID_Name.get(id), docID_Content.get(id));
                map2.put(docID_Name.get(id), (Double) ResultRates.get(i));
            }
            if(map1.isEmpty()) {//没有结果
                modelMap.addAttribute("results",0);
                return modelMap;
            }else {
                JSONArray jsonArray =JSONArray.fromObject(map1);
                JSONArray jsonArray1 =JSONArray.fromObject(map2);
                //System.out.println("jsonArray是： "+jsonArray);
                modelMap.addAttribute("contents",jsonArray);
                modelMap.addAttribute("rate",jsonArray1);
                return modelMap;
            }
        }
    }
}
