package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ContentActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;

import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class SchemeStepFactory {

    public static BaseSchemeStepEntity factory(WorkflowStepWrapperRespVO stepWrapper) {
        WorkflowStepRespVO step = Optional.ofNullable(stepWrapper.getFlowStep()).orElseThrow(() -> new RuntimeException("流程步骤不能为空！"));
        String handler = step.getHandler();
        if (ContentActionHandler.class.getSimpleName().equals(handler)) {
            ContentSchemeStepEntity contentSchemeStep = new ContentSchemeStepEntity();
            contentSchemeStep.transformSchemeStep(stepWrapper);
            return contentSchemeStep;
        }
        if (ParagraphActionHandler.class.getSimpleName().equals(handler)) {
            ParagraphSchemeStepEntity paragraphSchemeStep = new ParagraphSchemeStepEntity();
            paragraphSchemeStep.transformSchemeStep(stepWrapper);
            return paragraphSchemeStep;
        }
        if (AssembleActionHandler.class.getSimpleName().equals(handler)) {
            AssembleSchemeStepEntity assembleSchemeStep = new AssembleSchemeStepEntity();
            assembleSchemeStep.transformSchemeStep(stepWrapper);
            return assembleSchemeStep;
        }
        if (PosterActionHandler.class.getSimpleName().equals(handler)) {
            PosterSchemeStepEntity posterSchemeStep = new PosterSchemeStepEntity();
            posterSchemeStep.transformSchemeStep(stepWrapper);
            return posterSchemeStep;
        }
        throw new RuntimeException("步骤流程不支持");
    }

}
