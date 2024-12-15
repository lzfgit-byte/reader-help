package com.ilzf.reader_helper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /**
     * get /
     */
    @GetMapping(value = "/")
    public String index() {
        //返回静态页面index.html
        return "forward:/frontend/index.html";
    }
    @GetMapping(value = "/d")
    public String dd() {
        //返回静态页面index.html
        return "dddd";
    }
}
