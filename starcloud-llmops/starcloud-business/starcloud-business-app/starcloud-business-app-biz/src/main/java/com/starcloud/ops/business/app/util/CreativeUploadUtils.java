package com.starcloud.ops.business.app.util;

import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.OSS;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
public class CreativeUploadUtils {

    private static final String BUCKET_NAME = "mofaai-poster";

    /**
     * OSS客户端
     */
    private static final OSS OSS_CLIENT = SpringUtil.getBean(OSS.class);

    /**
     * 上传图片
     *
     * @param imageName 图片名称
     * @param pathType  上传路径类型
     * @param content   图片内容
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImage(String imageName, String pathType, byte[] content) {

//        // 图片名称校验
//        if (StringUtils.isBlank(imageName)) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_NAME_REQUIRED);
//        }
//
//        // 图片格式校验
//        String extension = ImageUploadUtils.getExtension(imageName);
//        ImageUploadUtils.validateUploadImageSuffix(extension);
//
//        try {
//            // 获取图片的信息
//            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(content));
//            if (Objects.isNull(bufferedImage)) {
//                throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
//            }
//
//            // 生成文件名称
//            String uuid = IdUtil.fastSimpleUUID();
//            String filename = uuid + "." + extension;
//            String failPath = UPLOAD_PATH_MAP.get(pathType) + filename;
//            if (StringUtils.isBlank(failPath)) {
//                throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_PATH_NON_EXISTENT);
//            }
//            // 上传图片并且返回图片URL
//            PutObjectResult result = OSS_CLIENT.putObject(BUCKET_NAME, failPath, new ByteArrayInputStream(content));
//
//            // 组装返回结果
//            UploadImageInfoDTO imageInfo = new UploadImageInfoDTO();
//            imageInfo.setUuid(uuid);
//            imageInfo.setName(filename);
//            imageInfo.setOriginalFilename(filename);
//            imageInfo.setMediaType(ImageUploadUtils.getMediaTypeByExtension(extension));
//            imageInfo.setUrl(url);
//            imageInfo.setWidth(bufferedImage.getWidth());
//            imageInfo.setHeight(bufferedImage.getHeight());
//            return imageInfo;
//        } catch (ServiceException exception) {
//            throw exception;
//        } catch (IOException exception) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_IO_FAILURE);
//        } catch (Exception exception) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_FAILURE, exception.getMessage());
//        }
        return null;
    }

}
