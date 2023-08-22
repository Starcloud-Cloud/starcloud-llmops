package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * App 实体类, 提供基础的应用功能，封装一些基本的模版方法。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public abstract class BaseAppEntity<Q extends AppContextReqVO, R> {

    /**
     * 用户权益服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

    /**
     * 会话记录服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static LogAppConversationService logAppConversationService = SpringUtil.getBean(LogAppConversationService.class);

    /**
     * 消息记录服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static LogAppMessageService logAppMessageService = SpringUtil.getBean(LogAppMessageService.class);

    /**
     * 线程池
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ThreadWithContext threadExecutor = SpringUtil.getBean(ThreadWithContext.class);

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    private String uid;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    private String model;

    /**
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    private String source;

    /**
     * 应用标签，多个以逗号分割
     */
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    private List<String> images;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    private WorkflowConfigEntity workflowConfig;

    /**
     * 应用聊天配置
     */
    private ChatConfigEntity chatConfig;

    /**
     * 应用图片配置
     */
    private ImageConfigEntity imageConfig;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用发布成功后，应用市场 uid-version
     */
    private String publishUid;

    /**
     * 应用安装成功后，应用市场 uid-version
     */
    private String installUid;

    /**
     * 最后一次发布到应用市场时间
     */
    private LocalDateTime lastPublish;

    /**
     * 应用创建者
     */
    private String creator;

    /**
     * 应用更新者
     */
    private String updater;

    /**
     * 应用创建时间
     */
    private LocalDateTime createTime;

    /**
     * 应用更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 应用状态
     */
    private Long tenantId;

    /**
     * 模版方法：基础校验
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void doValidate(Q request);

    /**
     * 模版方法：执行应用
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract R doExecute(Q request);

    /**
     * 模版方法：异步执行应用
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void doAsyncExecute(Q request);

    /**
     * 模版方法：执行应用前置处理方法
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void beforeExecute(Q request);

    /**
     * 模版方法：执行应用后置处理方法
     *
     * @param request   请求参数
     * @param throwable 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void afterExecute(Q request, Throwable throwable);

    /**
     * 模版方法：历史记录初始化
     *
     * @param request            请求参数
     * @param logAppConversation 会话记录
     * @param logAppMessageList  消息记录
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void initHistory(Q request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList);

    /**
     * 模版方法：构建会话记录信息
     *
     * @param request       请求参数
     * @param createRequest 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void buildAppConversationLog(Q request, LogAppConversationCreateReqVO createRequest);

    /**
     * 模版方法：新增应用
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void doInsert();

    /**
     * 模版方法：更新应用
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract void doUpdate();

    /**
     * 获取当前执行记录的主体用户，会做主体用户做如下操作。默认是当前用户态
     * 1，扣除权益
     * 2，记录日志
     *
     * @return 用户 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected Long getRunUserId(Q req) {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    /**
     * 同步执行应用
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public R execute(Q request) {
        try {
            log.info("应用执行开始: 应用 UID：{}, 应用名称：{}", this.getUid(), this.getName());

            // 扣除权益用户，记录日志用户
            if (request.getUserId() == null) {
                request.setUserId(this.getRunUserId(request));
            }
            log.info("应用执行：权益扣除用户, 日志记录用户 ID：{}, ", request.getUserId());
            // 基础校验
            this.validate(request);
            // 会话记录
            this.initConversationLog(request);

            // 执行应用
            this.beforeExecute(request);
            R result = this.doExecute(request);
            this.afterExecute(request, null);

            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), true);
            log.info("应用执行结束: {} {}", this.getUid(), result);
            return result;

        } catch (ServiceException exception) {
            log.error("应用执行异常(ServiceException): {}", exception.getMessage());
            this.afterExecute(request, exception);
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);
            throw exception;

        } catch (Exception exception) {
            log.error("应用执行异常(Exception): {}", exception.getMessage());
            this.afterExecute(request, ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage()));
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage());
        }
    }

    /**
     * 异步执行
     * log 交由具体类去实现
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void asyncExecute(Q request) {
        try {
            log.info("应用异步执行开始: 应用 UID：{}, 应用名称：{}", this.getUid(), this.getName());
            // 扣除权益用户，记录日志用户
            if (request.getUserId() == null) {
                request.setUserId(this.getRunUserId(request));
            }
            log.info("应用异步执行：权益扣除用户, 日志记录用户 ID：{}, ", request.getUserId());
            // 基础校验
            this.validate(request);
            //会话处理
            this.initConversationLog(request);
            // 异步执行应用
            threadExecutor.asyncExecute(() -> {
                try {
                    this.beforeExecute(request);
                    this.doAsyncExecute(request);
                    this.afterExecute(request, null);
                    log.info("应用异步执行结束: 应用UID： {}", this.getUid());
                    // 更新会话记录
                    this.updateAppConversationLog(request.getConversationUid(), true);

                } catch (ServiceException exception) {
                    log.error("应用异步任务执行异常(ServiceException): {}", exception.getMessage(), exception);
                    this.afterExecute(request, exception);
                    // 更新会话记录
                    this.updateAppConversationLog(request.getConversationUid(), false);

                } catch (Exception exception) {
                    log.error("应用异任务步任务执行异常: {}", exception.getMessage(), exception);
                    this.afterExecute(request, ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage()));
                    // 更新会话记录
                    this.updateAppConversationLog(request.getConversationUid(), false);
                }
            });

        } catch (ServiceException exception) {
            log.error("应用异步执行异常(ServiceException): {}", exception.getMessage(), exception);
            this.afterExecute(request, exception);
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);

        } catch (Exception exception) {
            log.error("应用异步执行异常(Exception): {}", exception.getMessage(), exception);
            this.afterExecute(request, ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage()));
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);

        }
    }

    /**
     * 基础校验
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate(Q request) {
        log.info("应用执行：基础校验开始 ...");
        this.doValidate(request);
        log.info("应用执行：基础校验结束 ...");
    }

    /**
     * 新增应用
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void insert() {
        // 设置 uid
        if (StrUtil.isBlank(this.getUid())) {
            this.setUid(IdUtil.fastSimpleUUID());
        }
        this.validate(null);
        this.doInsert();
    }

    /**
     * 更新应用
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void update() {
        this.validate(null);
        this.doUpdate();
    }

    /**
     * 权益检测
     *
     * @param benefitsType 权益类型
     * @param userId       用户 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void allowExpendBenefits(String benefitsType, Long userId) {
        userBenefitsService.allowExpendBenefits(benefitsType, userId);
    }

    /**
     * 初始化会话记录
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void initConversationLog(Q request) {
        log.info("应用执行：日志会话记录初始化开始 ...");
        if (StrUtil.isNotBlank(request.getConversationUid())) {
            LogAppConversationDO logAppConversationDO = this.getAppConversation(request.getConversationUid());
            if (logAppConversationDO == null) {
                log.info("应用执行：会话 UID 不为空，但会话不存在，自动创建新的日志会话记录开始...");
                String conversationUid = this.createAppConversationLog(request);
                request.setConversationUid(conversationUid);
                log.info("应用执行：会话 UID 不为空，但会话不存在，自动创建新的日志会话记录结束...");
            } else {
                log.info("应用执行：会话 UID 不为空，会话存在，初始化会话历史记录开始...");
                List<LogAppMessageDO> logAppMessageList = this.getAppConversationMessages(request.getConversationUid());
                this.initHistory(request, logAppConversationDO, logAppMessageList);
                log.info("应用执行：会话 UID 不为空，会话存在，初始化会话历史记录结束...");
            }
        } else {
            log.info("应用执行：会话 UID 为空，自动创建新的日志会话记录开始...");
            String conversationUid = this.createAppConversationLog(request);
            request.setConversationUid(conversationUid);
            log.info("应用执行：会话 UID 为空，自动创建新的日志会话记录结束...");
        }
        log.info("应用执行：日志会话记录初始化结束 会话 UID {} ...", request.getConversationUid());
    }

    /**
     * 创建一条新的会话
     *
     * @param request 请求参数
     * @return 会话 UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String createAppConversationLog(Q request) {
        LogAppConversationCreateReqVO reqVO = new LogAppConversationCreateReqVO();
        String conversationUid = request.getConversationUid();
        if (StringUtils.isBlank(conversationUid)) {
            conversationUid = IdUtil.fastSimpleUUID();
        }
        reqVO.setUid(conversationUid);
        reqVO.setAppUid(this.getUid());
        reqVO.setAppName(this.getName());
        reqVO.setAppMode(this.getModel());
        reqVO.setStatus(LogStatusEnum.ERROR.name());
        reqVO.setEndUser(request.getEndUser());
        reqVO.setCreator(String.valueOf(request.getUserId()));
        reqVO.setUpdater(String.valueOf(request.getUserId()));
        reqVO.setTenantId(this.getTenantId());
        reqVO.setFromScene(request.getScene());
        this.buildAppConversationLog(request, reqVO);
        logAppConversationService.createAppConversation(reqVO);
        return reqVO.getUid();
    }

    /**
     * 判断执行情况，最最后的 会话状态更新
     *
     * @param conversationUid 日志会话 UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void updateAppConversationLog(String conversationUid, Boolean status) {
        log.info("应用执行：更新会话记录开始 会话 UID：{}, 会话状态：{}...", conversationUid, status);
        logAppConversationService.updateAppConversationStatus(conversationUid, status ? "SUCCESS" : "ERROR");
        log.info("应用执行：更新会话记录结束, 更新会话成功");
    }

    /**
     * 根据日志会话 UID 获取日志会话
     *
     * @param conversationUid 日志会话 UID
     * @return 会话对象
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private LogAppConversationDO getAppConversation(String conversationUid) {
        return logAppConversationService.getAppConversation(conversationUid);
    }

    /**
     * 获取会话消息
     *
     * @param conversationUid 日志会话 UID
     * @return 会话消息列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<LogAppMessageDO> getAppConversationMessages(String conversationUid) {
        if (StrUtil.isNotBlank(conversationUid)) {
            LogAppMessagePageReqVO reqVO = new LogAppMessagePageReqVO();
            reqVO.setPageSize(100);
            reqVO.setPageNo(1);
            reqVO.setAppConversationUid(conversationUid);
            PageResult<LogAppMessageDO> pageResult = logAppMessageService.getAppMessagePage(reqVO);
            return Optional.ofNullable(pageResult).map(PageResult::getList).orElse(new ArrayList<>());
        }
        return new ArrayList<>();

    }

    /**
     * 创建日志消息
     *
     * @param consumer 消息创建
     * @return 消息对象
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public LogAppMessageCreateReqVO createAppMessage(Consumer<LogAppMessageCreateReqVO> consumer) {
        log.info("应用执行：创建日志消息开始 ...");
        LogAppMessageCreateReqVO messageCreateRequest = new LogAppMessageCreateReqVO();
        messageCreateRequest.setUid(IdUtil.fastSimpleUUID());
        messageCreateRequest.setAppUid(this.getUid());
        messageCreateRequest.setAppMode(this.getModel());
        messageCreateRequest.setTenantId(this.getTenantId());
        consumer.accept(messageCreateRequest);
        log.info("应用执行：创建日志消息请求：{}", JSONUtil.toJsonStr(messageCreateRequest));
        logAppMessageService.createAppMessage(messageCreateRequest);
        log.info("应用执行：创建日志消息结束 ...");
        return messageCreateRequest;
    }

    /**
     * 执行成功后，响应更新
     *
     * @param stepId   步骤ID
     * @param response 响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setActionResponse(String stepId, ActionResponse response) {
        workflowConfig.setActionResponse(stepId, response);
    }
}
