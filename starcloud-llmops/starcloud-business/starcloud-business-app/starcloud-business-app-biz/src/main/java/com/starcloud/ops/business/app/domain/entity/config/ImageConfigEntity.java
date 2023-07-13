package com.starcloud.ops.business.app.domain.entity.config;

import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
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
     * 变量
     */
    private VariableEntity variable;

    @Override
    public void validate() {

    }

}
