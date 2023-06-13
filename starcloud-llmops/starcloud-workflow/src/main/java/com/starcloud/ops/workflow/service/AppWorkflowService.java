package com.starcloud.ops.workflow.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.NoticeTracking;
import cn.kstry.framework.core.util.GlobalUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppStepResponse;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.workflow.constant.WorkflowConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Slf4j
@Component
public class AppWorkflowService {


    @Autowired
    private LogAppApi logAppApi;


    @Autowired
    private StoryEngine storyEngine;

    public void fireByAppUid(String appId) {

        AppEntity app = AppFactory.factory(appId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app);

        this.fireByAppContext(appContext);
    }


    public void fireByApp(String appId, AppDTO AppDTO) {

        AppEntity app = AppFactory.factory(appId, AppDTO);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app);


        this.fireByAppContext(appContext);
    }


    public void fireByApp(String appId, AppDTO appDTO, String stepId) {

        AppEntity app = AppFactory.factory(appId, appDTO, stepId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app);
        appContext.setStepId(stepId);

        this.fireByAppContext(appContext);
    }

    public void fireByApp(String appId, AppDTO AppDTO, String stepId, HttpServletResponse httpServletResponse) {

        AppEntity app = AppFactory.factory(appId, AppDTO, stepId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app);
        appContext.setStepId(stepId);
        appContext.setHttpServletResponse(httpServletResponse);

        this.fireByAppContext(appContext);
    }


    public void fireByApp(String appId, AppDTO appDTO, String stepId, String requestId) {

        AppEntity app = AppFactory.factory(appId, appDTO, stepId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app);
        appContext.setStepId(stepId);
        appContext.setConversationId(requestId);

        this.fireByAppContext(appContext);

    }


    private void fireByAppContext(AppContext appContext) {

        StoryRequest<Void> req = ReqBuilder.returnType(Void.class)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .startId(appContext.getApp().getUid())
                .request(appContext).build();


        //@todo
        appContext.setScene("scene");

        LogAppConversationCreateReqVO conversation = this.createAppConversationLog(appContext);

        appContext.setConversationId(conversation.getUid());

        req.setRecallStoryHook(recallStory -> {
            MonitorTracking monitorTracking = recallStory.getMonitorTracking();
            List<NodeTracking> storyTracking = monitorTracking.getStoryTracking();

            log.info("recallStory: {} {} {} {}", recallStory.getBusinessId(), recallStory.getStartId(), recallStory.getResult(), recallStory.getReq());

            storyTracking.stream().filter((nodeTracking) -> BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())).forEach(nodeTracking -> {
                this.createAppMessageLog(appContext, nodeTracking);
            });

            List<String> collect = storyTracking.stream()
                    .map(nt -> GlobalUtil.format("{}({} {} {} {} {} {} {})", nt.getNodeName(), nt.getThreadId(), nt.getNodeId(), nt.getIndex(), nt.getSpendTime(), nt.getNodeName(), nt.getIterateStride(), nt.getNoticeTracking().get(0).getValue())).collect(Collectors.toList());
            System.out.println("name list: " + String.join(",", collect));
        });

        TaskResponse<Void> fire = storyEngine.fire(req);

        this.updateAppConversationLog(conversation.getUid(), fire.isSuccess());

        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());

    }

    private <T> T getTracking(List<NoticeTracking> noticeTrackings, Class<T> cls) {

        String clsName = cls.getSimpleName();

        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clsName);

        return Optional.ofNullable(noticeTrackings).orElse(new ArrayList<>()).stream().filter(noticeTracking -> noticeTracking.getFieldName().equals(field)).map(noticeTracking -> {
            return JSON.parseObject(noticeTracking.getValue(), cls);
        }).findFirst().orElse(null);
    }

    private LogAppConversationCreateReqVO createAppConversationLog(AppContext appContext) {


        LogAppConversationCreateReqVO logAppConversationCreateReqVO = new LogAppConversationCreateReqVO();

        logAppConversationCreateReqVO.setUid(appContext.getConversationId());
        logAppConversationCreateReqVO.setAppMode("completion");
        logAppConversationCreateReqVO.setStatus(LogStatusEnum.ERROR.name());

        logAppConversationCreateReqVO.setAppUid(appContext.getApp().getUid());
        logAppConversationCreateReqVO.setAppConfig(JSON.toJSONString(appContext.getApp()));

        logAppConversationCreateReqVO.setFromScene(appContext.getScene());
        logAppConversationCreateReqVO.setEndUser(appContext.getEndUser());

        logAppConversationCreateReqVO.setCreateTime(LocalDateTime.now());

        return logAppApi.createAppConversation(logAppConversationCreateReqVO);

    }


    private void updateAppConversationLog(String uid, Boolean status) {

        logAppApi.updateAppConversationStatus(uid, status ? LogStatusEnum.SUCCESS : LogStatusEnum.ERROR);

    }


    private void createAppMessageLog(AppContext appContext, NodeTracking nodeTracking) {


        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();

        String stepId = nodeTracking.getNodeName();


        nodeTracking.getParamTracking();

        messageCreateReqVO.setUid(IdUtil.fastSimpleUUID());
        messageCreateReqVO.setAppConversationUid(appContext.getConversationId());
        messageCreateReqVO.setAppUid(appContext.getApp().getUid());
        messageCreateReqVO.setAppMode("mode");
        messageCreateReqVO.setAppStep(appContext.getStepId());

        messageCreateReqVO.setCreateTime(nodeTracking.getStartTime());
        messageCreateReqVO.setUpdateTime(nodeTracking.getStartTime());

        messageCreateReqVO.setElapsed(nodeTracking.getSpendTime());

        Map<String, Object> variablesMaps = appContext.getCurrentAppStepWrapper(stepId).getContextVariablesMaps();

        messageCreateReqVO.setVariables(JSON.toJSONString(variablesMaps));
        messageCreateReqVO.setEndUser(appContext.getEndUser());
        messageCreateReqVO.setFromScene(appContext.getScene());

        if (nodeTracking.getTaskException() == null) {

            AppStepResponse appStepResponse = this.getTracking(nodeTracking.getNoticeTracking(), AppStepResponse.class);

            messageCreateReqVO.setAppConfig(JSON.toJSONString(appStepResponse.getStepConfig()));

            messageCreateReqVO.setMessage(appStepResponse.getMessage());

            messageCreateReqVO.setMessageTokens(appStepResponse.getMessageTokens().intValue());
            messageCreateReqVO.setMessageUnitPrice(appStepResponse.getMessageUnitPrice());

            messageCreateReqVO.setAnswer(appStepResponse.getAnswer());
            messageCreateReqVO.setAnswerTokens(appStepResponse.getAnswerTokens().intValue());
            messageCreateReqVO.setAnswerUnitPrice(appStepResponse.getAnswerUnitPrice());

            messageCreateReqVO.setTotalPrice(appStepResponse.getTotalPrice());
            messageCreateReqVO.setCurrency("USD");

            messageCreateReqVO.setStatus(LogStatusEnum.SUCCESS.name());

        } else {

            messageCreateReqVO.setStatus(LogStatusEnum.ERROR.name());

            //messageCreateReqVO.setErrorCode();
            messageCreateReqVO.setErrorMsg(nodeTracking.getTaskException().getMessage());
        }


        logAppApi.createAppMessage(messageCreateReqVO);
    }


}
