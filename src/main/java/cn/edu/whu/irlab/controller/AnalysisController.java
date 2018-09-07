package cn.edu.whu.irlab.controller;

import cn.edu.whu.irlab.service.analysis.DivideWord;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-08-20 15:06
 * @desc 分词的前后端交互
 **/
@RestController
public class AnalysisController {

    @Autowired
    private DivideWord divideWord;

    @RequestMapping(value = "/analysis")
    public ModelMap analysisController(String data){
        //DivideWord dw=new DivideWord();//调用分词
        String responseText = null;
        DivideWord dw=new DivideWord();//调用分词
        try {
            responseText = dw.divide(data, 7);//最大正向匹配
        } catch (IOException e) {
            e.printStackTrace();
        }
        //	String responseText = dw.left_to_right_divide(text, 4);//最大正向匹配的从左向右
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("results",responseText);
        return modelMap;

        }
}
