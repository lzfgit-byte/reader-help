package com.ilzf.readerhelper.controller;

import cn.hutool.core.io.FileUtil;
import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.service.BookService;
import jakarta.annotation.Resource;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private BookService bookService;

    /**
     * get /
     */
    @RequestMapping(value = "/")
    public ModelAndView test(ModelAndView mv) {
        List<BookEntity> books = bookService.listFiles();
        mv.setViewName("/index");
        mv.addObject("books", books);
        return mv;
    }

    @GetMapping(value = "/bookinfo/{path}")
    public BookEntity bookInfo(@PathVariable String path) {
        //返回静态页面index.html
        return null;
    }
}
