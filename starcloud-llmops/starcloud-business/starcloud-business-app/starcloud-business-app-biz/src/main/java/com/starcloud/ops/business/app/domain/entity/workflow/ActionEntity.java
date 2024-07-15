package com.starcloud.ops.business.app.domain.entity.workflow;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import lombok.Data;

/**
 * 基础 action 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ActionEntity extends BaseActionEntity {

    private static final long serialVersionUID = -5714733426396289571L;

    /**
     * 获取 value
     *
     * @return value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getValue() {
        return ObjectUtil.isNotEmpty(this.getResponse()) ? this.getResponse().getAnswer() : "";
    }

    /**
     * 获取 value
     *
     * @return value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getOutput() {
        return ObjectUtil.isNotEmpty(this.getResponse()) ? ObjectUtil.isNotEmpty(this.getResponse().getOutput()) ? this.getResponse().getOutput().getData() : null : null;
    }


    /**
     * Action 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate(ValidateTypeEnum validateType) {
    }
}
