package com.ilzf.readerhelper.controller;

import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.entity.MetInfo;
import com.ilzf.readerhelper.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(value = "/data-operate")
public class DataController {
    @Resource
    private BookService bookService;

    /**
     * post /save-meta-data
     */
    @PostMapping("/save-meta-data")
    public String saveMetaData(@RequestBody MetInfo metInfo, @RequestParam(value = "bookTitle") String bookTitle) {
        this.bookService.saveMetaData(metInfo, bookTitle);
        return "ok";
    }
}
