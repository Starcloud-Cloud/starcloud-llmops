package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
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
    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

    /**
     * 会话记录服务
     */
    private static LogAppConversationService logAppConversationService = SpringUtil.getBean(LogAppConversationService.class);

    /**
     * 消息记录服务
     */
    private static LogAppMessageService logAppMessageService = SpringUtil.getBean(LogAppMessageService.class);

    /**
     * 线程池
     */
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
    @JSONField(serialize = false)
    protected abstract void _validate(Q request);

    /**
     * 模版方法：执行应用
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @JSONField(serialize = false)
    protected abstract R _execute(Q request);

    /**
     * 模版方法：异步执行应用
     *
     * @param request 请求参数
     */
    @JSONField(serialize = false)
    protected abstract void _aexecute(Q request);

    /**
     * 模版方法：执行应用后置处理方法
     *
     * @param request   请求参数
     * @param throwable 异常
     */
    @JSONField(serialize = false)
    protected abstract void _afterExecute(Q request, Throwable throwable);

    /**
     * 模版方法：历史记录初始化
     *
     * @param request            请求参数
     * @param logAppConversation 会话记录
     * @param logAppMessageList  消息记录
     */
    @JSONField(serialize = false)
    protected abstract void _initHistory(Q request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList);

    /**
     * 模版方法：创建会话记录
     *
     * @param request       请求参数
     * @param createRequest 请求参数
     */
    @JSONField(serialize = false)
    protected abstract void _createAppConversationLog(Q request, LogAppConversationCreateReqVO createRequest);

    /**
     * 模版方法：解析会话配置
     *
     * @param conversationConfig 会话配置
     * @param <C>                会话配置
     * @return 会话配置
     */
    @JSONField(serialize = false)
    protected abstract <C> C _parseConversationConfig(String conversationConfig);

    /**
     * 模版方法：新增应用
     */
    @JSONField(serialize = false)
    protected abstract void _insert();

    /**
     * 模版方法：更新应用
     */
    @JSONField(serialize = false)
    protected abstract void _update();

    /**
     * 获取当前执行记录的主体用户，会做主体用户做如下操作。默认是当前用户态
     * 1，扣除权益
     * 2，记录日志
     *
     * @return 用户 ID
     */
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
    @JSONField(serialize = false)
    public R execute(Q request) {
        try {
            log.info("app start:{}, {}, {}", this.getUid(), this.getName(), request.getUserId());

            // 执行用户
            if (request.getUserId() == null) {
                request.setUserId(this.getRunUserId(request));
            }

            // 基础校验
            this.validate(request);

            // 会话记录
            if (StrUtil.isNotBlank(request.getConversationUid())) {
                LogAppConversationDO logAppConversationDO = this.getAppConversation(request.getConversationUid());
                if (logAppConversationDO == null) {
                    String conversationUid = this.createAppConversationLog(request);
                    request.setConversationUid(conversationUid);
                } else {
                    List<LogAppMessageDO> logAppMessageList = this.getAppConversationMessages(request.getConversationUid());

                    this._initHistory(request, logAppConversationDO, logAppMessageList);
                }
            } else {
                //会话uid为空,自动创建
                String conversationUid = this.createAppConversationLog(request);
                request.setConversationUid(conversationUid);
            }

            // 执行应用
            R result = this._execute(request);
            this._afterExecute(request, null);

            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), true);
            log.info("app end: {} {}", this.getUid(), result);
            return result;

        } catch (ServiceException exception) {
            log.error("app execute is fail: {}", exception.getMessage(), exception);
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);
            this._afterExecute(request, exception);
            throw exception;

        } catch (Exception exception) {
            log.error("app execute is fail: {}", exception.getMessage(), exception);
            // 更新会话记录
            this.updateAppConversationLog(request.getConversationUid(), false);
            this._afterExecute(request, exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage());
        }
    }

    /**
     * 异步执行
     * log 交由具体类去实现
     *
     * @param request 请求参数
     */
    @JSONField(serialize = false)
    public void asyncExecute(Q request) {
        try {
            log.info("app async start:{}, {}", this.getUid(), this.getName());

            // 执行用户
            if (request.getUserId() == null) {
                request.setUserId(this.getRunUserId(request));
            }

            // 基础校验
            this.validate(request);

            //会话处理
            if (StrUtil.isNotBlank(request.getConversationUid())) {
                LogAppConversationDO logAppConversationDO = this.getAppConversation(request.getConversationUid());
                if (logAppConversationDO == null) {
                    String conversationUid = this.createAppConversationLog(request);
                    request.setConversationUid(conversationUid);
                } else {
                    List<LogAppMessageDO> logAppMessageList = this.getAppConversationMessages(request.getConversationUid());

                    this._initHistory(request, logAppConversationDO, logAppMessageList);
                }

            } else {
                String conversationUid = this.createAppConversationLog(request);
                request.setConversationUid(conversationUid);
            }

            // 异步执行应用
            threadExecutor.asyncExecute(() -> {
                try {
                    this._aexecute(request);
                    this._afterExecute(request, null);
                    log.info("app async end: {}", this.getUid());
                    this.updateAppConversationLog(request.getConversationUid(), true);
                } catch (Exception exception) {
                    log.error("app async execute is fail: {}", exception.getMessage(), exception);
                    this.updateAppConversationLog(request.getConversationUid(), false);
                    this._afterExecute(request, exception);
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage());
                }
            });

        } catch (ServiceException exception) {
            log.error("app ServiceException is fail: {}", exception.getMessage(), exception);
            //直接 会话异常
            this.updateAppConversationLog(request.getConversationUid(), false);
            this._afterExecute(request, exception);
            throw exception;
        } catch (Exception exception) {
            log.error("app exception is fail: {}", exception.getMessage(), exception);
            //直接 会话异常
            this.updateAppConversationLog(request.getConversationUid(), false);
            this._afterExecute(request, exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, exception.getMessage());
        }
    }

    /**
     * 权益检测
     *
     * @param benefitsType 权益类型
     * @param userId       用户 ID
     */
    @JSONField(serialize = false)
    protected void allowExpendBenefits(String benefitsType, Long userId) {
        userBenefitsService.allowExpendBenefits(benefitsType, userId);
    }

    /**
     * 基础校验
     *
     * @param request 请求参数
     */
    @JSONField(serialize = false)
    public void validate(Q request) {
        this._validate(request);
    }

    /**
     * 新增应用
     */
    @JSONField(serialize = false)
    public void insert() {
        // 设置 uid
        if (StrUtil.isBlank(this.getUid())) {
            this.setUid(IdUtil.fastSimpleUUID());
        }
        this.validate(null);
        this._insert();
    }

    /**
     * 更新应用
     */
    @JSONField(serialize = false)
    public void update() {
        this.validate(null);
        this._update();
    }

    /**
     * 创建一条新的会话
     *
     * @param request 请求参数
     * @return 会话 UID
     */
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
        this._createAppConversationLog(request, reqVO);
        logAppConversationService.createAppConversation(reqVO);
        return reqVO.getUid();
    }

    /**
     * 创建日志消息
     *
     * @param consumer 消息创建
     * @return 消息对象
     */
    @JSONField(serialize = false)
    public LogAppMessageCreateReqVO createAppMessage(Consumer<LogAppMessageCreateReqVO> consumer) {

        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();
//        messageCreateReqVO.setAppConversationUid(req.getConversationUid());
        messageCreateReqVO.setUid(IdUtil.fastSimpleUUID());
        messageCreateReqVO.setAppUid(this.getUid());
        messageCreateReqVO.setAppMode(this.getModel());
        messageCreateReqVO.setTenantId(this.getTenantId());

        consumer.accept(messageCreateReqVO);
        logAppMessageService.createAppMessage(messageCreateReqVO);
        return messageCreateReqVO;
    }

    /**
     * 判断执行情况，最最后的 会话状态更新
     *
     * @param conversationUid 日志会话 UID
     */
    @JSONField(serialize = false)
    protected void updateAppConversationLog(String conversationUid, Boolean status) {
        logAppConversationService.updateAppConversationStatus(conversationUid, status ? "SUCCESS" : "ERROR");
    }

    /**
     * 根据日志会话 UID 获取日志会话
     *
     * @param conversationUid 日志会话 UID
     * @return 会话对象
     */
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
     * 执行成功后，响应更新
     *
     * @param stepId   步骤ID
     * @param response 响应
     */
    @JSONField(serialize = false)
    public void setActionResponse(String stepId, ActionResponse response) {
        workflowConfig.setActionResponse(stepId, response);
    }
}
