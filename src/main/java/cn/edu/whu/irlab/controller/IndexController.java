package cn.edu.whu.irlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author fangrf
 * @version 1.0
 * @date 2018-08-16 19:03
 * @desc 首页路径的跳转
 **/
@Controller
public class IndexController {

    @RequestMapping(value = "/booleanretrieval")
    public String booleanRetrieval(){
        return "booleanretrieval";
    }

    @RequestMapping(value = "/languagemodel")
    public String languageModel(){
        return "languagemodel";
    }

    @RequestMapping(value = "/learningtorank")
    public String learningToRank(){
        return "learningtorank";
    }

    @RequestMapping(value = "/lexicalanalysis")
    public String lexicalAnalysis(){
        return "lexicalanalysis";
    }

    @RequestMapping(value = "/namedentityrecognition")
    public String namedEntityRecognition(){
        return "namedentityrecognition";
    }

    @RequestMapping(value = "/postagging")
    public String posagging(){
        return "postagging";
    }

    @RequestMapping(value = "/probabilitymodel")
    public String probabilityModel(){
        return "probabilitymodel";
    }

    @RequestMapping(value = "/vectorspacemodel")
    public String vectorSpaceModel(){
        return "vectorspacemodel";
    }

}
