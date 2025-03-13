package com.ilzf.readerhelper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
import com.ilzf.readerhelper.entity.MetInfo;
import lombok.SneakyThrows;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextBookUtil {
    private static final Map<String, ChapterEntity> chapterEntityCache = new ConcurrentHashMap<>();

    @SneakyThrows
    public static String detectEncoding(File file) {
        UniversalDetector detector = new UniversalDetector(null);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096]; // 每次读取 4KB
            int nread;
            while ((nread = fis.read(buffer)) > 0 && !detector.isDone()) {
                detector.handleData(buffer, 0, nread);
            }
        }

        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    public static List<ChapterEntity> getChapterEntity(File file, BookEntity result) {
        List<String> lines = FileUtil.readLines(file, Charset.forName(detectEncoding(file)));
        List<ChapterEntity> chapters = new ArrayList<>();
        String content = "";
        String title = "";
        int count = 0;
        for (String line : lines) {
            line = line.trim();
            if (!StrUtil.isNotEmpty(line)) {
                continue;
            }
            boolean isTitleContent = (line.startsWith("第") || line.startsWith("正文 第") || line.equals("序"))
                    && (line.contains("章") || line.contains("回") || line.contains("节") || (line.contains("卷") && (line.indexOf("卷") < 5)) || line.contains("创作手记") || line.contains("后记") ||
                    line.contains("楔子") || (line.contains("序") && line.length() < 5));
            boolean isTitle = isTitleContent && line.length() < 20;
            if (isTitle) {
                if (!StrUtil.isEmpty(content)) {
                    if (StrUtil.isEmpty(title)) {
                        String[] split = content.split("<br/>\n");
                        if (split.length > 0 && split[0].length() < 30) {
                            title = split[0];
                        } else {
                            title = "第%s章[自动]".formatted(chapters.size() + 1);
                        }
                    }
                    setChapter(chapters.size() + "|" + title, content, result, chapters);
                }
                title = line;
                content = "";
                System.out.println(++count + "/" + lines.size());
                continue;
            }
            if (StrUtil.isNotEmpty(line)) {
                content += line + "<br/>\n";
                System.out.println(++count + "/" + lines.size());
            }
        }
        setChapter(chapters.size() + "|" + title, content, result, chapters);
        return chapters;
    }

    public static ChapterEntity getChapterContent(String id) {
        return chapterEntityCache.get(id);
    }

    private static void setChapter(String title, String content, BookEntity result, List<ChapterEntity> chapters) {
        if (title.endsWith("节") || title.endsWith("章") || title.endsWith("回")) {
            String[] split = content.split("\n");
            for (String s : split) {
                if (s.length() < 15 && s.trim().length() > 1) {
                    title = title + " " + s.replace("<br/>", "");
                    break;
                }
            }

        }
        ChapterEntity entity = ChapterEntity.builder().title(title).content(content).id(DigestUtil.md5Hex(title + result.getTitle())).build();
        chapters.add(entity);
        chapterEntityCache.put(entity.getId(), entity);
    }

    public static BookEntity setMetInfo(String mateInfoPath, BookEntity entity) {
        List<String> metInfos = FileUtil.readLines(mateInfoPath, StandardCharsets.UTF_8);
        if (!metInfos.isEmpty()) {
            MetInfo matInfo = MetInfo.toBean(metInfos);
            entity.setCovertImage(matInfo.getCoverImg());
            entity.setIntroduction(matInfo.getIntro());
            entity.setAuthor(matInfo.getAuthor());
        }
        return entity;
    }
}
