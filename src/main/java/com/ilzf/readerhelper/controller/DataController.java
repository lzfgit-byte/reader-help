package com.ilzf.readerhelper.controller;

import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.entity.BookForm;
import com.ilzf.readerhelper.entity.MetInfo;
import com.ilzf.readerhelper.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/submitForm")
    public String submitForm(@RequestParam("file") MultipartFile file,
                             @RequestParam("bookName") String bookName,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "authorName", required = false) String authorName,
                             @RequestParam(value = "coverImg", required = false) String coverImg) {
        // 获取文件
        if (file.isEmpty()) {
            return "{\"message\":\"文件不能为空！\"}";
        }
        this.bookService.transFile(file, bookName);
        MetInfo metInfo = new MetInfo();
        metInfo.setAuthor(authorName);
        metInfo.setIntro(desc);
        metInfo.setCoverImg(coverImg);
        this.bookService.saveMetaData(metInfo, bookName);

        return "{\"message\":\"表单已成功提交！\"}";
    }

    /**
     * get delete
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("bookName") String bookName) {
        this.bookService.delete(bookName);
        return "ok";
    }
}
