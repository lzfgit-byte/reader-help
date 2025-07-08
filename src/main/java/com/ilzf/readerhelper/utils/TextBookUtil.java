package com.ilzf.readerhelper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ilzf.readerhelper.entity.BookEntity;
import com.ilzf.readerhelper.entity.ChapterEntity;
import com.ilzf.readerhelper.entity.MetInfo;
import lombok.SneakyThrows;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TextBookUtil {
    private static final Map<String, ChapterEntity> chapterEntityCache = new ConcurrentHashMap<>();
    private static final boolean enableSmallTitle = false;

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
        StringBuilder content = new StringBuilder();
        String title = "";
        int count = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!StrUtil.isNotEmpty(line) || isSeqContent(line)) {
                continue;
            }
            boolean isTitle = isIsTitle(line);
            boolean isMa = isMaybeTitle(line, i, lines);
            if (isTitle || (isMa && enableSmallTitle)) {
                if (!StrUtil.isEmpty(content.toString())) {
                    if (StrUtil.isEmpty(title)) {
                        String[] split = content.toString().split("<br/>\n");
                        if (split.length > 0 && split[0].length() < 30) {
                            title = split[0];
                        } else {
                            title = "第%s章[自动]".formatted(chapters.size() + 1);
                        }
                    }
                    setChapter(chapters.size() + "|" + title, content.toString(), result, chapters);
                    title = "";
                } else {
                    if (StrUtil.isNotEmpty(title)) {
                        //两个标题合并了
                        setChapter(chapters.size() + "|" + title, title, result, chapters);
                        title = "";
                    }
                }
                title = line;
                content = new StringBuilder();
                System.out.println(++count + "/" + lines.size());
                continue;
            }
            if (StrUtil.isNotEmpty(line)) {
                content.append(wrapperNewLine(line, i, lines));
                System.out.println(++count + "/" + lines.size());
            }
        }
        setChapter(chapters.size() + "|" + title, content.toString(), result, chapters);
        title = "";
        return chapters;
    }

    private static boolean isSeqContent(String line) {
        return line.startsWith("-") && line.endsWith("-") && line.length() < 5;
    }

    private static String wrapperNewLine(String line, int idx, List<String> lines) {
        if (isMaybeTitle(line, idx, lines)) {
            return line + "<br/>\n";
        }

        return canAppend(line, idx, lines);
    }

    private static String canAppend(String line, int idx, List<String> lines) {
        boolean afterEmpty = afterEmpty(line, idx, lines);
        boolean a = line.endsWith("。") ||
                (line.startsWith("“") && line.endsWith("”")) ||
                (line.startsWith("\"") && line.endsWith("\"")) ||
                (line.startsWith("…") && line.endsWith("…")) ||
                line.endsWith("。”") || afterEmpty;
        if (a) {
            return line + "<br/>\n";
        }
        if (idx > 1) {
            if (line.length() < 20 && StrUtil.isEmpty(lines.get(idx - 1)) && StrUtil.isEmpty(lines.get(idx + 1))) {
                return line + "  ";
            }
        }

        return line;
    }

    private static boolean isIsTitle(String line) {

        List<String> startsWith = List.of("第", "正文 第", "序:", "番外篇");
        List<String> endsWith = List.of("章", "回", "节");
        List<String> containsStr = List.of("章", "回", "节", "创作手记", "后记", "楔子");
        boolean isTitleContent = (line.equals("序") || startsWith.stream().anyMatch(line::startsWith) || endsWith.stream().anyMatch(line::endsWith))
                && (containsStr.stream().anyMatch(line::contains) || (line.contains("卷") && (line.indexOf("卷") < 5)) || (line.contains("序") && line.length() < 15));
        return isTitleContent && line.length() < 20;
    }

    private static boolean preEmpty(String line, int idx, List<String> lines) {
        if (idx > 1 && idx < lines.size() - 2) {
            String p = lines.get(idx - 1);
            String p2 = lines.get(idx - 2);
            return p.trim().isEmpty() && p2.trim().isEmpty();
        }
        return false;
    }

    private static boolean afterEmpty(String line, int idx, List<String> lines) {
        if (idx > 1 && idx < lines.size() - 2) {
            String a = lines.get(idx + 1);
            String a2 = lines.get(idx + 2);
            return a.trim().isEmpty() && a2.trim().isEmpty();
        }
        return false;
    }

    private static boolean isMaybeTitle(String line, int idx, List<String> lines) {
        if (idx > 0) {
            String preLine = lines.get(idx - 1);
            String newLine = canAppend(line, idx, lines);
            if (isIsTitle(preLine) && (preLine.endsWith("章") || newLine.endsWith("回") || newLine.endsWith("节")) && !newLine.endsWith("\n")) {
                return true;
            }
        }
        return preEmpty(line, idx, lines) && afterEmpty(line, idx, lines) && line.length() < 30 && Stream.of("【").noneMatch(line::startsWith);

    }

    public static ChapterEntity getChapterContent(String id) {
        return chapterEntityCache.get(id);
    }

    private static void setChapter(String title, String content, BookEntity result, List<ChapterEntity> chapters) {
        if (title.endsWith("节") || title.endsWith("章") || title.endsWith("回")) {
            String[] split = content.split("\n");
            for (String s : split) {
                if (s.trim().isEmpty()) {
                    continue;
                }
                if (s.trim().length() < 25 && s.trim().length() > 1) {
                    title = title + " " + s.replace("<br/>", "");
                    break;
                }
                break;
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
