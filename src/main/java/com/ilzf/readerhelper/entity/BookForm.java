package com.ilzf.readerhelper.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookForm {
    private MultipartFile file; // 文件
    private String bookName; // 书名
    private String desc; // 描述
    private String coverImg; // 封面
}
