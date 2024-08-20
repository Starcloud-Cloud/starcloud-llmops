package com.starcloud.ops.business.app.domain.entity.config;

import com.starcloud.ops.business.app.api.image.vo.request.BaseImageRequest;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import lombok.Data;

import java.util.Collections;
import java.util.List;

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

    @Override
    public List<Verification> validate(String uid, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }

}
