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

    @RequestMapping(value = "/IRLab_index")
    public String index(){
        return "IRLab_index";
    }

    @RequestMapping(value = "/IRLab_Chinese")
    public String chinese(){
        return "IRLab_Chinese";
    }

    @RequestMapping(value = "/IRLab_English")
    public String english(){
        return "IRLab_English";
    }
    @RequestMapping(value = "/IRLab_participle")
    public String participle(){
        return "IRLab_participle";
    }

    @RequestMapping(value = "/IRLab_post")
    public String postModel(){
        return "IRLab_post";
    }

    @RequestMapping(value = "/IRLab_ione")
    public String ioneModel(){
        return "IRLab_ione";
    }

    @RequestMapping(value = "/IRLab_br")
    public String brModel(){
        return "IRLab_br";
    }

    @RequestMapping(value = "/IRLab_lm")
    public String lmModel(){
        return "IRLab_lm";
    }

    @RequestMapping(value = "/IRLab_pm")
    public String pmModel(){
        return "IRLab_pm";
    }

    @RequestMapping(value = "/IRLab_vsm")
    public String vsmModel(){
        return "IRLab_vsm";
    }

    @RequestMapping(value = "/IRLab_api")
    public String apiModel(){
        return "IRLab_api";
    }
}
