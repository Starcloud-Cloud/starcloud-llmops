package com.starcloud.ops.business.app.domain.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    private static final String TEMPLATE_CODE = "APP_EXECUTE_ALARM_TEMPLATE";

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    /**
     * 向钉钉群发送报警信息
     * <p>
     */
    public void executeAlarm(AppContextReqVO request, Throwable throwable) {
        log.info("应用执行失败：开始发送应用执行报警信息");
        try {
            // 构建模板信息
            Map<String, Object> templateParams = this.buildTemplateParams(request, throwable);
            // 构建发送的请求
            SmsSendSingleToUserReqDTO messageRequest = new SmsSendSingleToUserReqDTO();
            messageRequest.setUserId(1L);
            messageRequest.setMobile("17835411844");
            messageRequest.setTemplateCode(TEMPLATE_CODE);
            messageRequest.setTemplateParams(templateParams);
            // 发送告警信息
            smsSendApi.sendSingleSmsToAdmin(messageRequest);
            log.info("应用执行失败: 开始发送应用执行报警信息成功");
        } catch (Exception exception) {
            log.error("应用执行失败: 发送应用执行报警信息失败", exception);
            // ignore
            // 发送失败，不抛出异常，避免影响业务
        }
    }

    /**
     * 模板：
     * ####【应用执行告警-{environment}】
     * > - 应用UID: {appUid}
     * > - 执行用户: {nickname}(userId)
     * > - 用户等级: {userLevel}
     * > - 执行场景：{appScene}
     * > - 应用名称：{appName}
     * > - 应用模式：{appModel}
     * > - 异常时间：{notifyTime}
     * > - 异常原因：{message}
     * > - 扩展信息: {extended}
     *
     * @param request   请求
     * @param throwable 异常
     * @return 模板参数
     */
    private Map<String, Object> buildTemplateParams(AppContextReqVO request, Throwable throwable) {

        // 应用模式
        AppModelEnum appModelEnum = AppModelEnum.getByName(request.getMode());
        AppValidate.notNull(appModelEnum, "应用模式不存在");
        String appModeLabel = Objects.nonNull(appModelEnum) ? appModelEnum.getLabel() : StrUtil.EMPTY;

        // 执行场景
        AppSceneEnum appSceneEnum = AppSceneEnum.getByName(request.getScene());
        AppValidate.notNull(appSceneEnum, "应用场景不存在");
        String appSceneLabel = Objects.nonNull(appSceneEnum) ? appSceneEnum.getLabel() : StrUtil.EMPTY;

        // 系统环境
        String environment = this.getEnvironment();
        // 应用 UID
        String appUid = request.getAppUid();
        // 应用名称
        String appName = request.getAppName();
        // 用户ID
        String userId = this.getUserId(request);
        // 用户昵称
        String nickname = this.getNickname(request);
        // 用户等级
        String userLevel = this.getUserLevel(request);
        // 通知时间
        String notifyTime = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
        // 错误信息
        String message = this.getExceptionMessage(throwable);
        // 扩展信息
        String extended = this.getExtended(request);

        // 构建发送模板信息
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("environment", environment);
        templateParams.put("appUid", appUid);
        templateParams.put("nickname", nickname);
        templateParams.put("userId", userId);
        templateParams.put("userLevel", userLevel);
        templateParams.put("appScene", appSceneLabel);
        templateParams.put("appName", appName);
        templateParams.put("appModel", appModeLabel);
        templateParams.put("notifyTime", notifyTime);
        templateParams.put("message", message);
        templateParams.put("extended", extended);

        return templateParams;
    }

    /**
     * 获取环境名称
     *
     * @return 环境名称
     */
    private String getEnvironment() {
        return "test".equalsIgnoreCase(dingTalkNoticeProperties.getName()) ? "测试环境" : "正式环境";
    }

    /**
     * 获取用户ID
     * 如果游客ID不为空，则返回游客ID，否则返回用户ID
     *
     * @param request 请求
     * @return 用户ID
     */
    private String getUserId(AppContextReqVO request) {
        if (Objects.nonNull(request.getEndUserId())) {
            return request.getEndUser();
        }
        return String.valueOf(request.getUserId());
    }

    /**
     * 获取用户昵称
     * 如果游客ID不为空，则返回游客昵称，否则返回用户昵称
     *
     * @param request 请求
     * @return 用户昵称
     */
    private String getNickname(AppContextReqVO request) {
        if (Objects.nonNull(request.getEndUserId())) {
            return "游客";
        }
        return UserUtils.getUsername(request.getUserId());
    }

    /**
     * 获取用户等级
     *
     * @param request 请求
     * @return 用户等级
     */
    private String getUserLevel(AppContextReqVO request) {
        if (Objects.nonNull(request.getEndUserId())) {
            return "";
        }
        Map<Long, List<String>> longListMap = UserUtils.mapUserRoleName(Collections.singletonList(request.getUserId()));
        List<String> roleNames = longListMap.get(request.getUserId());
        if (CollectionUtil.isEmpty(roleNames)) {
            return "";
        }
        return String.join(",", roleNames);
    }

    /**
     * 获取异常信息
     *
     * @param throwable 异常
     * @return 异常信息
     */
    private String getExceptionMessage(Throwable throwable) {
        return ExceptionUtil.stackTraceToString(throwable, 500);
    }

    /**
     * 获取扩展信息
     *
     * @param request 请求
     * @return 扩展信息
     */
    private String getExtended(AppContextReqVO request) {
        Map<String, Object> extended = request.getExtended();
        if (CollectionUtil.isEmpty(extended)) {
            return "";
        }
        extended.remove("isSendAlarm");
        return JsonUtils.toJsonPrettyString(extended);
    }

}
