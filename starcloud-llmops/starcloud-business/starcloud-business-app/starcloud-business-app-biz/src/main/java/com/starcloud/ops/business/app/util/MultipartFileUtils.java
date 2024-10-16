package com.starcloud.ops.business.app.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MultipartFileUtils {

    public static MultipartFile convert(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "text/plain",
                input
        );
        return multipartFile;
    }


    public static File convert(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));

        File file = null;
        try {
            file = File.createTempFile(fileName, prefix);
            multipartFile.transferTo(file);
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
