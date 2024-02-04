package com.starcloud.ops.business.app.service.xhs.convert;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.xhs.execute.CreativeAppExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.PosterImageDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.TitleActionHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.framework.common.api.util.StringUtil;

import java.util.List;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class AppResponseConverter {

    public static CreativeAppExecuteResponse practicalConverter(AppRespVO appResponse) {
        WorkflowConfigRespVO workflowConfig = appResponse.getWorkflowConfig();
        if (workflowConfig == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED);
        }
        List<WorkflowStepWrapperRespVO> steps = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

        Optional<WorkflowStepWrapperRespVO> titleOption = steps.stream().filter(item -> TitleActionHandler.class.getSimpleName().equals(item.getFlowStep().getHandler())).findFirst();
        if (!titleOption.isPresent()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "标题步骤未找到！"));
        }
        WorkflowStepWrapperRespVO titleStepWrapper = titleOption.get();
        ActionResponseRespVO titleResponse = titleStepWrapper.getFlowStep().getResponse();
        if (titleResponse == null || !titleResponse.getSuccess() || StringUtil.isBlank(titleResponse.getAnswer())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "标题结果未找到！"));
        }


        Optional<WorkflowStepWrapperRespVO> assembleOption = steps.stream().filter(item -> AssembleActionHandler.class.getSimpleName().equals(item.getFlowStep().getHandler())).findFirst();
        if (!assembleOption.isPresent()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "组装步骤未找到！"));
        }
        WorkflowStepWrapperRespVO assembleStepWrapper = assembleOption.get();
        ActionResponseRespVO assembleResponse = assembleStepWrapper.getFlowStep().getResponse();
        if (assembleResponse == null || !assembleResponse.getSuccess() || StringUtil.isBlank(assembleResponse.getAnswer())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "组装结果未找到！"));
        }

        Optional<WorkflowStepWrapperRespVO> posterOption = steps.stream().filter(item -> PosterActionHandler.class.getSimpleName().equals(item.getFlowStep().getHandler())).findFirst();
        if (!posterOption.isPresent()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "海报步骤未找到！"));
        }
        WorkflowStepWrapperRespVO posterStepWrapper = posterOption.get();
        ActionResponseRespVO posterResponse = posterStepWrapper.getFlowStep().getResponse();
        if (posterResponse == null || !posterResponse.getSuccess() || StringUtil.isBlank(posterResponse.getAnswer())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310100320, "海报结果未找到！"));
        }

        List<PosterImageDTO> posterList = JSONUtil.toList(posterResponse.getAnswer(), PosterImageDTO.class);

        CopyWritingContentDTO copyWritingContent = new CopyWritingContentDTO();
        copyWritingContent.setTitle(titleResponse.getAnswer());
        copyWritingContent.setContent(assembleResponse.getAnswer());

        CreativeAppExecuteResponse response = new CreativeAppExecuteResponse();
        response.setSuccess(Boolean.TRUE);
        response.setCopyWritingContent(copyWritingContent);
        response.setPosterList(posterList);
        return response;
    }
}
