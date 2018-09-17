package cn.edu.whu.irlab.controller;

import cn.edu.whu.irlab.service.vectormodel.DocSet;
import cn.edu.whu.irlab.service.vectormodel.Query;
import cn.edu.whu.irlab.util.ReadDoc;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-09-02 19:11
 * @desc 空间向量模型交互层
 **/
@Controller
@RequestMapping(value = "/vector")
public class VectorController {

    @RequestMapping(value = "/index")
    @ResponseBody
    public Map<String, ArrayList<Integer>> vectorIndexController() throws FileNotFoundException {
        DocSet docSet=new DocSet(ResourceUtils.getFile("classpath:dataset").getPath());
        Map<String, ArrayList<Integer>> results =docSet.getIndexMap();
        return results;
    }

    @RequestMapping(value = "/search")
    @ResponseBody
    public ModelMap vectorSearchController(@RequestParam(name = "query")String query) throws IOException {
        FileOutputStream writerStream = new FileOutputStream(ResourceUtils.getFile("classpath:Query.txt").getPath());
        BufferedWriter Query = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
        Query.write(query);
        Query.close();
        DocSet docSet=new DocSet(ResourceUtils.getFile("classpath:dataset").getPath());
        String queryString= ReadDoc.readDoc(ResourceUtils.getFile("classpath:Query.txt").getPath());
        cn.edu.whu.irlab.service.vectormodel.Query query1=new Query(queryString);
        double[] queryVector =query1.countQueryVector(docSet.getWordIdf());
        TreeMap<String, String> result=docSet.result(queryVector);
        JSONArray jsonArray = JSONArray.fromObject(result);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("result",jsonArray);
        return modelMap;
    }
}
