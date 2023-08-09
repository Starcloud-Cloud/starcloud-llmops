package com.starcloud.ops.business.dataset.core.handler.strategy;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.core.handler.UploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FileUploadStrategy implements UploadStrategy {


    @Resource
    private FileApi fileApi;


    // 成员变量用于保存MultipartFile对象
    private MultipartFile uploadFile;
    private byte[] fileContent;

    private static final String PATH_OBJECT = "dataset-source-data/";

    // Setter方法，用于接收MultipartFile对象
    public void setFileData(MultipartFile file, byte[] fileContent) {
        this.fileContent = fileContent;
        this.uploadFile = file;
    }


    /**
     * 实现文件上传的处理逻辑
     *
     * @return UploadFileRespDTO
     */
    @Override
    public UploadResultDTO process(Long userId) {
        log.info("====> 文件开始上传 ,初始化数据状态为 false");
        UploadResultDTO uploadFileRespDTO = new UploadResultDTO();

        // 获取文件的name
        String name = uploadFile.getOriginalFilename();
        // 读取输入流到临时缓冲区
        name = name.isEmpty() ? "文件" + IdUtil.fastSimpleUUID().substring(0, 8) : name;
        // 设置文件名称
        uploadFileRespDTO.setName(name);

        // 获取文件size
        long size = uploadFile.getSize();
        // 设置文件大小
        uploadFileRespDTO.setSize(size);
        // 生成文件ID - 使用 URL SecureUtil.md5 会关闭流
        String fileId = null;

        fileId = SecureUtil.md5(new ByteArrayInputStream(fileContent));

        String filePath = null;

        String extension = getExtension(name);
        try {

            // 上传文件
            filePath = uploadFile(fileId, fileContent, extension, userId);
            // 设置文件地址
            uploadFileRespDTO.setFilepath(filePath);
            uploadFileRespDTO.setStatus(true);
        } catch (Exception e) {
            log.error("====> 文件开始上传失败 ,返回数据状态为 false");
            return uploadFileRespDTO;
            // throw exception(SOURCE_DATA_UPLOAD_URL_ERROR);
        }

        // 设置扩展名
        uploadFileRespDTO.setExtension(extension);

        // 设置MimeType
        uploadFileRespDTO.setMimeType(getMimeType(extension));

        // 设置字符数

        uploadFileRespDTO.setCharacterCount(getFileCharacterCount(fileContent));

        return uploadFileRespDTO;
    }


    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param fileId    文件 ID
     * @param fileByte  文件流
     * @param extension 文件 扩展名
     * @return 文件路径
     */
    private String uploadFile(String fileId, byte[] fileByte, String extension, Long userId) {

        String fileName = fileId + "." + extension;
        String path = String.format(PATH_OBJECT + "%s" + "/", userId) + fileName;

        return fileApi.createFile(fileName, path, fileByte);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    private static String getExtension(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 获取文件MimeType
     *
     * @param extension
     * @return
     */
    private static String getMimeType(String extension) {
        switch (extension) {
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }


    private static Long getFileCharacterCount(byte[] inputStream) {
        long characterCount = 0;

        try {
            // 将MultipartFile对象中的内容读取成字符串
            String content = new String(inputStream, StandardCharsets.UTF_8);
            // 计算字符串的字符数
            characterCount = content.length();
            return characterCount;
        } catch (Exception e) {
            return characterCount;
        }

    }

}