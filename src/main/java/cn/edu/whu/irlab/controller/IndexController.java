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

    @RequestMapping(value = "/index")
    public String index(){
        return "index";
    }
}
