package com.starcloud.ops.business.dataset.core.handler.strategy;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.core.handler.UploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadFileRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class StringUploadStrategy implements UploadStrategy {

    @Resource
    private FileApi fileApi;

    // 成员变量用于保存URL
    private String character;

    // Setter方法，用于接收MultipartFile对象
    public void setCharacter(String character) {
        this.character = character;
    }

    @Override
    public UploadFileRespDTO process() {

        UploadFileRespDTO uploadFileRespDTO = new UploadFileRespDTO();

        // 获取资源名称
        String name = character.substring(0,12);

        // 设置数据名称
        uploadFileRespDTO.setName(name);

        // 将结果转换为InputStream流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(character.getBytes(StandardCharsets.UTF_8));
        // 生成文件ID - 使用 String SecureUtil.md5 会关闭流
        String fileId = SecureUtil.md5(character);
        String filePath = null;
        try {
            // 上传文件
            filePath = uploadFile(fileId, inputStream, null);
            // 设置文件名称
            uploadFileRespDTO.setFilepath(filePath);
            uploadFileRespDTO.setStatus(true);
        } catch (Exception e) {
            log.error("====> 字符串上传失败,数据状态为 false");

        }

        // 获取文件size
        long size;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            size = byteArrayOutputStream.toByteArray().length;

        } catch (Exception e) {
            size = 0;
        }


        // 设置文件大小
        uploadFileRespDTO.setSize(size);

        // 设置扩展名
        uploadFileRespDTO.setExtension("txt");

        // 设置MimeType
        uploadFileRespDTO.setMimeType("text/plain");

        // 设置字符数
        uploadFileRespDTO.setCharacterCount((long) character.length());

        return uploadFileRespDTO;
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param fileId     文件 ID
     * @param fileStream 文件流
     * @param path       文件 path 可以为空
     * @return 文件路径
     */
    private String uploadFile(String fileId, InputStream fileStream, String path) {

        String fileType;
        try {
            fileType = FileTypeUtil.getType(fileStream);
            if (fileType == null || "null".equals(fileType)) {
                fileType = "txt";
            }

        } catch (Exception e) {
            fileType = "txt";
        }


        path = fileId + "." + fileType;

        return fileApi.createFile(path, IoUtil.readBytes(fileStream));
    }
}