package com.ilzf.readerhelper.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import com.ilzf.readerhelper.constant.FileType;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
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

    public BookEntity getDetail(String path) {
        File file = new File(path);
        BookEntity result = BookEntity.init(file);
        if (FileType.TXT == result.getFileType()) {
            List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
            List<ChapterEntity> chapters = new ArrayList<>();
            String content = "";
            String title = "";
            for (String line : lines) {
                if (line.startsWith("第") && (line.contains("章") || line.contains("回")) && line.length() < 30) {
                    if (!StrUtil.isEmpty(content)) {
                        if (StrUtil.isEmpty(title)) {
                            String[] split = content.split("\n");
                            if (split.length > 0 && split[0].length() < 30) {
                                title = split[0];
                            } else {
                                title = "第%s章[自动]".formatted(chapters.size() + 1);
                            }
                        }
                        chapters.add(ChapterEntity.builder().title(title).content(content).id(DigestUtil.md5Hex(title)).build());
                    }
                    title = line;
                    content = "";
                    continue;
                }
                content += line + "\n";
            }
            chapters.add(ChapterEntity.builder().title(title).content(content).id(DigestUtil.md5Hex(title)).build());
            result.setChapters(chapters);
        }

        return result;
    }
}
