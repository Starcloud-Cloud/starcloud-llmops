package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.poster.PosterStyleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PosterSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 1488877429722884016L;

    /**
     * 海报生成模式
     */
    @Schema(description = "海报生成模式")
    private String mode;

    /**
     * 创作方案步骤图片风格
     */
    @Schema(description = "创作方案步骤图片风格")
    private List<PosterStyleEntity> styleList;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        stepWrapper.putVariable(Collections.singletonMap(CreativeConstants.POSTER_MODE, this.mode));
        stepWrapper.putVariable(Collections.singletonMap(CreativeConstants.POSTER_STYLE_CONFIG, this.styleList));
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        VariableItemRespVO modeVariable = stepWrapper.getVariable(CreativeConstants.POSTER_MODE);
        this.mode = String.valueOf(Optional.ofNullable(modeVariable).map(VariableItemRespVO::getValue).orElse(PosterModeEnum.RANDOM.name()));

        VariableItemRespVO styleVariable = stepWrapper.getVariable(CreativeConstants.POSTER_STYLE_CONFIG);
        String posterStyleConfig = String.valueOf(Optional.ofNullable(styleVariable).map(VariableItemRespVO::getValue).orElse(StringUtils.EMPTY));
        if (StringUtils.isBlank(posterStyleConfig) || "[]".equals(posterStyleConfig) || "null".equals(posterStyleConfig)) {
            this.styleList = Collections.singletonList(PosterStyleEntity.ofOne());
        } else {
            this.styleList = JSONUtil.toList(posterStyleConfig, PosterStyleEntity.class);
        }

    }
}
