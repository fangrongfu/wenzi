package cn.edu.whu.irlab.controller;

import cn.edu.whu.irlab.service.vectormodel.DocSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

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
    public ModelMap vectorSearchController(){
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("index","");
        return modelMap;
    }
}
