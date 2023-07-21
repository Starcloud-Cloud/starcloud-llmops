package com.starcloud.ops.business.app.service;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.exception.KstryException;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.NoticeTracking;
import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.ExecuteAppRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.constant.WorkflowConstants;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * App 工作流服务, 执行应用
 *
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

    @Autowired
    private UserBenefitsService userBenefitsService;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private ThreadWithContext threadExecutor;


    /**
     * 根据保存的配置直接执行，默认执行第一个步骤
     *
     * @param appId 应用 UID
     */
    public void fireByAppUid(String appId, AppSceneEnum scene) {

        AppEntity app = AppFactory.factory(appId);

        log.info("fireByAppUid app: {}", JSON.toJSON(app));

        AppContext appContext = new AppContext(app, scene);

        this.fireByAppContext(appContext);
    }

    /**
     * 根据传入的配置 执行
     *
     * @param appId 应用 UID
     * @param scene 场景
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest) {
        fireByApp(appId, scene, appRequest, StringUtils.EMPTY);
    }

    /**
     * 根据传入的配置 执行
     *
     * @param appId      应用 UID
     * @param scene      场景
     * @param appRequest 请求参数
     * @param stepId     步骤 ID
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId) {
        fireByApp(appId, scene, appRequest, stepId, StringUtils.EMPTY);
    }

    /**
     * 根据传入的配置 执行
     *
     * @param appId      应用 UID
     * @param scene      场景
     * @param appRequest 请求参数
     * @param stepId     步骤 ID
     * @param requestId  请求 ID
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId, String requestId) {
        // 获取 AppEntity
        AppEntity app = AppFactory.factory(appId, appRequest);
        log.info("fireByApp app: {}", app);

        // 创建 App 执行上下文
        AppContext appContext = new AppContext(app, scene);
        if (StringUtils.isNotBlank(stepId)) {
            appContext.setStepId(stepId);
        }
        if (StringUtils.isNotBlank(requestId)) {
            appContext.setConversationId(requestId);
        }

        // 执行该应用
        this.fireByAppContext(appContext);
    }

    /**
     * 根据传入的配置 执行
     *
     * @param appId               应用 UID
     * @param scene               场景
     * @param appRequest          请求参数
     * @param stepId              步骤 ID
     * @param httpServletResponse Http 响应
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId, HttpServletResponse httpServletResponse) {
        // 获取 AppEntity
        AppEntity app = AppFactory.factory(appId, appRequest);
        log.info("fireByApp app: {}", app);

        // 创建 App 执行上下文
        AppContext appContext = new AppContext(app, scene);
        if (StringUtils.isNotBlank(stepId)) {
            appContext.setStepId(stepId);
        }

        // 执行该应用
        this.fireByAppContext(appContext);

        new ExecuteAppRespVO();
    }

    /**
     * 根据传入的配置 执行
     *
     * @param appId               应用 UID
     * @param scene               场景
     * @param appRequest          请求参数
     * @param stepId              步骤 ID
     * @param httpServletResponse Http 响应
     */
    public void fireByApp(String appId, AppSceneEnum scene, AppReqVO appRequest, String stepId, String requestId, SseEmitter sseEmitter) {
        // 获取 AppEntity
        AppEntity app = null;
        if (appRequest == null) {
            if (AppSceneEnum.WEB_MARKET.equals(scene)) {
                app = AppFactory.factoryMarket(appId);
            } else {
                app = AppFactory.factory(appId);
            }
        } else {
            app = AppFactory.factory(appId, appRequest);
        }

        userBenefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), SecurityFrameworkUtils.getLoginUserId());

        log.info("fireByApp app: {}", JSON.toJSON(app));

        // 创建 App 执行上下文
        AppContext appContext = new AppContext(app, scene);

        if (StringUtils.isNotBlank(stepId)) {
            appContext.setStepId(stepId);
        }
        //appContext.setHttpServletResponse(httpServletResponse);
        appContext.setSseEmitter(sseEmitter);
        if (StringUtils.isNotBlank(requestId)) {
            appContext.setConversationId(requestId);
        }

        // 执行该应用
        threadExecutor.asyncExecute(() -> {
            this.fireByAppContext(appContext);
        });
    }

    /**
     * 执行应用
     *
     * @param appContext 执行应用上下文
     */
    private void fireByAppContext(@Valid AppContext appContext) {

        StoryRequest<Void> req = ReqBuilder.returnType(Void.class)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .startId(appContext.getConversationId())
                .request(appContext).build();

        LogAppConversationCreateReqVO conversation = this.createAppConversationLog(appContext);

        req.setRecallStoryHook(recallStory -> {
            MonitorTracking monitorTracking = recallStory.getMonitorTracking();
            List<NodeTracking> storyTracking = monitorTracking.getStoryTracking();

            log.info("recallStory: {} {} {} {}", recallStory.getBusinessId(), recallStory.getStartId(), recallStory.getResult().isPresent(), recallStory.getReq());

            storyTracking.stream().filter((nodeTracking) -> BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())).forEach(nodeTracking -> {
                this.createAppMessageLog(appContext, nodeTracking);
            });

//            List<String> collect = storyTracking.stream()
//                    .map(nt -> GlobalUtil.format("{}({} {} {} {} {} {} {})", nt.getNodeName(), nt.getThreadId(), nt.getNodeId(), nt.getIndex(), nt.getSpendTime(), nt.getNodeName(), nt.getIterateStride(), nt.getNoticeTracking().get(0).getValue())).collect(Collectors.toList());
//            System.out.println("name list: " + String.join(",", collect));
        });

        TaskResponse<Void> fire = storyEngine.fire(req);

        // 使用量加一
        if (AppSceneEnum.WEB_MARKET.equals(appContext.getScene())) {
            AppOperateReqVO appOperateReqVO = new AppOperateReqVO();
            appOperateReqVO.setAppUid(appContext.getApp().getUid());
            appOperateReqVO.setVersion(AppConstants.DEFAULT_VERSION);
            appOperateReqVO.setOperate(AppOperateTypeEnum.USAGE.name());
            appMarketService.operate(appOperateReqVO);
        }

        this.updateAppConversationLog(conversation.getUid(), fire.isSuccess());
        appContext.getSseEmitter().complete();

        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());

    }

    /**
     * 获取追踪
     *
     * @param noticeTrackings 追踪列表
     * @param cls             类
     * @param <T>             类型
     * @return 追踪
     */
    private <T> T getTracking(List<NoticeTracking> noticeTrackings, Class<T> cls) {

        String clsName = cls.getSimpleName();

        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clsName);

        return Optional.ofNullable(noticeTrackings).orElse(new ArrayList<>()).stream().filter(noticeTracking -> noticeTracking.getFieldName().equals(field)).map(noticeTracking -> {
            return JSON.parseObject(noticeTracking.getValue(), cls);
        }).findFirst().orElse(null);
    }

    /**
     * 创建应用对话日志
     *
     * @param appContext 应用上下文
     * @return 应用对话日志
     */
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

        //logAppConversationCreateReqVO.setCreateTime(LocalDateTime.now());

        return logAppApi.createAppConversation(logAppConversationCreateReqVO);

    }

    /**
     * 更新应用消息日志
     *
     * @param uid    应用消息日志 UID
     * @param status 状态
     */
    private void updateAppConversationLog(String uid, Boolean status) {

        logAppApi.updateAppConversationStatus(uid, status ? LogStatusEnum.SUCCESS : LogStatusEnum.ERROR);

    }

    /**
     * 创建应用消息日志
     *
     * @param appContext   应用上下文
     * @param nodeTracking 节点跟踪
     */
    private void createAppMessageLog(AppContext appContext, NodeTracking nodeTracking) {


        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();

        String stepId = nodeTracking.getNodeName();

        messageCreateReqVO.setUid(IdUtil.fastSimpleUUID());
        messageCreateReqVO.setAppConversationUid(appContext.getConversationId());
        messageCreateReqVO.setAppUid(appContext.getApp().getUid());
        messageCreateReqVO.setAppMode(appContext.getApp().getModel());
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

        ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);

        if (actionResponse != null) {

            messageCreateReqVO.setStatus(actionResponse.getSuccess() ? LogStatusEnum.SUCCESS.name() : LogStatusEnum.ERROR.name());

            messageCreateReqVO.setAppConfig(JSON.toJSONString(actionResponse.getStepConfig()));

            messageCreateReqVO.setMessage(actionResponse.getMessage());

            messageCreateReqVO.setMessageTokens(actionResponse.getMessageTokens().intValue());
            messageCreateReqVO.setMessageUnitPrice(actionResponse.getMessageUnitPrice());

            messageCreateReqVO.setAnswer(actionResponse.getAnswer());
            messageCreateReqVO.setAnswerTokens(actionResponse.getAnswerTokens().intValue());
            messageCreateReqVO.setAnswerUnitPrice(actionResponse.getAnswerUnitPrice());

            messageCreateReqVO.setTotalPrice(actionResponse.getTotalPrice());

            messageCreateReqVO.setErrorCode(actionResponse.getErrorCode());
            messageCreateReqVO.setErrorMsg(actionResponse.getErrorMsg());
        }

        if (nodeTracking.getTaskException() != null) {

            messageCreateReqVO.setStatus(LogStatusEnum.ERROR.name());
            messageCreateReqVO.setErrorMsg(nodeTracking.getTaskException().getMessage());
            messageCreateReqVO.setErrorCode("010");

            if (nodeTracking.getTaskException() instanceof KstryException) {
                messageCreateReqVO.setErrorCode(((KstryException) nodeTracking.getTaskException()).getErrorCode());
            }
        }


        logAppApi.createAppMessage(messageCreateReqVO);
    }


}
