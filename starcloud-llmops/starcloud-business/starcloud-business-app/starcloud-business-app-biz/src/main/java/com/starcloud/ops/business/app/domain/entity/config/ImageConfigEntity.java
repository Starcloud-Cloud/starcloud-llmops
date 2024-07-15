package com.starcloud.ops.business.app.domain.entity.config;

import com.starcloud.ops.business.app.api.image.vo.request.BaseImageRequest;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import lombok.Data;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class ImageConfigEntity extends BaseConfigEntity {

    /**
     * 具体配置信息
     */
    private BaseImageRequest info;

    /**
     * 基础数据校验
     */
    @Override
    public void validate(ValidateTypeEnum validateType) {

    }

}
