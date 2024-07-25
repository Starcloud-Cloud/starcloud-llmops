package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_BEAN_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_IMAGE_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_NOT_ENOUGH;

/**
 * 应用报警管理
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class CreativeAlarmManager {

    /**
     * 模板编码
     */
    private static final String TEMPLATE_CODE = "CREATIVE_EXECUTE_ALARM_TEMPLATE";

    /**
     * 不打印堆栈信息的错误码
     */
    private static final List<Integer> NO_STACK_TRACE_CODE_LIST = Arrays.asList(
            USER_RIGHTS_BEAN_NOT_ENOUGH.getCode(),
            USER_RIGHTS_IMAGE_NOT_ENOUGH.getCode(),
            USER_RIGHTS_NOT_ENOUGH.getCode(),
            USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH.getCode()
    );

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    @Resource
    private CreativeContentService creativeContentService;

    /**
     * 向钉钉群发送报警信息
     * <p>
     */
    public void executeAlarm(String contentUid, Boolean force, Integer maxRetry, Throwable throwable) {
        log.info("创作内容执行失败：开始发送创作内容执行报警信息");
        try {
            // 查询创作内容详情
            CreativeContentRespVO content = creativeContentService.get(contentUid);
            if (Objects.isNull(content)) {
                log.error("发送创作内容执行报警信息失败：创作内容不存在：{}", contentUid);
                return;
            }

            // 如果不是强制执行，则判断是否需要发送报警信息
            if (!force) {
                // 如果创作内容不是最终失败，或者重试次数小于最大重试次数，不发送报警信息
                if (!CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(content.getStatus()) ||
                        (content.getRetryCount() == null || content.getRetryCount() < maxRetry)) {
                    log.info("创作内容执行失败：创作内容不是最终失败，或者重试次数小于最大重试次数，不发送报警信息");
                    return;
                }
            }

            // 构建模板信息
            Map<String, Object> templateParams = this.buildTemplateParams(content, throwable);
            // 构建发送的请求
            SmsSendSingleToUserReqDTO messageRequest = new SmsSendSingleToUserReqDTO();
            messageRequest.setUserId(1L);
            messageRequest.setMobile("17835411844");
            messageRequest.setTemplateCode(TEMPLATE_CODE);
            messageRequest.setTemplateParams(templateParams);
            // 发送告警信息
            smsSendApi.sendSingleSmsToAdmin(messageRequest);
            log.info("创作内容执行失败: 开始发送创作内容执行报警信息成功");
        } catch (Exception exception) {
            log.error("创作内容执行失败: 发送创作内容执行报警信息失败", exception);
            // ignore
            // 发送失败，不抛出异常，避免影响业务
        }
    }

    /**
     * 模板：
     * ####【创作内容执行告警-{environment}】
     * > - 内容UID: {contentUid}
     * > - 批次UID: {batchUid}
     * > - 计划UID: {planUid}
     * > - 应用UID: {appUid}
     * > - 执行用户: {nickname}(userId)
     * > - 用户等级: {userLevel}
     * > - 执行场景：{appScene}
     * > - 应用名称：{appName}
     * > - 应用模式：{appModel}
     * > - 异常时间：{notifyTime}
     * > - 异常信息：{message}
     * > - 异常原因：{stackTrace}
     * > - 扩展信息: {extended}
     *
     * @param content   请求
     * @param throwable 异常
     * @return 模板参数
     */
    private Map<String, Object> buildTemplateParams(CreativeContentRespVO content, Throwable throwable) {

        // 获取应用信息
        AppMarketRespVO appInformation = Optional.ofNullable(content)
                .map(CreativeContentRespVO::getExecuteParam)
                .map(CreativeContentExecuteParam::getAppInformation)
                .orElseThrow(() -> new RuntimeException("创作内容执行应用参数为空!"));

        // 系统环境
        String environment = this.getEnvironment();
        // 内容UID
        String contentUid = content.getUid();
        // 批次UID
        String batchUid = content.getBatchUid();
        // 计划UID
        String planUid = content.getPlanUid();
        // 应用UID
        String appUid = appInformation.getUid();

        // 用户ID
        String userId = content.getCreator();
        // 用户昵称
        String nickname = this.getNickname(userId);
        // 用户等级
        String userLevel = this.getUserLevel(userId);

        // 执行场景固定位小红书模式
        String appSceneLabel = AppSceneEnum.XHS_WRITING.getLabel();
        // 应用名称
        String appName = appInformation.getName();
        // 应用模式固定位生成模式
        String appModeLabel = AppModelEnum.COMPLETION.getLabel();
        // 通知时间
        String notifyTime = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
        // 错误信息
        String message = this.getErrorMessage(throwable);
        // 堆栈信息
        String stackTrace = this.getStackTrace(throwable);
        // 扩展信息
        String extended = this.getExtended(content);

        // 构建发送模板信息
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("environment", environment);
        templateParams.put("contentUid", contentUid);
        templateParams.put("batchUid", batchUid);
        templateParams.put("planUid", planUid);
        templateParams.put("appUid", appUid);
        templateParams.put("nickname", nickname);
        templateParams.put("userId", userId);
        templateParams.put("userLevel", userLevel);
        templateParams.put("appScene", appSceneLabel);
        templateParams.put("appName", appName);
        templateParams.put("appModel", appModeLabel);
        templateParams.put("notifyTime", notifyTime);
        templateParams.put("message", message);
        templateParams.put("stackTrace", stackTrace);
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
     * @param userId 用户ID
     * @return 用户昵称
     */
    private String getNickname(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("用户ID为空");
        }
        return UserUtils.getUsername(userId);
    }

    /**
     * 获取用户等级
     *
     * @param userId 请求
     * @return 用户等级
     */
    private String getUserLevel(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("用户ID为空");
        }
        Map<Long, List<String>> longListMap = UserUtils.mapUserRoleName(Collections.singletonList(Long.valueOf(userId)));
        List<String> roleNames = longListMap.get(Long.valueOf(userId));
        if (CollectionUtil.isEmpty(roleNames)) {
            return "";
        }
        return String.join(",", roleNames);
    }

    /**
     * 获取错误信息
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String getErrorMessage(Throwable throwable) {
        if (throwable != null) {
            return throwable.getMessage();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取堆栈信息
     *
     * @param throwable 异常
     * @return 堆栈信息
     */
    private String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return StringUtils.EMPTY;
        }

        if (throwable instanceof ServiceException || throwable.getCause() instanceof ServiceException) {
            ServiceException serviceException;
            if (throwable instanceof ServiceException) {
                serviceException = (ServiceException) throwable;
            } else {
                serviceException = (ServiceException) throwable.getCause();
            }

            // 魔法豆/图片不足，不打印堆栈信息
            if (NO_STACK_TRACE_CODE_LIST.contains(serviceException.getCode())) {
                return StrUtil.EMPTY;
            }
        }

        return this.getStackTraceMessage(throwable);
    }

    /**
     * 获取异常信息
     *
     * @param throwable 异常
     * @return 异常信息
     */
    private String getStackTraceMessage(Throwable throwable) {
        return ExceptionUtil.stackTraceToString(throwable, 1000);
    }

    /**
     * 获取扩展信息
     *
     * @param content 创作内容
     * @return 扩展信息
     */
    private String getExtended(CreativeContentRespVO content) {
        return "";
    }

}
