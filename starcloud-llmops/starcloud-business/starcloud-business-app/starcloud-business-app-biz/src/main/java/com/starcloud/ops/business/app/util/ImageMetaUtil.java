package com.starcloud.ops.business.app.util;

import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.enums.vsearch.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理图片元数据工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
public class ImageMetaUtil {

    /**
     * 获取 samples
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> samplesList() {
        return Arrays.stream(SamplesEnum.values())
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 imageSize
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> imageSizeList() {
        return Arrays.stream(ImageSizeEnum.values())
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription(), item.getScale()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 guidancePreset
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> guidancePresetList() {
        return Arrays.stream(GuidancePresetEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 sampler
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> samplerList() {
        return Arrays.stream(SamplerEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 stylePreset
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> stylePresetList() {
        return Arrays.stream(StylePresetEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code        枚举值
     * @param name        名称
     * @param description 描述
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO of(Object code, String name, String description) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(name);
        meta.setDescription(description);
        return meta;
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code        枚举值
     * @param name        名称
     * @param description 描述
     * @param scale       图片比例
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO of(Object code, String name, String description, String scale) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(name);
        meta.setDescription(description);
        meta.setScale(scale);
        return meta;
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code            枚举值
     * @param nameCode        名称国际化code
     * @param descriptionCode 描述国际化code
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO ofByMessage(Object code, String nameCode, String descriptionCode, String image) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(MessageUtil.getMessage(nameCode));
        meta.setDescription(MessageUtil.getMessage(descriptionCode));
        meta.setImage(image);
        return meta;
    }
}
