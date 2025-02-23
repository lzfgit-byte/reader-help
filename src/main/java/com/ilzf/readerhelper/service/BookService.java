package com.ilzf.readerhelper.service;

import cn.hutool.core.io.FileUtil;
import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.constant.FileType;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
import com.ilzf.readerhelper.entity.MetInfo;
import com.ilzf.readerhelper.utils.TextBookUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.ilzf.readerhelper.constant.Constant.MET_INFO_SUFFIX;

@Service
public class BookService {
    @Resource
    private ReaderPropertyConfig readerPropertyConfig;


    public List<BookEntity> listBooks() {
        File booKPath = new File(readerPropertyConfig.getPath());
        List<BookEntity> books = new ArrayList<>();
        if (booKPath.exists()) {
            FileUtil.walkFiles(booKPath, file -> {
                BookEntity init = BookEntity.init(file);
                books.add(TextBookUtil.setMetInfo(readerPropertyConfig.getMetInfoPath() + File.separator + init.getTitle() + MET_INFO_SUFFIX, init));
            });
        }
        return books;
    }

    public BookEntity getDetail(String path, boolean loadChapter) {
        File file = new File(path);
        BookEntity result = BookEntity.init(file);
        if (!loadChapter) {
            if (FileType.TXT == result.getFileType()) {
                result = TextBookUtil.setMetInfo(readerPropertyConfig.getMetInfoPath() + File.separator + result.getTitle() + MET_INFO_SUFFIX, result);
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

    public void saveMetaData(MetInfo metInfo, String bookTitle) {
        String metInfoPath = this.readerPropertyConfig.getMetInfoPath();
        String path = metInfoPath + File.separator + bookTitle + MET_INFO_SUFFIX;
        FileUtil.writeString(metInfo.toString(), path, StandardCharsets.UTF_8);
    }

    public void transFile(MultipartFile file, String bookName) {
        String path = this.readerPropertyConfig.getPath();
        path = path + File.separator + bookName + "." + FileUtil.getSuffix(file.getOriginalFilename());
        try {
            FileUtil.writeBytes(file.getBytes(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
