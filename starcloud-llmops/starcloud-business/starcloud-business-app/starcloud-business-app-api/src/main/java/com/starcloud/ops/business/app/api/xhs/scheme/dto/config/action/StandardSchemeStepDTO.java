package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.reference.ReferenceSchemeDTO;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class StandardSchemeStepDTO extends BaseSchemeStepDTO {

    private static final long serialVersionUID = 2298470913179114149L;

    /**
     * 创作方案生成模式
     */
    @Schema(description = "创作方案生成模式")
    private String model;

    /**
     * 创作方案参考内容
     */
    @Schema(description = "创作方案参考内容")
    private List<ReferenceSchemeDTO> referList;

    /**
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;

    /**
     * 创作方案步骤变量
     */
    @Schema(description = "创作方案步骤变量")
    private List<VariableItemDTO> variableList;

    /**
     * 校验
     */
    @Override
    public void validate() {
        if (StrUtil.isBlank(model)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "生成模式不能为空！"));
        }
        // 非自定义模式，参考文案不能为空
        if (!CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(model)) {
            if (CollectionUtil.isEmpty(referList)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "参考内容不能为空！"));
            }
        }
        // 自定义模式下，要求不能为空
        if (CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(model)) {
            if (StrUtil.isBlank(requirement)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "生成要求不能为空！"));
            }
        }
    }

    /**
     * 简化
     */
    @Override
    public void easy() {
        this.model = null;
        this.referList = null;
        this.requirement = null;
    }
}