package com.starcloud.ops.business.app.service.xhs.convert;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.xhs.content.dto.CopyWritingContent;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.api.xhs.content.dto.ImageContent;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
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

    public static CreativeContentExecuteRespVO practicalConverter(AppRespVO appResponse) {
        WorkflowConfigRespVO workflowConfig = appResponse.getWorkflowConfig();
        if (workflowConfig == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED);
        }
        List<WorkflowStepWrapperRespVO> steps = CollectionUtil.emptyIfNull(workflowConfig.getSteps());


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

        List<ImageContent> posterList = JsonUtils.parseArray(posterResponse.getAnswer(), ImageContent.class);

        JsonDataVO output = assembleResponse.getOutput();
        CopyWritingContent copyWriting = JsonUtils.parseObject(String.valueOf(output.getData()), CopyWritingContent.class);

        CreativeContentExecuteRespVO response = new CreativeContentExecuteRespVO();
        CreativeContentExecuteResult result = new CreativeContentExecuteResult();
        result.setCopyWriting(copyWriting);
        result.setImageList(posterList);

        response.setSuccess(Boolean.TRUE);
        response.setResult(result);
        return response;
    }
}
