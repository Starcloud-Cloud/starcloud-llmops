package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServerException;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.core.client.AliyunOssClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
public class CreativeUploadUtils {

    /**
     * OSS客户端
     */
    private static final AliyunOssClient ALIYUN_OSS_CLIENT = SpringUtil.getBean(AliyunOssClient.class);

    /**
     * 上传图片
     *
     * @param image    图片
     * @param pathType 上传路径类型
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImage(MultipartFile image, String pathType) {
        try {
            // 获取图片字节数组
            byte[] content = IOUtils.toByteArray(image.getInputStream());
            UploadImageInfoDTO imageInfo = uploadImage(image.getOriginalFilename(), pathType, content);
            log.info("图片上传成功：图片信息: {}", JSONUtil.toJsonStr(imageInfo));
            return imageInfo;
        } catch (ServiceException exception) {
            log.error("图片上传失败：{}", exception.getMessage(), exception);
            throw exception;
        } catch (IOException exception) {
            log.error("图片上传失败：{}", exception.getMessage(), exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_IO_FAILURE);
        } catch (Exception exception) {
            log.error("图片上传失败：{}", exception.getMessage(), exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_FAILURE, exception.getMessage());
        }
    }

    /**
     * 上传图片
     *
     * @param imageName 图片名称
     * @param pathType  上传路径类型
     * @param content   图片
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImage(String imageName, String pathType, byte[] content) {

        // 图片名称校验
        if (StringUtils.isBlank(imageName)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_NAME_REQUIRED);
        }

        // 图片格式校验
        String extension = ImageUploadUtils.getExtension(imageName);
        ImageUploadUtils.validateUploadImageSuffix(extension);

        try {
            // 获取图片的信息
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(content));
            if (Objects.isNull(bufferedImage)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
            }

            // 生成文件名称
            String uuid = IdUtil.fastSimpleUUID();
            String filename = uuid + "." + extension;
            String failPath = ImageUploadUtils.getUploadPathMap().get(pathType) + filename;
            if (StringUtils.isBlank(failPath)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_PATH_NON_EXISTENT);
            }
            // 上传图片并且返回图片URL
            String url = ALIYUN_OSS_CLIENT.upload(failPath, content);

            // 组装返回结果
            UploadImageInfoDTO imageInfo = new UploadImageInfoDTO();
            imageInfo.setUuid(uuid);
            imageInfo.setName(filename);
            imageInfo.setOriginalFilename(filename);
            imageInfo.setMediaType(ImageUploadUtils.getMediaTypeByExtension(extension));
            imageInfo.setUrl(url);
            imageInfo.setWidth(bufferedImage.getWidth());
            imageInfo.setHeight(bufferedImage.getHeight());
            return imageInfo;
        } catch (ServerException exception) {
            throw exception;
        } catch (IOException exception) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_IO_FAILURE);
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_FAILURE, exception.getMessage());
        }
    }

}
