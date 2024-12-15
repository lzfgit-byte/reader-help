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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {
    @Resource
    private ReaderPropertyConfig readerPropertyConfig;

    private final Map<String, ChapterEntity> chapterEntityCache = new ConcurrentHashMap<>();

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
            return result;
        }
        if (FileType.TXT == result.getFileType()) {
            List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
            List<ChapterEntity> chapters = new ArrayList<>();
            String content = "";
            String title = "";
            for (String line : lines) {
                if (((line.startsWith("第") && (line.contains("章") || line.contains("回"))) || line.contains("创作手记") || line.contains("后记") || line.contains("楔子")) && line.length() < 30) {
                    if (!StrUtil.isEmpty(content)) {
                        if (StrUtil.isEmpty(title)) {
                            String[] split = content.split("<br/>\n");
                            if (split.length > 0 && split[0].length() < 30) {
                                title = split[0];
                            } else {
                                title = "第%s章[自动]".formatted(chapters.size() + 1);
                            }
                        }
                        setChapter(title, content, result, chapters);
                    }
                    title = line;
                    content = "";
                    continue;
                }
                if (StrUtil.isNotEmpty(line)) {
                    content += line + "<br/>\n";
                }
            }
            setChapter(title, content, result, chapters);
            result.setChapters(chapters);
        }

        return result;
    }

    public BookEntity getDetail(String path) {
        return getDetail(path, true);
    }

    private void setChapter(String title, String content, BookEntity result, List<ChapterEntity> chapters) {
        ChapterEntity entity = ChapterEntity.builder().title(title).content(content).id(DigestUtil.md5Hex(title + result.getTitle())).build();
        chapters.add(entity);
        chapterEntityCache.put(entity.getId(), entity);
    }

    public String getContent(String id) {
        ChapterEntity entity = chapterEntityCache.get(id);
        if (entity == null) {
            return "";
        }
        return entity.getContent();
    }
}
