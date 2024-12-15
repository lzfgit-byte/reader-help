package com.ilzf.readerhelper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
import com.ilzf.readerhelper.entity.MetInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextBookUtil {
    private static final Map<String, ChapterEntity> chapterEntityCache = new ConcurrentHashMap<>();

    public static List<ChapterEntity> getChapterEntity(File file, BookEntity result) {
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
        return chapters;
    }

    public static ChapterEntity getChapterContent(String id) {
        return chapterEntityCache.get(id);
    }

    private static void setChapter(String title, String content, BookEntity result, List<ChapterEntity> chapters) {
        ChapterEntity entity = ChapterEntity.builder().title(title).content(content).id(DigestUtil.md5Hex(title + result.getTitle())).build();
        chapters.add(entity);
        chapterEntityCache.put(entity.getId(), entity);
    }

    public static BookEntity setMetInfo(String mateInfoPath, BookEntity entity) {
        String matInfoStr = FileUtil.readUtf8String(mateInfoPath);
        if (StrUtil.isNotEmpty(matInfoStr)) {
            JSONObject entries = JSONUtil.parseObj(matInfoStr);
            MetInfo matInfo = JSONUtil.toBean(entries, MetInfo.class);
            entity.setCovertImage(matInfo.getCoverImg());
            entity.setIntroduction(matInfo.getIntro());
            entity.setAuthor(matInfo.getAuthor());
        }
        return entity;
    }
}
