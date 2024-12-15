package com.ilzf.readerhelper.entity;

import cn.hutool.core.io.FileUtil;
import com.ilzf.readerhelper.constant.FileType;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class BookEntity {
    private String id;
    private String covertImage;
    private String path;
    private String title;
    private String author;
    private String introduction;
    private String size;
    private FileType fileType;
    private List<ChapterEntity> chapters;

    public static BookEntity init(File file) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(file.getName());
        bookEntity.setSize(FileUtil.readableFileSize(file));
        bookEntity.setPath(file.getAbsolutePath());
        bookEntity.setFileType(FileType.convert(FileUtil.getSuffix(file)));
        return bookEntity;
    }
}
