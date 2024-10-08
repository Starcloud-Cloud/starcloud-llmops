package com.starcloud.ops.business.dataset.core.handler.strategy;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.core.handler.UploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadContentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.SOURCE_DATA_UPLOAD_FAIL;

@Slf4j
@Component
public class StringUploadStrategy implements UploadStrategy {

    @Resource
    private FileApi fileApi;

    // 成员变量用于保存URL
    private String title;

    private String characters;

    private static final String PATH_OBJECT = "dataset-source-data/";


    // Setter方法，用于接收MultipartFile对象
    public void setData(String title, String characters) {
        this.title = title;
        this.characters = characters;
    }


    @Override
    public UploadContentDTO process(Long userId) {

        UploadContentDTO uploadFileRespDTO = new UploadContentDTO();

        // 获取资源名称
        String name = title;
        String character = characters;


        // 设置数据名称
        uploadFileRespDTO.setName(name);

        ByteArrayInputStream inputStream = IoUtil.toUtf8Stream(character);
        // 生成文件ID - 使用 String SecureUtil.md5 会关闭流
        String fileId = SecureUtil.md5(character);
        String filePath;
        try {
            // 上传文件
            filePath = uploadFile(fileId, character, userId);

            if (StrUtil.isBlank(filePath)) {
                return uploadFileRespDTO;
            }
            // 设置文件名称
            uploadFileRespDTO.setFilepath(filePath);
            uploadFileRespDTO.setStatus(true);
        } catch (Exception e) {
            uploadFileRespDTO.setErrCode(String.valueOf(SOURCE_DATA_UPLOAD_FAIL.getCode()));
            uploadFileRespDTO.setErrMsg(SOURCE_DATA_UPLOAD_FAIL.getMsg());
            log.error("====> 字符串上传失败,数据状态为 false", e);
            return uploadFileRespDTO;
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
     * @param fileId 文件 ID
     * @param data   文件
     * @param userId
     * @return 文件路径
     */
    private String uploadFile(String fileId, String data, Long userId) {

        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);

        String fileName = fileId + "." + "txt";
        String path = String.format(PATH_OBJECT + "%s" + "/", userId) + fileName;

        return fileApi.createFile(fileName, path, IoUtil.readBytes(utf8Stream));


    }
}