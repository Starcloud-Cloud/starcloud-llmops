package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 应用请求 action 请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用请求 action 请求对象")
public class ActionRespVO implements Serializable {

    private static final long serialVersionUID = 5848613340472242430L;

    /**
     * 动作名称
     */
    @Schema(description = "动作(step)名称")
    private String name;

    /**
     * 步骤类型
     */
    @Schema(description = "步骤类型")
    private String type;

    /**
     * 动作处理器
     */
    @Schema(description = "动作(step)处理器")
    private String handler;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    private VariableRespVO variable;

    /**
     * 动作执行结果
     */
    @Schema(description = "动作(step)执行结果")
    private ActionResponseRespVO response;

    /**
     * 动作描述
     */
    @Schema(description = "动作(step)描述")
    private String description;


    /**
     * 步骤标签
     */
    @Schema(description = "步骤标签")
    private List<String> tags;

    /**
     * 步骤场景
     */
    @Schema(description = "步骤场景")
    private List<String> scenes;


    /**
     * 步骤图标
     */
    @Schema(description = "步骤图标")
    private String icon;


    /**
     * 根据模型变量的{@code field}获取变量
     *
     * @param field 变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getModelVariableItem(String field) {
        if (Objects.isNull(this.variable)) {
            return null;
        }
        return this.variable.getItem(field);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getModelVariableToString(String field) {
        if (Objects.isNull(this.variable)) {
            return StringUtils.EMPTY;
        }
        return this.variable.getVariableToString(field);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String field) {
        if (Objects.isNull(this.variable)) {
            return null;
        }
        return this.variable.getVariable(field);
    }

    /**
     * 将模型变量为{@code field}的值设置为{@code value}
     *
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String field, Object value) {
        if (Objects.isNull(this.variable)) {
            return;
        }
        this.variable.putVariable(field, value);
    }

}
