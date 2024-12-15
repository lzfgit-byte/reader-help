package com.ilzf.readerhelper.constant;

public enum FileType {
    TXT("txt"),
    PDF("pdf"),
    EPUB("epub"),
    DOC("doc"),
    DOCX("docx"),
    PPT("ppt"),
    PPTX("pptx"),
    XLS("xls"),
    XLSX("xlsx"),
    MP3("mp3"),
    MP4("mp4"),
    AVI("avi"),
    WAV("wav"),
    FLV("flv"),
    RMVB("rmvb"),
    MKV("mkv");
    private String name;

    FileType(String name) {
        this.name = name;
    }
}
