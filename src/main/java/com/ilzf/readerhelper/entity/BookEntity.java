package com.ilzf.readerhelper.entity;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.val;

import java.io.File;
import java.util.List;

@Data
public class BookEntity {
    private String covertImage;
    private String path;
    private String title;
    private String author;
    private String introduction;
    private String size;
    private List<ChapterEntity> chapters;

    public static BookEntity init(File file) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(file.getName());
        bookEntity.setSize(FileUtil.readableFileSize(file));
        return bookEntity;
    }
}
