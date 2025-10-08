package com.ilzf.readerhelper.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;

@RestController
@RequestMapping("/file")
public class FileDownloadController {

    @GetMapping("/download")
    public void downloadLargeFile(HttpServletResponse response) {
        String filePath = "E:\\BaiduNetdiskDownload\\塞尔达传说 王国之泪\\The Legend of Zelda Tears of the Kingdom [0100F2C0115B6000] [v655360] (1G+1U).xci";
        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
