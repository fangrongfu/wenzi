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
    private BoolRetrivalModel boolRetrivalModel = new BoolRetrivalModel();
    @Autowired
    private Document document = new Document();

    @RequestMapping(value = "/boolRetrival_chn")
    @ResponseBody
    public ModelMap retrivalController(@RequestParam(name = "terms",defaultValue = "") String[] terms,@RequestParam(name = "operators", defaultValue = "") String[] operators) {
        ModelMap modelMap = new ModelMap();
        boolean isChinese = true;

//        String dataDir = null;
//        try {
//            dataDir = ResourceUtils.getFile("classpath:dataset").getPath();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        HashMap<String, String> results = new HashMap<String, String>();
//        BoolRetrivalModel boolRetrivalModel = new BoolRetrivalModel();
//        Document document = new Document();

        //根据逻辑运算得到的docID 从docID_Name.txt获取对应的docName ,再根据docName从doc_list_new.json中获取title
        //页面最后展示的是title和content

        TreeMap<String, ArrayList<Integer>> invertedIndex= document.getInvertedIndex(isChinese);
        ArrayList<Integer>ResultIDs = boolRetrivalModel.boolRetrival(terms, operators,invertedIndex);
        if(null==ResultIDs) {//没有结果
            modelMap.addAttribute("JSONdata", 0);
            return modelMap;
        }else {
            results = document.getTitle_Content(ResultIDs,isChinese);
            if (results.isEmpty()) {//没有结果
                modelMap.addAttribute("JSONdata", 0);
                return modelMap;
            } else {
                JSONArray jsonArray = JSONArray.fromObject(results);
                modelMap.addAttribute("JSONdata", jsonArray);
                return modelMap;
            }
        }

    }

}
