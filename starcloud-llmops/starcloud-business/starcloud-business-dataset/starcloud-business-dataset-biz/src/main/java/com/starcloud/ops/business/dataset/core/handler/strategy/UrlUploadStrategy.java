package com.starcloud.ops.business.dataset.core.handler.strategy;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.core.handler.UploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class UrlUploadStrategy implements UploadStrategy {


    @Resource
    private FileApi fileApi;

    // 成员变量用于保存URL
    private String url;

    private static final String PATH_OBJECT = "dataset-source-data/";

    // Setter方法，用于接收MultipartFile对象
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 实现URL上传的处理逻辑
     *
     * @return
     */
    @Override
    public UploadResultDTO process(Long userId) {

        UploadResultDTO uploadFileRespDTO = new UploadResultDTO();

        // 判断 URL 是网页还是文件流
        // 设置文件名称
        Document doc;
        try {
            String normalize = URLUtil.normalize(url);
            doc = Jsoup.connect(normalize).get();
        } catch (Exception e) {
            log.error("====> 网页解析失败,数据状态为 false，网页链接为{}", url);
            return uploadFileRespDTO;
        }

        // 获取网页的title
        String name = getUrlTitle(doc);

        name = name.isEmpty() ? url : name;

        uploadFileRespDTO.setName(name);

        String result = doc.toString();

        // 生成文件ID - 使用 URL SecureUtil.md5 会关闭流
        String fileId = SecureUtil.md5(url);
        String filePath = null;
        try {
            // 上传文件
            filePath = uploadFile(fileId, result, userId);
            // 设置文件名称
            uploadFileRespDTO.setFilepath(filePath);
            uploadFileRespDTO.setStatus(true);
        } catch (Exception e) {
            log.error("====> URL上传失败,数据状态为 false");
            return uploadFileRespDTO;

        }

        // 获取文件size
        long size;
        try {
            InputStream utf8Stream = IoUtil.toUtf8Stream(result);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = utf8Stream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            size = byteArrayOutputStream.toByteArray().length;

        } catch (Exception e) {
            size = 0;
        }

        // 设置文件大小
        uploadFileRespDTO.setSize(size);

        // 设置扩展名
        uploadFileRespDTO.setExtension("html");

        // 设置MimeType
        uploadFileRespDTO.setMimeType("text/html");

        // 设置字符数
        uploadFileRespDTO.setCharacterCount((long) result.length());

        return uploadFileRespDTO;
    }


    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param fileId     文件 ID
     * @param data 文件流
     * @param userId       文件 path 可以为空
     * @return 文件路径
     */
    private String uploadFile(String fileId, String data, Long userId) {


        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);


        String fileName = fileId + "." + "txt";
        String path = String.format(PATH_OBJECT + "%s" + "/", userId) + fileName;

        return fileApi.createFile(fileName, path, IoUtil.readBytes(utf8Stream));
    }

    /**
     * 获取 Url 中的 title-- Jsoup
     *
     * @param doc Jsoup Document 对象
     * @return title
     */
    private static String getUrlTitle(Document doc) {
        // 获取网页的meta标签
        Element meta = doc.select("meta[http-equiv=Content-Type], meta[charset]").first();
        String charset = meta != null ? meta.attr("charset") : null;

        // 如果charset为空，则默认使用UTF-8
        if (charset == null || charset.isEmpty()) {
            charset = CharsetUtil.UTF_8;
        }

        // 获取网页的title，使用实际编码进行解析
        return new String(doc.title().getBytes(StandardCharsets.UTF_8), Charset.forName(charset));
    }
}