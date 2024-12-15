package com.ilzf.reader_helper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/")
public class IndexController {

    /**
     * get /
     */
    @GetMapping(value = "/")
    public String index() {
        //返回静态页面index.html
        return "index";
    }
}
