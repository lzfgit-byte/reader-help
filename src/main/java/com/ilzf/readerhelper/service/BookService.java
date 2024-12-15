package com.ilzf.readerhelper.service;

import cn.hutool.core.io.FileUtil;
import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.entity.BookEntity;
import jakarta.annotation.Resource;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    @Resource
    private ReaderPropertyConfig readerPropertyConfig;

    public List<BookEntity> listBooks() {
        File booKPath = new File(readerPropertyConfig.getPath());
        List<BookEntity> books = new ArrayList<>();
        if (booKPath.exists()) {
            FileUtil.walkFiles(booKPath, file -> {
                books.add(BookEntity.init(file));
            });
        }
        return books;
    }

    public BookEntity getDetail(String path) {
        File file = new File(path);
        BookEntity result = BookEntity.init(file);


        return result;
    }
}
