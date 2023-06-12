package com.starcloud.ops.workflow.service;

import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.util.GlobalUtil;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.workflow.constant.WorkflowConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Slf4j
@Component
public class AppWorkflowService {

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


        req.setRecallStoryHook(recallStory -> {
            MonitorTracking monitorTracking = recallStory.getMonitorTracking();
            List<NodeTracking> storyTracking = monitorTracking.getStoryTracking();

            log.info("recallStory: {} {} {} {}", recallStory.getBusinessId(), recallStory.getStartId(), recallStory.getResult(), recallStory.getReq());

            List<String> collect = storyTracking.stream()
                    .map(nt -> GlobalUtil.format("{}({} {} {} {} {} {} {})", nt.getNodeName(), nt.getThreadId(), nt.getNodeId(), nt.getIndex(), nt.getSpendTime(), nt.getNodeName(), nt.getIterateStride(), nt.getNoticeTracking().get(0).getValue())).collect(Collectors.toList());
            System.out.println("name list: " + String.join(",", collect));
        });

        TaskResponse<Void> fire = storyEngine.fire(req);

        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());


    }


}
