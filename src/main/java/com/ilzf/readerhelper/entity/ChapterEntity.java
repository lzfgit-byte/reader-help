package com.ilzf.readerhelper.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterEntity {
    private String id;
    private String title;
    private String content;
}
