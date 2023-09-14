package com.starcloud.ops.business.core.config.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * Aliyun OSS Client
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2022-06-22
 */
@Slf4j
@Component
public class AliyunOssClient {

    private static final String AI_GENERATE_IMAGE_PATH = "mofaai/images/ai-generation";

    @Resource
    private OSS ossClient;

    @Value("${oss.bucket}")
    private String bucket;

    @Value("${oss.endpoint}")
    private String endpoint;

    /**
     * 上传到 AI 生成图片文件夹
     *
     * @param uuid        生成图片的 uuid
     * @param contentType 文件类型
     * @param bytes       文件字节
     * @return 上传URL
     */
    public String putAiGenerateImage(String uuid, String contentType, byte[] bytes) {
        String imageName = uuid + getFileSuffix(contentType);
        return put(AI_GENERATE_IMAGE_PATH, imageName, bytes, contentType);
    }

    /**
     * 上传文件到 OSS，文件名称需要带后缀
     *
     * @param filePath  文件路径，是oss的文件夹路径
     * @param fileName  文件名称
     * @param fileBytes 文件字节
     * @return 上传URL
     */
    public String put(String filePath, String fileName, byte[] fileBytes) {
        return put(filePath, fileName, fileBytes, null);
    }

    /**
     * 上传文件到 OSS，文件名称需要带后缀
     *
     * @param filePath    文件路径，是oss的文件夹路径
     * @param fileName    文件名称
     * @param fileBytes   文件字节
     * @param contentType 文件类型
     * @return 上传URL
     */
    public String put(String filePath, String fileName, byte[] fileBytes, String contentType) {
        String path = filePath + "/" + fileName;
        if (ossClient.doesObjectExist(bucket, path)) {
            log.error("上传文件到 OSS 失败, 文件已存在");
            throw new IllegalArgumentException("文件已存在");
        }
        try {
            ObjectMetadata metadata = null;
            if (StringUtils.isNotBlank(contentType)) {
                metadata = new ObjectMetadata();
                metadata.setContentType(contentType);
            }
            ossClient.putObject(bucket, path, new ByteArrayInputStream(fileBytes), metadata);
        } catch (Exception exception) {
            log.error("上传文件到 OSS 失败, {}", exception.getMessage());
            throw new RuntimeException(exception);
        } finally {
            ossClient.shutdown();
        }

        return endpoint + "/" + path;
    }

    /**
     * 获取文件后缀
     *
     * @param contentType 文件类型
     * @return 文件后缀
     */
    private static String getFileSuffix(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalArgumentException("contentType 不能为空!");
        }

        if ("image/png".equals(contentType)) {
            return ".png";
        }

        if ("image/jpeg".equals(contentType)) {
            return ".jpeg";
        }

        if ("image/jpg".equals(contentType)) {
            return ".jpg";
        }

        if ("image/bmp".equals(contentType)) {
            return ".bmp";
        }

        if ("image/gif".equals(contentType)) {
            return ".gif";
        }

        return ".png";
    }
}
