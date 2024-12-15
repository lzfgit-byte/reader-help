package com.ilzf.readerhelper.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.constant.FileType;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
import com.ilzf.readerhelper.utils.TextBookUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
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

    public BookEntity getDetail(String path, boolean loadChapter) {
        File file = new File(path);
        BookEntity result = BookEntity.init(file);
        if (!loadChapter) {
            if (FileType.TXT == result.getFileType()) {
                result = TextBookUtil.setMetInfo(readerPropertyConfig.getMetInfoPath() + File.separator + result.getTitle() + "." + "metinfo", result);
            }
            return result;
        }
        if (FileType.TXT == result.getFileType()) {
            List<ChapterEntity> chapters = TextBookUtil.getChapterEntity(file, result);
            result.setChapters(chapters);
        }

        return result;
    }

    public BookEntity getDetail(String path) {
        return getDetail(path, true);
    }


    public String getContent(String id) {
        ChapterEntity entity = TextBookUtil.getChapterContent(id);
        if (entity == null) {
            return "";
        }
        return entity.getContent();
    }
}
