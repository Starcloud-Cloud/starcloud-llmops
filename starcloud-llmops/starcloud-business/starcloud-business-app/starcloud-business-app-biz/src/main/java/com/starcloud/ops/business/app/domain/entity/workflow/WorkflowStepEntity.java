package com.starcloud.ops.business.app.domain.entity.workflow;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 工作流步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WorkflowStepEntity extends ActionEntity {

    private static final long serialVersionUID = -6835618372581251185L;

    /**
     * 步骤版本，默认版本 1
     */
    private Integer version;

    /**
     * 步骤图标
     */
    private String icon;

    /**
     * 步骤标签
     */
    private List<String> tags;

    /**
     * 步骤场景
     */
    private List<String> scenes;

    /**
     * 模版变量
     */
    private VariableEntity variable;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    private Boolean isCanEditStep;

    /**
     * Action 校验
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(String stepId, ValidateTypeEnum validateType) {
        List<Verification> verifications = new ArrayList<>();
        VerificationUtils.notNullStep(verifications, this.getHandler(), stepId, "应用不知处理器不能为空，请重试或联系管理员！");
        VerificationUtils.notNullStep(verifications, this.getVariable(), stepId, "应用步骤模型变量不存在!");
        if (Objects.isNull(this.variable)) {
            return verifications;
        }
        List<Verification> validateList = this.variable.validate(stepId, validateType);
        verifications.addAll(validateList);
        return verifications;
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(String field) {
        if (StringUtils.isBlank(field) || Objects.isNull(variable)) {
            return null;
        }
        return variable.getItem(field);
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
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String field) {
        if (StringUtils.isBlank(field) || Objects.isNull(variable)) {
            return null;
        }
        return variable.getVariable(field);
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
        if (StringUtils.isBlank(field) || Objects.isNull(variable)) {
            return;
        }
        variable.putVariable(field, value);
    }

}
