package com.ilzf.readerhelper.entity;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class BookEntity {
    private String covertImage;
    private String path;
    private String title;
    private String author;
    private String introduction;
    private List<ChapterEntity> chapters;

    public static BookEntity init(File file) {
        
        return new BookEntity();
    }
}
