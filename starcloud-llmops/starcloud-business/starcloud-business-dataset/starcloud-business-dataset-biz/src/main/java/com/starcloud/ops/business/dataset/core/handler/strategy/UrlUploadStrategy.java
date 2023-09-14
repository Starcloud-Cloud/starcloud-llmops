package com.starcloud.ops.business.dataset.core.handler.strategy;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.core.handler.UploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadContentDTO;
import com.starcloud.ops.business.dataset.util.dataset.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.SOURCE_DATA_UPLOAD_FAIL;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.SOURCE_DATA_UPLOAD_URL_FAIL_INACCESSIBLE;

@Slf4j
@Component
public class UrlUploadStrategy implements UploadStrategy {


    @Resource
    private FileApi fileApi;

    // 成员变量用于保存URL
    private String url;

    private String language;


    private static final String PATH_OBJECT = "dataset-source-data/";

    // Setter方法，用于接收MultipartFile对象
    public void setUrl(String url, String language) {
        this.url = url;
        this.language = language;
    }

    /**
     * 实现URL上传的处理逻辑
     *
     * @return
     */
    @Override
    public UploadContentDTO process(Long userId) {

        UploadContentDTO uploadFileRespDTO = new UploadContentDTO();

        // 设置文件名称
        Document doc;
        try {
            doc = JsoupUtil.loadUrl(url, language);
        } catch (Exception e) {
            uploadFileRespDTO.setName(url);
            uploadFileRespDTO.setErrCode(String.valueOf(SOURCE_DATA_UPLOAD_URL_FAIL_INACCESSIBLE.getCode()));
            uploadFileRespDTO.setErrMsg(SOURCE_DATA_UPLOAD_URL_FAIL_INACCESSIBLE.getMsg());
            log.error("====> 使用代理请求网页解析失败,数据状态为 false，网页链接为{}", url, e);
            return uploadFileRespDTO;
        }


        // 获取网页的title
        String name = getUrlTitle(doc);

        name = StrUtil.isBlank(name) ? url : name;
        // 如果长度不足 300，返回全部字符串
        if (name.length() >= 300) {
            name = name.substring(0, 300);
        }

        uploadFileRespDTO.setName(name);

        // 获取网页的描述
        String description = getUrlDescription(doc);

        uploadFileRespDTO.setDescription(description);

        String result = doc.toString();

        // 生成文件ID - 使用 URL SecureUtil.md5 会关闭流
        String fileId = SecureUtil.md5(url);
        String filePath = null;
        try {
            // 上传文件
            filePath = uploadFile(fileId, result, userId);
            if (StrUtil.isBlank(filePath)) {
                return uploadFileRespDTO;
            }
            // 设置文件名称
            uploadFileRespDTO.setFilepath(filePath);
            uploadFileRespDTO.setStatus(true);
        } catch (Exception e) {
            uploadFileRespDTO.setErrCode(String.valueOf(SOURCE_DATA_UPLOAD_FAIL.getCode()));
            uploadFileRespDTO.setErrMsg(SOURCE_DATA_UPLOAD_FAIL.getMsg());
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
     * @param fileId 文件 ID
     * @param data   文件流
     * @param userId 文件 path 可以为空
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
        try {
            // 获取网页的meta标签
            Element meta = doc.select("meta[http-equiv=Content-Type], meta[charset]").first();
            String charset = meta != null ? meta.attr("charset") : null;

            // 如果charset为空，则默认使用UTF-8
            if (charset == null || charset.isEmpty()) {
                charset = CharsetUtil.UTF_8;
            }
            String title;
            // 获取网页的title，使用实际编码进行解析
            title = new String(doc.title().getBytes(StandardCharsets.UTF_8), Charset.forName(charset));
            if (StrUtil.isBlank(title)) {
                title = Objects.requireNonNull(doc.select("meta[property=og:title]").first()).attr("content");
            }
            return title;

        } catch (RuntimeException e) {
            return null;
        }
    }


    /**
     * 获取网页描述
     *
     * @param doc
     * @return
     */
    private String getUrlDescription(Document doc) {
        String description = null;

        try {
            Element metaTag = doc.selectFirst("meta[property=description], meta[name=description]");
            if (metaTag != null) {
                description = metaTag.attr("content");
                return description;
            }
            return description;
        } catch (RuntimeException e) {
            return description;
        }
    }
}