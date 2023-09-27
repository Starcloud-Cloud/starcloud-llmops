package com.starcloud.ops.business.app.convert.image;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Mapper
public interface ImageConvert {

    ImageConvert INSTANCE = Mappers.getMapper(ImageConvert.class);

    /**
     * VSearchImage 转 ImageDTO
     *
     * @param vSearchImage vSearchImage
     * @return ImageDTO
     */
    ImageDTO convert(VSearchImage vSearchImage);

    /**
     * ClipDropImage 转为 ImageDTO
     *
     * @param clipDropImage clipDrop image
     * @return image
     */
    default ImageDTO convert(ClipDropImage clipDropImage) {
        ImageDTO image = new ImageDTO();
        image.setUuid(clipDropImage.getUuid());
        image.setMediaType(clipDropImage.getMediaType());
        image.setUrl(clipDropImage.getUrl());
        return image;
    }

    /**
     * List<VSearchImage> 转 List<ImageDTO>
     *
     * @param vSearchImageList vSearchImageList
     * @return List<ImageDTO>
     */
    default List<ImageDTO> convert(List<VSearchImage> vSearchImageList) {
        return CollectionUtil.emptyIfNull(vSearchImageList).stream().filter(Objects::nonNull).map(this::convert).collect(Collectors.toList());
    }
}
