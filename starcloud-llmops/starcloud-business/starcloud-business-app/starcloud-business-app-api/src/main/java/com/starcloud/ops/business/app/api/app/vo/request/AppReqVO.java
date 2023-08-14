package com.starcloud.ops.business.app.api.app.vo.request;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.config.ChatConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ImageConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowStepWrapperReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableItemReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * App 请求实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用基础请求实体")
public class AppReqVO implements Serializable {

    private static final long serialVersionUID = 1578944445567574534L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用不能为空")
    private String model;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用类型不能为空")
    @InEnum(value = AppTypeEnum.class, message = "应用类型[{value}]必须是: {values}")
    private String type;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "应用来源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用来源不能为空")
    @InEnum(value = AppSourceEnum.class, message = "应用来源[{value}]必须是: {values}")
    private String source;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "应用类别不能为空")
    private List<String> categories;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用图片
     */
    @Schema(description = "应用图片")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息")
    @Valid
    private WorkflowConfigReqVO workflowConfig;

    /**
     * 应用聊天配置信息
     */
    @Schema(description = "应用聊天配置信息")
    @Valid
    private ChatConfigReqVO chatConfig;

    /**
     * 生成图片配置信息
     */
    @Schema(description = "生成图片配置信息")
    @Valid
    private ImageConfigReqVO imageConfig;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    @JsonIgnore
    public void addVariables(Map<String, Object> variables) {
        WorkflowConfigReqVO config = this.getWorkflowConfig();
        if (config == null) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300002, "应用配置不能为空"));
        }
        List<WorkflowStepWrapperReqVO> steps = config.getSteps();
        if (CollectionUtil.isEmpty(steps)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300003, "应用步骤不能为空"));
        }
        WorkflowStepWrapperReqVO stepWrapperReqVO = steps.get(0);
        if (stepWrapperReqVO == null) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300004, "应用步骤不能为空"));
        }
        VariableReqVO variable = stepWrapperReqVO.getVariable();
        if (variable == null) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300005, "应用变量不能为空"));
        }
        List<VariableItemReqVO> variableList = variable.getVariables();
        if (CollectionUtil.isEmpty(variableList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300006, "应用变量不能为空"));
        }

        for (VariableItemReqVO variableItemReqVO : variableList) {
            String field = variableItemReqVO.getField();
            if (!variables.containsKey(field)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(300007, "应用配置，变量[" + field + "]是必须的，需要配置该变量信息"));
            }
            variableItemReqVO.setValue(variables.get(field));
        }

        variable.setVariables(variableList);
        stepWrapperReqVO.setVariable(variable);
        steps.set(0, stepWrapperReqVO);
        config.setSteps(steps);
        this.setWorkflowConfig(config);
    }

}
