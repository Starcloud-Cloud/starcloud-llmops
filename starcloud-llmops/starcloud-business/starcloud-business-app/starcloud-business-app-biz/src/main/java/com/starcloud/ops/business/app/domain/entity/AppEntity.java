package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
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
import com.starcloud.ops.business.app.constant.WorkflowConstants;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.*;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public class AppEntity<Q, R> extends BaseAppEntity<AppExecuteReqVO, Object> {


    private AppWorkflowService appWorkflowService = SpringUtil.getBean(AppWorkflowService.class);


    private ThreadWithContext threadExecutor = SpringUtil.getBean(ThreadWithContext.class);


    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);


    private StoryEngine storyEngine = SpringUtil.getBean(StoryEngine.class);


    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    @Override
    protected void _validate() {

    }


    @Override
    protected LogAppConversationCreateReqVO _execute(AppExecuteReqVO req) {

        this.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), SecurityFrameworkUtils.getLoginUserId());

        log.info("fireByApp app: {}", JSON.toJSON(req.getAppReqVO()));

        // 创建 App 执行上下文
        AppContext appContext = new AppContext(this, AppSceneEnum.valueOf(req.getScene()));

        if (StringUtils.isNotBlank(req.getStepId())) {
            appContext.setStepId(req.getStepId());
        }
        //appContext.setHttpServletResponse(httpServletResponse);

        if (StringUtils.isNotBlank(req.getConversationUid())) {
            appContext.setConversationId(req.getConversationUid());
        }

        this.fireWorkflowContext(appContext);

        return null;
    }


    @Override
    protected void _aexecute(AppExecuteReqVO req) {

        this._execute(req);

        if (req.getSseEmitter() != null) {
            req.getSseEmitter().complete();
        }

    }

    @Override
    protected void _createAppConversationLog(AppExecuteReqVO req, LogAppConversationCreateReqVO logAppConversationCreateReqVO) {


        logAppConversationCreateReqVO.setAppMode(req.getAppReqVO().getModel());
        logAppConversationCreateReqVO.setAppName(req.getAppReqVO().getName());

        logAppConversationCreateReqVO.setStatus(LogStatusEnum.ERROR.name());

        logAppConversationCreateReqVO.setAppUid(req.getAppUid());
        logAppConversationCreateReqVO.setAppConfig(JSON.toJSONString(req.getAppReqVO()));

        logAppConversationCreateReqVO.setFromScene(req.getScene());
        logAppConversationCreateReqVO.setEndUser(req.getEndUser());

    }


    @Override
    protected void _initHistory(AppExecuteReqVO req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS) {

    }


    @Override
    protected void _insert() {
        getAppRepository().insert(this);
    }

    @Override
    protected void _update() {
        getAppRepository().update(this);
    }

    @Override
    protected <C> C _parseConversationConfig(String conversationConfig) {
        return null;
    }


    /**
     * 执行应用
     *
     * @param appContext 执行应用上下文
     */
    protected void fireWorkflowContext(@Valid AppContext appContext) {

        StoryRequest<Void> req = ReqBuilder.returnType(Void.class)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .startId(appContext.getConversationId())
                .request(appContext).build();

        req.setRecallStoryHook(recallStory -> {
            MonitorTracking monitorTracking = recallStory.getMonitorTracking();
            List<NodeTracking> storyTracking = monitorTracking.getStoryTracking();

            log.info("recallStory: {} {} {} {}", recallStory.getBusinessId(), recallStory.getStartId(), recallStory.getResult().isPresent(), recallStory.getReq());

            storyTracking.stream().filter((nodeTracking) -> BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())).forEach(nodeTracking -> {
                this.createAppMessageLog(appContext, nodeTracking);
            });

        });

        TaskResponse<Void> fire = storyEngine.fire(req);

//        // 使用量加一
//        if (AppSceneEnum.WEB_MARKET.equals(appContext.getScene())) {
//            AppOperateReqVO appOperateReqVO = new AppOperateReqVO();
//            appOperateReqVO.setAppUid(appContext.getApp().getUid());
//            appOperateReqVO.setVersion(AppConstants.DEFAULT_VERSION);
//            appOperateReqVO.setOperate(AppOperateTypeEnum.USAGE.name());
//            appMarketService.operate(appOperateReqVO);
//        }

        this.updateAppConversationLog(appContext.getConversationId(), fire.isSuccess());

        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());

    }


    /**
     * 创建应用消息日志
     *
     * @param appContext   应用上下文
     * @param nodeTracking 节点跟踪
     */
    private void createAppMessageLog(AppContext appContext, NodeTracking nodeTracking) {

        String stepId = nodeTracking.getNodeName();

        this.createAppMessage((messageCreateReqVO) -> {

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

        });

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

}
