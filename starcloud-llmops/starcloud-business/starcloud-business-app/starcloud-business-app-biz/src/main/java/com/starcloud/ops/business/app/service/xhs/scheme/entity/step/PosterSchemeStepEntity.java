package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.poster.PosterStyleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
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
public class PosterSchemeStepEntity extends BaseSchemeStepEntity {


    private static final long serialVersionUID = 1488877429722884016L;

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

    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        this.styleList = Collections.singletonList(PosterStyleEntity.ofOne());
    }
}
