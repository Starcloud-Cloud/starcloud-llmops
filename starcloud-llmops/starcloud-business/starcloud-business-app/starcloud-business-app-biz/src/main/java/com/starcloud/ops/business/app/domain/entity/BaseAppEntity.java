package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.MATERIAL_LIST;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_BEAN_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_IMAGE_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_NOT_ENOUGH;

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
    private static AdminUserRightsApi adminUserRightsApi = SpringUtil.getBean(AdminUserRightsApi.class);

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
     * 应用排序，越小越靠前
     */
    private Long sort;

    /**
     * 应用类别
     */
    private String category;

    /**
     * 应用标签，多个以逗号分割
     */
    private List<String> tags;

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
     * 应用示例
     */
    private String example;

    /**
     * 应用演示
     */
    private String demo;

    /**
     * 应用插件列表
     */
    private List<String> pluginList;

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
     * 素材列表
     */
    private List<Map<String, Object>> materialList;

    /**
     * 是否校验
     */
    private Boolean validate;

    /**
     * 验证列表
     */
    private List<Verification> verificationList = new ArrayList<>();

    /**
     * 模版方法：基础校验
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract List<Verification> doValidate(Q request, ValidateTypeEnum validateType);

    /**
     * 模版方法：执行应用
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract R
    doExecute(Q request);

    /**
     * 模版方法：异步执行应用
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract R doAsyncExecute(Q request);

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
    protected abstract void afterExecute(R result, Q request, Throwable throwable);

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
     * 模版方法：获取应用的 AI 模型类型
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract String getLlmModelType(Q request);

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
        log.info("应用执行开始: 应用UID：{}, 应用名称：{}", this.getUid(), this.getName());
        // 扣除权益用户，记录日志用户
        if (request.getUserId() == null) {
            Long userId = this.getRunUserId(request);
            request.setUserId(userId);
        }
        //强制设置，不设置应该获取的是当前上下文的
        if (request.getTenantId() != null) {
            TenantContextHolder.setTenantId(request.getTenantId());
        }
        // 初始化回话记录
        this.initAppConversationLog(request);

        try {
            log.info("应用执行：权益扣除用户, 日志记录用户 ID：{}, {}, {}, {}", request.getUserId(), TenantContextHolder.getTenantId(), TenantContextHolder.isIgnore(), SecurityFrameworkUtils.getLoginUser());
            // 基础校验
            this.validate(request, ValidateTypeEnum.EXECUTE);

            // 执行应用
            this.beforeExecute(request);
            R result = this.doExecute(request);
            this.afterExecute(result, request, null);

            // 更新会话记录
            this.successAppConversationLog(request.getConversationUid(), request);
            log.info("应用执行结束: 应用UID: {}", this.getUid());
            return result;

        } catch (ServiceException exception) {
            log.error("应用执行异常(ServiceException): 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage());
            this.afterExecute(null, request, exception);
            // 更新会话记录
            this.failureAppConversationLog(request.getConversationUid(), String.valueOf(exception.getCode()), ExceptionUtil.stackTraceToString(exception), request);
            throw exception;

        } catch (Exception exception) {
            log.error("应用执行异常(Exception): 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage());
            this.afterExecute(null, request, exception(ErrorCodeConstants.EXECUTE_BASE_FAILURE, exception.getMessage()));
            // 更新会话记录
            this.failureAppConversationLog(request.getConversationUid(), String.valueOf(ErrorCodeConstants.EXECUTE_BASE_FAILURE.getCode()), ExceptionUtil.stackTraceToString(exception), request);
            throw exceptionWithCause(ErrorCodeConstants.EXECUTE_BASE_FAILURE, exception.getMessage(), exception);
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
        log.info("应用异步执行开始: 应用UID：{}, 应用名称：{}", this.getUid(), this.getName());
        // 扣除权益用户，记录日志用户
        if (request.getUserId() == null) {
            request.setUserId(this.getRunUserId(request));
        }
        // 会话处理
        this.initAppConversationLog(request);

        try {
            log.info("应用异步执行：权益扣除用户, 日志记录用户 ID：{}, {}, {}, {}", request.getUserId(), TenantContextHolder.getTenantId(), TenantContextHolder.isIgnore(), SecurityFrameworkUtils.getLoginUser());
            // 基础校验
            this.validate(request, ValidateTypeEnum.EXECUTE);

            // 异步执行应用
            threadExecutor.asyncExecute(() -> {
                try {

                    log.info("应用异步执行-threadExecutor：权益扣除用户, 日志记录用户 ID：{}, {}, {}, {}", request.getUserId(), TenantContextHolder.getTenantId(), TenantContextHolder.isIgnore(), SecurityFrameworkUtils.getLoginUser());

                    this.beforeExecute(request);
                    R result = this.doAsyncExecute(request);
                    this.afterExecute(result, request, null);
                    log.info("应用异步执行结束: 应用UID： {}", this.getUid());
                    // 更新会话记录
                    this.successAppConversationLog(request.getConversationUid(), request);

                } catch (ServiceException exception) {
                    log.error("应用异步任务执行异常(ServiceException): 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage());
                    // 更新会话记录
                    this.failureAppConversationLog(request.getConversationUid(), String.valueOf(exception.getCode()), ExceptionUtil.stackTraceToString(exception), request);
                    this.afterExecute(null, request, exception);

                } catch (Exception exception) {
                    log.error("应用异任务步任务执行异常: 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage(), exception);
                    // 更新会话记录
                    this.failureAppConversationLog(request.getConversationUid(), String.valueOf(ErrorCodeConstants.EXECUTE_BASE_FAILURE.getCode()), exception.getMessage(), request);
                    this.afterExecute(null, request, exception(ErrorCodeConstants.EXECUTE_BASE_FAILURE, ExceptionUtil.stackTraceToString(exception)));
                }
            });

        } catch (ServiceException exception) {
            log.error("应用异步执行异常(ServiceException): 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage());
            // 更新会话记录
            this.failureAppConversationLog(request.getConversationUid(), String.valueOf(exception.getCode()), ExceptionUtil.stackTraceToString(exception), request);
            this.afterExecute(null, request, exception);
        } catch (Exception exception) {
            log.error("应用异步执行异常(Exception): 应用UID: {}, 错误消息: {}", this.getUid(), exception.getMessage());
            // 更新会话记录
            this.failureAppConversationLog(request.getConversationUid(), String.valueOf(ErrorCodeConstants.EXECUTE_BASE_FAILURE.getCode()), ExceptionUtil.stackTraceToString(exception), request);
            this.afterExecute(null, request, exception(ErrorCodeConstants.EXECUTE_BASE_FAILURE, exception.getMessage()));
        }
    }


    /**
     * 基础校验
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(Q request, ValidateTypeEnum validateType) {
        log.info("应用执行：基础校验开始 ...");
        List<Verification> verifications = this.doValidate(request, validateType);
        log.info("应用执行：基础校验结束 ...");
        return verifications;
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
        // 开启校验，才会进行校验
        if (Objects.isNull(this.validate) || this.validate) {
            List<Verification> validate = this.validate(null, ValidateTypeEnum.CREATE);
            if (CollectionUtil.isNotEmpty(validate)) {
                this.setVerificationList(validate);
                return;
            }
        }

        this.doInsert();
    }

    /**
     * 更新应用
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void update() {
        // 开启校验，才会进行校验
        if (Objects.isNull(this.validate) || this.validate) {
            List<Verification> validate = this.validate(null, ValidateTypeEnum.UPDATE);
            if (CollectionUtil.isNotEmpty(validate)) {
                this.setVerificationList(validate);
                return;
            }
        }
        disposeMaterial();
        this.doUpdate();
    }

    /**
     * 素材单独拆出一个字段
     */
    private void disposeMaterial() {
        if (Objects.nonNull(workflowConfig) && CollectionUtil.isEmpty(materialList)) {
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
            };
            WorkflowStepWrapper stepWrapper = workflowConfig.getStepWrapperWithoutError(MaterialActionHandler.class);

            if (Objects.isNull(stepWrapper)) {
                return;
            }

            materialList = Optional.ofNullable(stepWrapper)
                    .map(step -> step.getVariablesValue(MATERIAL_LIST))
                    .map(Object::toString)
                    .map(str -> JSONUtil.toBean(StringUtil.isBlank(str) ? "[]" : str, typeReference, true))
                    .orElse(Collections.emptyList());
            workflowConfig.putVariable(MaterialActionHandler.class, MATERIAL_LIST, "[]");
        }
    }

    /**
     * 权益检测
     *
     * @param rightsType 权益类型
     * @param userId     用户 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void allowExpendBenefits(AdminUserRightsTypeEnum rightsType, Long userId) {
        if (!adminUserRightsApi.calculateUserRightsEnough(userId, rightsType, null)) {
            if (AdminUserRightsTypeEnum.MAGIC_BEAN.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_BEAN_NOT_ENOUGH);
            }
            if (AdminUserRightsTypeEnum.MAGIC_IMAGE.getType().equals(rightsType.getType())) {
                throw exception(USER_RIGHTS_IMAGE_NOT_ENOUGH);
            }
            throw exception(USER_RIGHTS_NOT_ENOUGH);
        }
    }

    /**
     * 初始化会话记录
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void initAppConversationLog(Q request) {
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
        LogAppConversationCreateReqVO createRequest = new LogAppConversationCreateReqVO();
        String conversationUid = request.getConversationUid();
        if (StringUtils.isBlank(conversationUid)) {
            conversationUid = createAppConversationUid();
        }
        createRequest.setUid(conversationUid);
        createRequest.setAppUid(this.getUid());
        createRequest.setAppName(this.getName());
        createRequest.setAppMode(this.getModel());
        createRequest.setStatus(LogStatusEnum.ERROR.name());
        createRequest.setEndUser(request.getEndUser());
        createRequest.setCreator(String.valueOf(request.getUserId()));
        createRequest.setUpdater(String.valueOf(request.getUserId()));
        createRequest.setTenantId(this.getTenantId());
        createRequest.setFromScene(request.getScene());
        createRequest.setAiModel(this.getLlmModelType(request));
        this.buildAppConversationLog(request, createRequest);
        logAppConversationService.createAppLogConversation(createRequest);
        return createRequest.getUid();
    }

    /**
     * 更新会话状态为成功
     *
     * @param conversationUid 会话UID
     * @param request         请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void successAppConversationLog(String conversationUid, Q request) {
        log.info("应用执行成功：更新会话记录开始: 应用UID: {}, 会话 UID：{}...", this.getUid(), conversationUid);
        LogAppConversationStatusReqVO updateRequest = new LogAppConversationStatusReqVO();
        updateRequest.setUid(conversationUid);
        updateRequest.setStatus(LogStatusEnum.SUCCESS.name());
        updateRequest.setErrorCode(null);
        updateRequest.setErrorMsg(null);
        updateRequest.setAiModel(this.getLlmModelType(request));
        this.updateAppLogConversationStatus(updateRequest);
        log.info("应用执行成功：更新会话记录结束, 更新会话成功");
    }

    /**
     * 更新会话状态为失败
     *
     * @param conversationUid 会话UID
     * @param errorCode       错误码
     * @param errorMsg        错误消息
     * @param request         请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void failureAppConversationLog(String conversationUid, String errorCode, String errorMsg, Q request) {
        log.info("应用执行失败：更新会话记录开始: 应用ID: {}, 会话 UID：{}, 错误码：{}", this.getUid(), conversationUid, errorCode);
        LogAppConversationStatusReqVO updateRequest = new LogAppConversationStatusReqVO();
        updateRequest.setUid(conversationUid);
        updateRequest.setStatus(LogStatusEnum.ERROR.name());
        updateRequest.setErrorCode(errorCode);
        updateRequest.setErrorMsg(errorMsg);
        updateRequest.setAiModel(this.getLlmModelType(request));
        this.updateAppLogConversationStatus(updateRequest);
        log.info("应用执行失败：更新会话记录结束, 更新会话成功");
    }

    @JsonIgnore
    @JSONField(serialize = false)
    private void updateAppLogConversationStatus(LogAppConversationStatusReqVO request) {
        logAppConversationService.updateAppLogConversationStatus(request);
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
        return logAppConversationService.getAppLogConversation(conversationUid);
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
            PageResult<LogAppMessageDO> pageResult = logAppMessageService.pageAppLogMessage(reqVO);
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
        logAppMessageService.createAppLogMessage(messageCreateRequest);
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
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.setActionResponse(stepId, response);
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariableItem(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @param clazz 节点执行器
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariableItem(clazz, field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariable(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariable(clazz, field);
    }

    /**
     * 将变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String stepId, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putVariable(stepId, field, value);
    }

    /**
     * 将{@code Map}中的变量值设置到变量中
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(Class<? extends BaseActionHandler> clazz, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putVariable(clazz, field, value);
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariableItem(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariableItem(clazz, field);
    }

    /**
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariable(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariable(clazz, field);
    }

    /**
     * 将模型变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String stepId, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putModelVariable(stepId, field, value);
    }

    /**
     * 将{@code Map}中的变量值设置到模型变量中
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(Class<? extends BaseActionHandler> clazz, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putModelVariable(clazz, field, value);
    }

    /**
     * 获取步骤状态
     *
     * @param stepId 步骤ID
     * @param key    键
     * @param value  值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void addVariable(String stepId, String key, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        this.workflowConfig.addVariable(stepId, key, value);
    }

    /**
     * 异常
     *
     * @param errorCode 错误码
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected static ServiceException exception(ErrorCode errorCode) {
        return ServiceExceptionUtil.exception(errorCode);
    }

    /**
     * 异常处理
     *
     * @param errorCode 错误码
     * @param params    参数
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected static ServiceException exception(ErrorCode errorCode, Object... params) {
        return ServiceExceptionUtil.exception(errorCode, params);
    }

    /**
     * 异常
     *
     * @param errorCode 错误码
     * @param message   消息
     * @param params    参数
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected static ServiceException exceptionWithMessage(ErrorCode errorCode, String message, Object... params) {
        return ServiceExceptionUtil.exception0(errorCode.getCode(), message, params);
    }

    /**
     * 异常处理
     *
     * @param errorCode 错误码
     * @param message   消息
     * @param cause     异常
     * @param params    参数
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static ServiceException exceptionWithCause(ErrorCode errorCode, String message, Throwable cause, Object... params) {
        return ServiceExceptionUtil.exception1(errorCode.getCode(), message, cause, params);
    }

    /**
     * 异常处理
     *
     * @param errorCode 错误码
     * @param cause     异常
     * @param params    参数
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static ServiceException exceptionWithCause(ErrorCode errorCode, Throwable cause, Object... params) {
        return ServiceExceptionUtil.exceptionWithCause(errorCode, cause, params);
    }

    /**
     * 异常处理
     *
     * @param message 错误消息模板
     * @param params  参数
     * @return 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected static ServiceException invalidParamException(String message, Object... params) {
        return ServiceExceptionUtil.invalidParamException(message, params);
    }

    /**
     * 创建会话 UID
     *
     * @return 会话 UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static String createAppConversationUid() {
        return IdUtil.fastSimpleUUID();
    }

}
