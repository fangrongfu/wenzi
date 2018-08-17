package cn.edu.whu.irlab.controller;

import cn.edu.whu.irlab.service.boolmodel.*;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-08-16 19:47
 * @desc 布尔模型的控制层
 **/
@Controller
@RequestMapping(value = "/bool")
public class BoolRetrivalController {

    @Autowired
    private BoolRetrivalModel boolRetrivalModel;
    @Autowired
    private Document document;
    @Autowired
    private MyComparator myComparator;
    @Autowired
    private NLPIR nlpir;
    @Autowired
    private NLPIRTest nlpirTest;
    @Autowired
    private Tokenize tokenize;

    @RequestMapping(value = "/getFileNames")
    @ResponseBody
    public ModelMap getFileNamesController() throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("getFileNames");
        //springboot获取resource下的文件的路径的方法
        String docDir = ResourceUtils.getFile("classpath:dataset").getPath();
        //第二种获取路径的方法
        //String docDir = this.getClass().getResource("/dataset").getPath();
        Document document = new Document();
        ArrayList<String> fileNames = document.getFileNames(docDir);
        JSONArray jsonArray =JSONArray.fromObject(fileNames);
        System.out.println(jsonArray);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("fileNames",jsonArray);
        return modelMap;
    }

    @RequestMapping(value = "/buildIndex")
    @ResponseBody
    public ModelMap indexController() throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("buildIndex");
        ModelMap modelMap = new ModelMap();
        BoolRetrivalModel br = new BoolRetrivalModel();
        Document document = new Document();
        boolean isChinese = true;
        //String docDir = "D:\\eclipse-workspace\\BooleanRetrival\\dataset";
        //document.fetchDocuments(docDir, isChinese);//处理文件//
        String initDir = ResourceUtils.getFile("classpath:lib").getPath();
        String dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        document.fetchDocuments(dataDir, isChinese,initDir);//处理文件//
        TreeMap<Integer, ArrayList<String>>documents =document.getDocuments();
        br.buildInvertedIndex(documents);//建立倒排索引
        //br.writeIndex();//倒排索引写入文件
        TreeMap<String, ArrayList<Integer>> invertedIndex= br.getInvertedIndex();
        JSONArray jsonArray =JSONArray.fromObject(invertedIndex);
        modelMap.addAttribute("jsonArray",jsonArray);
        return modelMap;
    }

    @RequestMapping(value = "/removePun")
    @ResponseBody
    public ModelMap removePunController(@RequestParam(name = "fileName") String fileName) throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("removePun");
        ModelMap modelMap = new ModelMap();
        boolean isChinese = true;
        Document document = new Document();
        //document.FetchDocuments("data_train", isChinese);//处理文件//
        //document.fetchDocuments("D:\\eclipse-workspace\\BooleanRetrival\\dataset", isChinese);
        String initDir = ResourceUtils.getFile("classpath:lib").getPath();
        String dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        document.fetchDocuments(dataDir, isChinese,initDir);
        String noPunSegments = document.getNoPunSegments().get(fileName);
        System.out.println("noPunSegments=="+noPunSegments);
        modelMap.addAttribute("noPunSegments",noPunSegments);
        return modelMap;
    }

    @RequestMapping(value = "/removeTxtFile")
    @ResponseBody
    public ModelMap removeTxtFileController(@RequestParam(name = "fileContents") String contents) throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("removeTxtFile");
        ModelMap modelMap = new ModelMap();
        //String contents = req.getParameter("fileContents");
        System.out.println(contents);
        String initDir = ResourceUtils.getFile("classpath:lib").getPath();
        NLPIR.init(initDir);
        String removeResult = NLPIR.paragraphProcess(contents.toString(),0).replaceAll("\\pP", "");
        System.out.println("removeResult=="+removeResult);
        modelMap.addAttribute("removeResult",removeResult);
        return modelMap;
    }

    @RequestMapping(value = "/boolRetrival")
    @ResponseBody
    public ModelMap retrivalController(@RequestParam(name = "terms",defaultValue = "") String[] terms,@RequestParam(name = "operators", defaultValue = "") String[] operators) {
        System.out.println("hello world");
        System.out.println("boolRetrival");
        ModelMap modelMap = new ModelMap();
        System.out.println("进入IndexController!");
        //resp.setContentType("text/text;charset=utf-8");
        //String docDir = "D:\\eclipse-workspace\\BooleanRetrival\\dataset";
        //String dataDir = this.getServletContext().getRealPath("/dataset");
        String dataDir = null;
        try {
            dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //String[] terms = req.getParameterValues("terms");
        //String[] operators = req.getParameterValues("operators");
        //ArrayList<String> nameResults = new ArrayList<String>();
        //ArrayList<String> contentResults = new ArrayList<String>();
        TreeMap<String, String> results = new TreeMap<String, String>();
        boolean isChinese = true;
        BoolRetrivalModel br = new BoolRetrivalModel();
        Document document = new Document();
        //	document.fetchDocuments(docDir, isChinese);//处理文件//
        //tring initDir = this.getServletContext().getRealPath("/lib");
        String initDir = null;
        try {
            initDir = ResourceUtils.getFile("classpath:lib").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.fetchDocuments(dataDir, isChinese, initDir);//处理文件//
        TreeMap<Integer, ArrayList<String>> documents = document.getDocuments();
        HashMap<Integer, String> docID_Name = document.getDocID_Name();
        HashMap<Integer, String> docID_Content = document.getDocID_Contents();
        br.buildInvertedIndex(documents);//建立倒排索引
        TreeMap<String, ArrayList<Integer>> invertedIndex = br.getInvertedIndex();
        ArrayList<Integer> ResultIDs = br.boolRetrival(terms, operators);
        if (null == ResultIDs) {//没有结果
            modelMap.addAttribute("JSONdata", 0);
            return modelMap;
        } else {
            for (int i = 0; i < ResultIDs.size(); i++) {
                int id = ResultIDs.get(i);
                //nameResults.add(docID_Name.get(id));
                //contentResults.add(docID_Content.get(id));
                results.put(docID_Name.get(id), docID_Content.get(id));
            }
            //System.out.println("结果：" + results);
            if (results.isEmpty()) {//没有结果
                modelMap.addAttribute("JSONdata", 0);
                return modelMap;
            } else {
                JSONArray jsonArray = JSONArray.fromObject(results);
                //System.out.println("jsonArray是： " + jsonArray);
                modelMap.addAttribute("JSONdata", jsonArray);
                return modelMap;
            }
        }
    }

    @RequestMapping(value = "/segment")
    @ResponseBody
    public ModelMap segmentController (@RequestParam(name = "fileName",defaultValue = "") String fileName) throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("segment");
        ModelMap modelMap = new ModelMap();
        boolean isChinese = true;
        Document document = new Document();
        //document.FetchDocuments("data_train", isChinese);//处理文件//
        //document.fetchDocuments("D:\\eclipse-workspace\\BooleanRetrival\\dataset", isChinese);
        String initDir = ResourceUtils.getFile("classpath:lib").getPath();
        String dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
        document.fetchDocuments(dataDir, isChinese,initDir);
        String segments = document.getSegments().get(fileName);
        System.out.println("segments=="+segments);
        modelMap.addAttribute("segments",segments);
        return modelMap;
    }

    @RequestMapping(value = "/splitTxtFile")
    @ResponseBody
    public ModelMap splitTxtFileController (@RequestParam(name = "fileContents",defaultValue = "") String contents) throws FileNotFoundException {
        System.out.println("hello world");
        System.out.println("splitTxtFile");
        ModelMap modelMap = new ModelMap();
        System.out.println(contents);
        String initDir = ResourceUtils.getFile("classpath:lib").getPath();
        NLPIR.init(initDir);
        String splitResult = NLPIR.paragraphProcess(contents.toString(),0);
        System.out.println("splitResult=="+splitResult);
        modelMap.addAttribute("splitResult",splitResult);
        return modelMap;
    }
}
