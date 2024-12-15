package com.ilzf.readerhelper.controller;

import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    @Resource
    private ReaderPropertyConfig readerPropertyConfig;

    /**
     * get /
     */
    @RequestMapping(value = "/")
    public ModelAndView test(ModelAndView mv) {
        mv.setViewName("/index");
        mv.addObject("title", "欢迎使用Thymeleaf!");
//        System.out.println(readerPropertyConfig.getPath());
        return mv;
    }

    @GetMapping(value = "/d")
    public String dd() {
        //返回静态页面index.html
        return "dddd";
    }
}
