package com.starcloud.ops.business.app.domain.entity.variable;

import lombok.Data;

import java.util.List;

/**
 * App 变量实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class VariableEntity {

    /**
     * 应用变量
     */
    private List<VariableItemEntity> variables;


}
