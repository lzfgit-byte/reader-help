package com.ilzf.readerhelper.entity;

import lombok.Data;

import java.util.List;

@Data
public class MetInfo {
    private String author;
    private String intro;
    private String coverImg;

    public String toString() {
        return "author;;" + author + "\nintro;;" + intro + "\ncoverImg;;" + coverImg;
    }

    public static MetInfo toBean(List<String> metInfos) {
        MetInfo metInfo = new MetInfo();
        for (String info : metInfos) {
            String[] split = info.split(";;");
            if (split.length == 2) {
                if ("author".equals(split[0])) {
                    metInfo.setAuthor(split[1]);
                } else if ("intro".equals(split[0])) {
                    metInfo.setIntro(split[1]);
                } else if ("converImg".equals(split[0])) {
                    metInfo.setCoverImg(split[1]);
                }
            }
        }
        return metInfo;
    }
}
