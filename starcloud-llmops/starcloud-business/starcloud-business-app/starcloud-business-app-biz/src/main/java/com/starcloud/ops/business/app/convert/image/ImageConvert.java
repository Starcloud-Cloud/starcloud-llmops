package com.starcloud.ops.business.app.convert.image;

import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Mapper
public interface ImageConvert {

    ImageConvert INSTANCE = Mappers.getMapper(ImageConvert.class);

}
