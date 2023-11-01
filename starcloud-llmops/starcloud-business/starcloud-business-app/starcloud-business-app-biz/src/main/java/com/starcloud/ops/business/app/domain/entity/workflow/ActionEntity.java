package com.starcloud.ops.business.app.domain.entity.workflow;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /**
     * 获取 value
     *
     * @return value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getValue() {
        return ObjectUtil.isNotEmpty(this.getResponse()) ? this.getResponse().getAnswer() : null;
    }

    /**
     * Action 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
    }
}
