package com.starcloud.ops.business.app.domain.manager;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 应用报警管理
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class AppAlarmManager {

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    /**
     * 向钉钉群发送报警信息
     * <p>
     * 模板：
     * #### 【告警-{environment}】
     * > - 应用UID：{appUid}
     * > - 应用名称：{appName}
     * > - 应用模式：{appModel}
     * > - 执行场景：{appScene}
     * > - 异常原因：{message}
     * > - 异常时间：{notifyTime}
     *
     * @param message 报警信息
     */
    public void executeAlarm(String appUid, String appName, String appModel, String appScene, String message) {
        log.info("发送应用执行报警信息。appUid: {}，appName: {}，appModel: {}，appScene: {}，message: {}", appUid, appName, appModel, appScene, message);
        try {
            // 应用模式和场景
            AppModelEnum appModelEnum = AppModelEnum.getByName(appModel);
            String appModeLabel = Objects.nonNull(appModelEnum) ? appModelEnum.getLabel() : StrUtil.EMPTY;
            AppSceneEnum appSceneEnum = AppSceneEnum.getByName(appScene);
            String appSceneLabel = Objects.nonNull(appSceneEnum) ? appSceneEnum.getLabel() : StrUtil.EMPTY;

            // 构建发送模板信息
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("environment", this.getEnvironment());
            templateParams.put("appUid", appUid);
            templateParams.put("appName", appName);
            templateParams.put("appModel", appModeLabel);
            templateParams.put("appScene", appSceneLabel);
            templateParams.put("message", message);
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));

            // 构建发送的请求
            SmsSendSingleToUserReqDTO request = new SmsSendSingleToUserReqDTO();
            request.setUserId(1L);
            request.setMobile("17835411844");
            request.setTemplateCode("APP_EXECUTE_ALARM_TEMPLATE");
            request.setTemplateParams(templateParams);
            // 发送告警信息
            smsSendApi.sendSingleSmsToAdmin(request);

            log.info("发送钉钉报警信息成功 appUid: {} appName: {}......", appUid, appName);
        } catch (Exception exception) {
            log.error("发送钉钉报警信息异常(应用执行)", exception);
            // ignore
            // 发送失败，不抛出异常，避免影响业务
        }


    }

    /**
     * 获取环境名称
     *
     * @return 环境名称
     */
    private String getEnvironment() {
        return dingTalkNoticeProperties.getName().equalsIgnoreCase("test") ? "测试环境" : "正式环境";
    }

}
