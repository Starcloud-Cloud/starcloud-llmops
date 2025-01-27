package com.starcloud.ops.business.app;

import com.documents4j.api.DocumentType;
import com.documents4j.job.LocalConverter;

import java.io.File;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
public class PDFGenerate {

    public static void main(String[] args) {

        File file = new File("output.pptx");
        File output = new File("output.pdf");
        LocalConverter.builder()
                .build()
                .convert(file).as(DocumentType.PPTX)
                .to(output).as(DocumentType.PDF)
                .execute();
        System.out.println("Convert success!");
    }
}
