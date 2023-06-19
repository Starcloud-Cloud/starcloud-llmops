package com.starcloud.ops.business.app.domain.entity2.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.domain.entity.AppVariableEntity;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class VariableEntity {

    /**
     * 模版变量
     */
    private List<VariableItemEntity> variables;




}
