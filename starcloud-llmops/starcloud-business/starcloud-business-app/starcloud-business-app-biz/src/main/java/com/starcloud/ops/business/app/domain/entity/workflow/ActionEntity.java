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
     * 获取 action 节点返回的结构
     *
     * @return value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getOutputJsonSchema() {
        String jsonSchema = ObjectUtil.isNotEmpty(this.getResponse()) ? ObjectUtil.isNotEmpty(this.getResponse().getOutput()) ? this.getResponse().getOutput().getJsonSchema() : null : null;

        //@todo 还是需要做精简处理，完整的jsonSchema 太多了
        return jsonSchema;
    }

    /**
     * Action 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
    }
}
