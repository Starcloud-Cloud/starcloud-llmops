package com.starcloud.ops.workflow.service;

import cn.hutool.core.util.IdUtil;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.NoticeTracking;
import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.workflow.constant.WorkflowConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author df007df
 */
@Slf4j
@Component
@Validated
public class AppWorkflowService {


    @Autowired
    private LogAppApi logAppApi;


    @Autowired
    private StoryEngine storyEngine;

    /**
     * 根据保存的配置直接执行，默认第一个step
     *
     * @param appId
     */
    public void fireByAppUid(String appId, AppSceneEnum scene) {

        AppEntity app = AppFactory.factory(appId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app, scene);

        this.fireByAppContext(appContext);
    }


    /**
     * 根据传入的配置 执行
     *
     * @param appId
     * @param scene
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest) {

        AppEntity app = AppFactory.factory(appId, appRequest);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app, scene);


        this.fireByAppContext(appContext);
    }


    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId) {

        AppEntity app = AppFactory.factory(appId, appRequest, stepId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app, scene);
        appContext.setStepId(stepId);

        this.fireByAppContext(appContext);
    }

    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId, HttpServletResponse httpServletResponse) {

        AppEntity app = AppFactory.factory(appId, appRequest, stepId);

        AppContext appContext = new AppContext(app, scene);
        appContext.setStepId(stepId);
        appContext.setHttpServletResponse(httpServletResponse);

        this.fireByAppContext(appContext);
    }


    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId, String requestId) {

        AppEntity app = AppFactory.factory(appId, appRequest, stepId);

        log.info("fireByAppUid app: {}", app);

        AppContext appContext = new AppContext(app, scene);
        appContext.setStepId(stepId);
        appContext.setConversationId(requestId);

        this.fireByAppContext(appContext);

    }


    private void fireByAppContext(@Valid AppContext appContext) {

        StoryRequest<Void> req = ReqBuilder.returnType(Void.class)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .startId(appContext.getApp().getUid())
                .request(appContext).build();


        LogAppConversationCreateReqVO conversation = this.createAppConversationLog(appContext);

        req.setRecallStoryHook(recallStory -> {
            MonitorTracking monitorTracking = recallStory.getMonitorTracking();
            List<NodeTracking> storyTracking = monitorTracking.getStoryTracking();

            log.info("recallStory: {} {} {} {}", recallStory.getBusinessId(), recallStory.getStartId(), recallStory.getResult(), recallStory.getReq());

            storyTracking.stream().filter((nodeTracking) -> BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())).forEach(nodeTracking -> {
                this.createAppMessageLog(appContext, nodeTracking);
            });

//            List<String> collect = storyTracking.stream()
//                    .map(nt -> GlobalUtil.format("{}({} {} {} {} {} {} {})", nt.getNodeName(), nt.getThreadId(), nt.getNodeId(), nt.getIndex(), nt.getSpendTime(), nt.getNodeName(), nt.getIterateStride(), nt.getNoticeTracking().get(0).getValue())).collect(Collectors.toList());
//            System.out.println("name list: " + String.join(",", collect));
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
        logAppConversationCreateReqVO.setAppMode(appContext.getApp().getModel());
        logAppConversationCreateReqVO.setAppName(appContext.getApp().getName());

        logAppConversationCreateReqVO.setStatus(LogStatusEnum.ERROR.name());

        logAppConversationCreateReqVO.setAppUid(appContext.getApp().getUid());
        logAppConversationCreateReqVO.setAppConfig(JSON.toJSONString(appContext.getApp()));

        logAppConversationCreateReqVO.setFromScene(appContext.getScene().name());
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

//        Map<String, Object> variablesMaps = appContext.getCurrentAppStepWrapper(stepId).getContextVariablesMaps();
        Map<String, Object> variablesMaps = new HashMap<>();

        messageCreateReqVO.setVariables(JSON.toJSONString(variablesMaps));
        messageCreateReqVO.setEndUser(appContext.getEndUser());
        messageCreateReqVO.setFromScene(appContext.getScene().name());
        messageCreateReqVO.setCurrency("USD");

        if (nodeTracking.getTaskException() == null) {

            ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);

            messageCreateReqVO.setAppConfig(JSON.toJSONString(actionResponse.getStepConfig()));

            messageCreateReqVO.setMessage(actionResponse.getMessage());

            messageCreateReqVO.setMessageTokens(actionResponse.getMessageTokens().intValue());
            messageCreateReqVO.setMessageUnitPrice(actionResponse.getMessageUnitPrice());

            messageCreateReqVO.setAnswer(actionResponse.getAnswer());
            messageCreateReqVO.setAnswerTokens(actionResponse.getAnswerTokens().intValue());
            messageCreateReqVO.setAnswerUnitPrice(actionResponse.getAnswerUnitPrice());

            messageCreateReqVO.setTotalPrice(actionResponse.getTotalPrice());
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
