package com.starcloud.ops.business.core.client;

import com.aliyun.oss.OSS;
import com.starcloud.ops.business.core.config.oss.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class AliyunOssClient {

    @Resource
    private OSS ossClient;

    @Resource
    private OssProperties properties;

    /**
     * 上传文件
     *
     * @param filename 文件名
     * @param content  文件内容
     * @return 文件地址
     */
    public String upload(String filename, byte[] content) {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("阿里云OSS上传失败：文件名称为必填！");
        }
        log.info("上传文件开始: 文件名: {}", filename);
        ossClient.putObject(properties.getBucket(), filename, new ByteArrayInputStream(content));
        log.info("上传文件到OSS成功，文件名：{}，文件大小：{}", filename, content.length);
        if (properties.getSupportCname()) {
            String url = properties.getEndpoint() + "/" + filename;
            log.info("支持自定义域名：文件访问地址：{}", url);
            return url;
        }
        String url = properties.getEndpoint() + "/" + properties.getBucket() + "/" + filename;
        log.info("文件访问地址：{}", url);
        return url;
    }

}
