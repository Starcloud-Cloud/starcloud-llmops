package com.starcloud.ops.business.app.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-26
 */
@SuppressWarnings("all")
@Slf4j
public class ImageUploadUtils {

    /**
     * UPLOAD 上传路径
     */
    public static final String UPLOAD = "UPLOAD";

    /**
     * GENERATE 上传路径
     */
    public static final String GENERATE = "GENERATE";

    /**
     * 上传路径Map
     */
    private static final Map<String, String> UPLOAD_PATH_MAP = new ConcurrentHashMap<>();

    /**
     * 支持上传的图片的媒体类型
     */
    private static final List<String> SUPPORTED_MEDIA_TYPE_LIST = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/webp");

    /**
     * 支持上传的图片的后缀
     */
    private static final List<String> SUPPORTED_SUFFIX_LIST = Arrays.asList("png", "jpg", "jpeg", "webp");

    /**
     * 文件服务
     */
    private static final FileApi FILE_API = SpringUtil.getBean(FileApi.class);


    static {
        UPLOAD_PATH_MAP.put(UPLOAD, "mofaai/images/upload/");
        UPLOAD_PATH_MAP.put(GENERATE, "mofaai/images/ai-generation/");
    }

    /**
     * 校验图片媒体类型是否符合要求
     *
     * @param imageType 图片媒体类型
     * @return
     */
    public static void validateUploadImageMediaType(String imageType) {
        if (!SUPPORTED_MEDIA_TYPE_LIST.contains(imageType)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UNSUPPORTED_IMAGE_TYPES, imageType, SUPPORTED_MEDIA_TYPE_LIST);
        }
    }

    /**
     * 校验图片后缀是否符合要求
     *
     * @param imageSuffix 图片后缀
     * @return 是否符合要求
     */
    public static void validateUploadImageSuffix(String imageSuffix) {
        imageSuffix = imageSuffix.toLowerCase();
        if (!SUPPORTED_SUFFIX_LIST.contains(imageSuffix)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UNSUPPORTED_IMAGE_TYPES, imageSuffix, SUPPORTED_SUFFIX_LIST);
        }
    }

    /**
     * 上传图片
     *
     * @param content  内容
     * @param metaType 图片类型
     * @return url
     */
    public static String upload(String imageName, String pathType, byte[] content) {
        // 图片格式校验
        String extension = getExtension(imageName);
        validateUploadImageSuffix(extension);

        String failPath = UPLOAD_PATH_MAP.get(pathType) + imageName;
        if (StringUtils.isBlank(failPath)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_PATH_NON_EXISTENT);
        }
        // 上传图片并且返回图片URL
        return FILE_API.createFile(imageName, failPath, content);
    }

    /**
     * 上传图片
     *
     * @param uuid      图片唯一标识
     * @param mediaType 图片媒体类型
     * @param pathType  上传路径类型
     * @param content   图片内容
     * @return url
     */
    public static String upload(String uuid, String mediaType, String pathType, byte[] content) {

        validateUploadImageMediaType(mediaType);

        // 图片格式校验
        String extension = getExtensionByMediaType(mediaType);
        validateUploadImageSuffix(extension);

        String filename = uuid + "." + extension;
        String failPath = UPLOAD_PATH_MAP.get(pathType) + filename;
        if (StringUtils.isBlank(failPath)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_PATH_NON_EXISTENT);
        }
        // 上传图片并且返回图片URL
        return FILE_API.createFile(filename, failPath, content);
    }

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
            UploadImageInfoDTO imageInfo = uploadImage(image.getOriginalFilename(), pathType, content, null, null);
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
     * @param image    图片
     * @param pathType 上传路径类型
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImageLimit1024(MultipartFile image, String pathType) {
        try {
            // 获取图片字节数组
            byte[] content = IOUtils.toByteArray(image.getInputStream());
            UploadImageInfoDTO imageInfo = uploadImage(image.getOriginalFilename(), pathType, content, 1048576, "1024x1024");
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
     * @param content   图片内容
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImage(String imageName, String pathType, byte[] content) {
        return uploadImage(imageName, pathType, content, null, null);
    }

    /**
     * 上传图片
     *
     * @param imageName 图片名称
     * @param pathType  上传路径类型
     * @param content   图片内容
     * @return 图片信息
     */
    public static UploadImageInfoDTO uploadImage(String imageName, String pathType, byte[] content, Integer limitPixel, String limitMessage) {

        // 图片名称校验
        if (StringUtils.isBlank(imageName)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_NAME_REQUIRED);
        }

        // 图片格式校验
        String extension = getExtension(imageName);
        validateUploadImageSuffix(extension);

        try {
            // 获取图片的信息
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(content));
            if (Objects.isNull(bufferedImage)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
            }

            if (Objects.nonNull(limitPixel) && (bufferedImage.getWidth() * bufferedImage.getWidth()) > limitPixel) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_PIXEL_LIMIT_FAILURE, limitMessage, limitPixel);
            }

            // 生成文件名称
            String uuid = IdUtil.fastSimpleUUID();
            String filename = uuid + "." + extension;
            String failPath = UPLOAD_PATH_MAP.get(pathType) + filename;
            if (StringUtils.isBlank(failPath)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_PATH_NON_EXISTENT);
            }
            // 上传图片并且返回图片URL
            String url = FILE_API.createFile(filename, failPath, content);

            // 组装返回结果
            UploadImageInfoDTO imageInfo = new UploadImageInfoDTO();
            imageInfo.setUuid(uuid);
            imageInfo.setName(filename);
            imageInfo.setOriginalFilename(filename);
            imageInfo.setMediaType(getMediaTypeByExtension(extension));
            imageInfo.setUrl(url);
            imageInfo.setWidth(bufferedImage.getWidth());
            imageInfo.setHeight(bufferedImage.getHeight());
            return imageInfo;
        } catch (ServiceException exception) {
            throw exception;
        } catch (IOException exception) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_IO_FAILURE);
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.UPLOAD_IMAGE_FAILURE, exception.getMessage());
        }
    }

    /**
     * 获取图片信息
     *
     * @param imageUrl 图片信息
     * @return 图片文件
     */
    public static byte[] getContent(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return FILE_API.getContent(path);
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GET_IMAGE_FAILURE, exception.getMessage());
        }
    }

    /**
     * 获取图片信息
     *
     * @param imageUrl 图片信息
     * @return 图片文件
     */
    public static File getImageFile(String imageUrl) {
        return FileUtil.writeBytes(getContent(imageUrl), FileUtil.createTempFile());
    }

    /**
     * 获取图片信息
     *
     * @param imageUrl 图片信息
     * @return 图片文件
     */
    public static File getImageFile(byte[] imageBytes) {
        return FileUtil.writeBytes(imageBytes, FileUtil.createTempFile());
    }

    /**
     * 获取图片信息
     *
     * @param imageUrl 图片信息
     * @return 图片文件
     */
    public static BufferedImage getBufferedImage(String imageUrl) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(getContent(imageUrl)));
            if (bufferedImage == null) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
            }
            return bufferedImage;
        } catch (IOException e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
        }
    }

    /**
     * 获取文件的后缀名
     *
     * @param filename 文件名称
     * @return 后缀名
     */
    public static String getExtension(String filename) {
        // 获取图片的后缀名
        String extension = StringUtils.substringAfterLast(filename, ".");
        if (StringUtils.isBlank(extension)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.FILE_EXTENSION_FAILURE);
        }
        return extension.toLowerCase();
    }

    /**
     * 处理图片为 64 的倍数
     *
     * @param image 图片
     * @return 处理后的图片
     */
    public static BufferedImage graphicsImage64x(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 宽度和高度都是64的倍数，直接返回
        if (width % 64 == 0 && height % 64 == 0) {
            return image;
        }

        int newWidth = roundToMultipleOf64(width);
        int newHeight = roundToMultipleOf64(height);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
        newImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);
        return newImage;
    }

    /**
     * 处理图片为 64 的倍数
     *
     * @param url 图片地址
     * @return 处理后的图片
     */
    public static byte[] graphicsImage64xToByteArray(String url) {
        BufferedImage bufferedImage = getBufferedImage(url);
        BufferedImage newImage = graphicsImage64x(bufferedImage);

        // 获取图片的字节数组
        byte[] bytes = bufferedImageToByteArray(newImage, getExtension(url));
        log.info("原图转为64倍数成功：width: {}, height: {}", newImage.getWidth(), newImage.getHeight());
        // String uploadUrl = upload(IdUtil.fastSimpleUUID(), getMediaTypeByExtension(getExtension(url)), UPLOAD, bytes);
        // log.info("处理后的图片上传成功：图片信息: {}", JSONUtil.toJsonStr(uploadUrl));
        return bytes;
    }

    /**
     * bufferedImage 转 byte[]
     *
     * @param image      图片
     * @param formatName 格式
     * @return 字节数组
     */
    public static byte[] bufferedImageToByteArray(BufferedImage image, String formatName) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_INFO_FAILURE);
        }
    }

    /**
     * 处理图片并且转换为 base64
     *
     * @param url 图片地址
     * @return 处理后的图片
     */
    public static String handleImageToBase64(String url) {
        BufferedImage bufferedImage = getBufferedImage(url);
        BufferedImage image = scaling1024(bufferedImage);
//        String uploadUrl = upload(IdUtil.fastSimpleUUID(), getMediaTypeByExtension(getExtension(url)), UPLOAD, bufferedImageToByteArray(image, getExtension(url)));
//        log.info("处理后的图片上传成功：图片信息: {}", JSONUtil.toJsonStr(uploadUrl));
        BufferedImage newImage = graphicsImage64x(image);
        // 获取图片的字节数组
        byte[] bytes = bufferedImageToByteArray(newImage, getExtension(url));
        log.info("原图转为64倍数成功：width: {}, height: {}", newImage.getWidth(), newImage.getHeight());
        // String uploadUrl = upload(IdUtil.fastSimpleUUID(), getMediaTypeByExtension(getExtension(url)), UPLOAD, bytes);
        // log.info("处理后的图片上传成功：图片信息: {}", JSONUtil.toJsonStr(uploadUrl));
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return ImageUtils.handlerBase64Image(base64);
    }

    /**
     * 处理图片为 64 的倍数
     *
     * @param value 宽度或高度
     * @return 64 的倍数
     */
    private static int roundToMultipleOf64(int value) {
        int remainder = value % 64;
        if (remainder == 0) {
            return value; // 宽度或高度已经是64的倍数
        } else {
            int addValue = value + 64 - remainder;
            if (addValue > 1024) {
                return value - remainder; // 调整为上一个64的倍数
            }
            return addValue; // 调整为下一个64的倍数
        }
    }

    /**
     * 图片按比例所放到 1024， 小于 1024 * 1024 不做任何处理
     *
     * @param bufferedImage 原始图片
     * @return 处理之后的图片
     */
    private static BufferedImage scaling1024(BufferedImage originalImage) {
        if (originalImage.getWidth() * originalImage.getHeight() > 1048576) {
            BigDecimal scaleing = new BigDecimal("1024");
            BigDecimal heightBigDecimal = new BigDecimal(String.valueOf(originalImage.getHeight()));
            BigDecimal widthBigDecimal = new BigDecimal(String.valueOf(originalImage.getWidth()));
            int newWidth = 0;
            int newHeight = 0;
            // 如果宽大于高，则宽度缩放为1024，高度按照比例缩放
            if (originalImage.getWidth() > originalImage.getHeight()) {
                newWidth = 1024;
                newHeight = heightBigDecimal.multiply(scaleing).divide(widthBigDecimal, 0, BigDecimal.ROUND_HALF_UP).intValue();
            } else {
                // 如果高大于宽，则高度缩放为1024，宽度按照比例缩放
                newHeight = 1024;
                newWidth = widthBigDecimal.multiply(scaleing).divide(heightBigDecimal, 0, BigDecimal.ROUND_HALF_UP).intValue();
            }
            // 按照比例缩放图片
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
            newImage.getGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            log.info("缩放图片成功：新图片宽度：{}, 高度：{}, 原始图片宽度：{}, 高度：{}", newWidth, newHeight, originalImage.getWidth(), originalImage.getHeight());
            return newImage;
        }
        log.info("原始图片宽度：{}, 高度：{}，小于 1024 x 1024，不做缩放处理！", originalImage.getWidth(), originalImage.getWidth());
        return originalImage;
    }

    /**
     * 根据 mediaType 获取后缀
     *
     * @param mediaType
     * @return 后缀
     */
    private static String getExtensionByMediaType(String mediaType) {
        if ("image/png".equals(mediaType)) {
            return "png";
        }
        if (mediaType.equals("image/jpg") || "image/jpeg".equals(mediaType)) {
            return "jpeg";
        }
        if ("image/webp".equals(mediaType)) {
            return "webp";
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.UNSUPPORTED_IMAGE_TYPES, mediaType, SUPPORTED_MEDIA_TYPE_LIST);
    }

    /**
     * 根据后缀获取媒体类型
     *
     * @param extension 后缀
     * @return 媒体类型
     */
    private static String getMediaTypeByExtension(String extension) {
        if ("png".equals(extension)) {
            return "image/png";
        }
        if (extension.equals("jpg")) {
            return "image/jpg";
        }
        if ("jpeg".equals(extension)) {
            return "image/jpeg";
        }
        if ("webp".equals(extension)) {
            return "image/webp";
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.UNSUPPORTED_IMAGE_TYPES, extension, SUPPORTED_MEDIA_TYPE_LIST);
    }

}
