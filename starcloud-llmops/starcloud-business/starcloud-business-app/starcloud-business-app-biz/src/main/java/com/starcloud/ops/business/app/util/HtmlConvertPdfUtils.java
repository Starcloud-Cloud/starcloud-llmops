package com.starcloud.ops.business.app.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
public class HtmlConvertPdfUtils {

    private static final Map<String, byte[]> DEFAULT_FONTS = new ConcurrentHashMap<>();

    static {
        ClassLoader classLoader = HtmlConvertPdfUtils.class.getClassLoader();
        InputStream microsoftYaHeiFontInputStream = classLoader.getResourceAsStream("MicrosoftYaHei.ttf");
        byte[] microsoftYaHeiFontBytes = IoUtil.readBytes(microsoftYaHeiFontInputStream);
        DEFAULT_FONTS.put("MicrosoftYaHei", microsoftYaHeiFontBytes);
    }

    /**
     * 将HTML内容转换为PDF
     *
     * @param htmlContent HTML内容
     * @param pageSize    页面大小
     * @param title       标题
     * @param subject     主题
     * @param author      作者
     * @param creator     创建者
     * @param producer    生产者
     * @return PDF字节数组
     */
    public static byte[] convert(String htmlContent, String title, String subject, String author, String creator, String producer) throws IOException, DocumentException, com.lowagie.text.DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer render = new ITextRenderer();
        ITextFontResolver fontResolver = render.getFontResolver();
        fontResolver.addFont("MicrosoftYaHei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        // 解析html生成pdf
        render.setDocumentFromString(htmlContent);
        //解决图片相对路径的问题
        render.layout();
        render.createPDF(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 如果字符串为空，则使用默认值
     *
     * @param consumer     消费者
     * @param value        值
     * @param defaultValue 默认值
     */
    private static void defaultIfBlank(Consumer<String> consumer, String value, String defaultValue) {
        String acceptValue = StringUtils.isBlank(value) ? defaultValue : value;
        consumer.accept(acceptValue);
    }

    public static void main(String[] args) throws DocumentException, IOException, com.lowagie.text.DocumentException {
//        String content = HTML_CONTENT;
//        for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
//            content = content.replace(entry.getKey(), entry.getValue());
//        }
        String content = "<html><body><h1>Hello World!</h1></body></html>";
        byte[] pdf = convert(content, "抗遗忘单词卡", "抗遗忘单词卡", ", ", "", "");
        File file = new File("output.pdf");
        FileUtil.writeBytes(pdf, file);
        System.out.println("PDF file created: " + file.getAbsolutePath());
    }

    private static final Map<String, String> REPLACEMENTS = new HashMap<>();

    static {
        REPLACEMENTS.put("${word1}", "apple");
        REPLACEMENTS.put("${word2}", "banana");
        REPLACEMENTS.put("${word3}", "cherry");
        REPLACEMENTS.put("${word4}", "durian");
        REPLACEMENTS.put("${word5}", "elderberry");
        REPLACEMENTS.put("${word6}", "fig");
        REPLACEMENTS.put("${word7}", "grape");
        REPLACEMENTS.put("${word8}", "honeydew");
        REPLACEMENTS.put("${word9}", "jackfruit");
        REPLACEMENTS.put("${word10}", "kiwi");
        REPLACEMENTS.put("${translation1}", "苹果");
        REPLACEMENTS.put("${translation2}", "香蕉");
        REPLACEMENTS.put("${translation3}", "樱桃");
        REPLACEMENTS.put("${translation4}", "榴莲");
        REPLACEMENTS.put("${translation5}", "黑莓");
        REPLACEMENTS.put("${translation6}", "无花果");
        REPLACEMENTS.put("${translation7}", "葡萄");
        REPLACEMENTS.put("${translation8}", "猕猴桃");
        REPLACEMENTS.put("${translation9}", "榴莲");
        REPLACEMENTS.put("${translation10}", "奇异果");
    }

    private static final String HTML_CONTENT = "<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>每日10个单词记忆纸</title>\n" +
            "</head>\n" +
            "<style>\n" +
            "    @page {\n" +
            "        size: A4 landscape;\n" +
            "        margin: 0;\n" +
            "    }\n" +
            "    \n" +
            "    body {\n" +
            "        width: 842px;\n" +
            "        height: 595px;\n" +
            "        margin: 0;\n" +
            "        padding: 0;\n" +
            "        background-image: url('https://service-oss-poster.mofaai.com.cn/mofaai/upload/20250121/1737453994137.png');\n" +
            "        background-size: 842px 595px;\n" +
            "        background-repeat: no-repeat;\n" +
            "        background-position: left top;\n" +
            "        box-sizing: border-box;\n" +
            "        overflow: hidden;\n" +
            "        position: relative;\n" +
            "        page-break-after: always;\n" +
            "    }\n" +
            "</style>\n" +
            "<body>\n" +
            "    <div style=\"width: 842px; height: 595px;\">\n" +
            "        <!-- 英文单词列 -->\n" +
            "        <div style=\"width: 280px; height: 595px; margin-top: 112px; margin-left: 150px;\">\n" +
            "            <div style=\"margin-bottom: 18px;\">\n" +
            "                <div style=\"font-size: 18px;\">${word1}</div>\n" +
            "                <div style=\"font-size: 18px; margin-top: 8px;\">${word2}</div>\n" +
            "            </div>\n" +
            "            <div style=\"margin-bottom: 18px; margin-top: 18px; padding-top: 18px;\">\n" +
            "                <div style=\"font-size: 18px; margin-bottom: 8px;\">${word3}</div>\n" +
            "                <div style=\"font-size: 18px; margin-top: 8px; padding-top: 8px;\">${word4}</div>\n" +
            "            </div>\n" +
            "            <div style=\"margin-bottom: 18px; margin-top: 18px; padding-top: 8px;\">\n" +
            "                <div style=\"font-size: 18px;\">${word5}</div>\n" +
            "                <div style=\"font-size: 18px; margin-top: 18px;\">${word6}</div>\n" +
            "            </div>\n" +
            "            <div style=\"margin-top: 18px; margin-bottom: 18px; padding-top: 12px;\">\n" +
            "                <div style=\"font-size: 18px;\">${word7}</div>\n" +
            "                <div style=\"font-size: 18px; margin-top: 14px;\">${word8}</div>\n" +
            "            </div>\n" +
            "            <div style=\"margin-top: 18px; margin-bottom: 18px; padding-top: 12px;\">\n" +
            "                <div style=\"font-size: 18px;\">${word9}</div>\n" +
            "                <div style=\"font-size: 18px; margin-top: 18px;\">${word10}</div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <!-- 中文翻译列 -->\n" +
            "        <!-- <div class=\"word-column chinese-column\">\n" +
            "            <div class=\"chinese-text chinese-text-1\">${translation1}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation2}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation3}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation4}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation5}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation6}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation7}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation8}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation9}</div>\n" +
            "            <div class=\"chinese-text chinese-text-2\">${translation10}</div>\n" +
            "        </div> -->\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
}