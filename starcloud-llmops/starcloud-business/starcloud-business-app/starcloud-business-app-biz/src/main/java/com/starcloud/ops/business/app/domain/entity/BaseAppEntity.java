package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public abstract class BaseAppEntity<Q extends AppContextReqVO, R> {

    /**
     * 会话记录服务
     */
    private static LogAppConversationService logAppConversationService = SpringUtil.getBean(LogAppConversationService.class);

    /**
     * 消息记录服务
     */
    private static LogAppMessageService logAppMessageService = SpringUtil.getBean(LogAppMessageService.class);

    /**
     * 用户权益服务
     */
    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

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
     * 基础校验
     */
    protected abstract void _validate(Q req);

    /**
     * 执行应用
     */
    protected abstract R _execute(Q req);

    /**
     * 执行应用
     */
    protected abstract void _aexecute(Q req);

    /**
     * 执行后执行
     */
    protected abstract void _afterExecute(Q req, Throwable t);

    /**
     * 历史记录初始化
     */
    protected abstract void _initHistory(Q req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageList);

    /**
     * 创建会话记录
     *
     * @param reqVO 请求参数
     */
    protected abstract void _createAppConversationLog(Q req, LogAppConversationCreateReqVO reqVO);

    /**
     * 新增应用
     */
    protected abstract void _insert();

    /**
     * 更新应用
     */
    protected abstract void _update();

    /**
     * 解析会话配置
     *
     * @param conversationConfig 会话配置
     * @param <C>                会话配置
     * @return 会话配置
     */
    protected abstract <C> C _parseConversationConfig(String conversationConfig);

    /**
     * 更新会话记录
     *
     * @param consumer 消费者
     */
    protected void updateLogConversation(Consumer<LogAppConversationDO> consumer) {
        LogAppConversationDO appConversationDO = new LogAppConversationDO();
        consumer.accept(appConversationDO);
    }

    /**
     * 解析会话
     *
     * @param conversationUid 会话 uid
     * @return 会话
     */
    private String parseConversationUid(String conversationUid) {

        //@todo 判断是否需要过期

        //新建个
        if (StrUtil.isBlank(conversationUid)) {
            conversationUid = IdUtil.fastSimpleUUID();
        }

        return conversationUid;
    }

    /**
     * 获取当前执行记录的主体用户，会做主体用户做如下操作.默认都是当前用户态
     * 1，扣点
     * 2，记录log
     *
     * @return 用户id
     */
    protected Long getRunUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    /**
     * 同步执行应用
     */
    public R execute(Q req) {

        try {

            this.validate(req);

            if (req.getUserId() == null) {
                req.setUserId(this.getRunUserId());
            }

            log.info("app start:{}, {}, {}", this.getUid(), this.getName(), req.getUserId());

            if (StrUtil.isNotBlank(req.getConversationUid())) {

                LogAppConversationDO logAppConversationDO = this.getAppConversation(req.getConversationUid());
                List<LogAppMessageDO> logAppMessageDOS = this.getAppConversationMessages(req.getConversationUid());
                Collections.reverse(logAppMessageDOS);
                this._initHistory(req, logAppConversationDO, logAppMessageDOS);

            } else {

                //会话uid为空,自动创建
                String conversationUid = this.createAppConversationLog(req);

                req.setConversationUid(conversationUid);
            }

            R result = this._execute(req);
            this._afterExecute(req, null);

            this.updateAppConversationLog(req.getConversationUid(), true);
            log.info("app end: {} {}", this.getUid(), result);
            return result;
        } catch (ServiceException e) {
            log.error("app execute is fail: {}", e.getMessage(), e);
            //应该没有异常的，APP内部执行抓取异常处理了 @todo 这里创建一个异常的 message 对象
            this.updateAppConversationLog(req.getConversationUid(), false);

            this._afterExecute(req, e);

            throw e;
        } catch (Exception e) {
            log.error("app execute is fail: {}", e.getMessage(), e);
            //应该没有异常的，APP内部执行抓取异常处理了 @todo 这里创建一个异常的 message 对象
            this.updateAppConversationLog(req.getConversationUid(), false);

            this._afterExecute(req, e);

            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, e.getMessage());
        }
    }

    /**
     * 异步执行
     * log 交由具体类去实现
     *
     * @param req 请求参数
     */
    public void aexecute(Q req) {

        try {

            log.info("app async start:{}, {}", this.getUid(), this.getName());
            req.setUserId(SecurityFrameworkUtils.getLoginUserId());

            this.validate(req);

            //会话uid为空
            if (StrUtil.isNotBlank(req.getConversationUid())) {

                LogAppConversationDO logAppConversationDO = this.getAppConversation(req.getConversationUid());
                List<LogAppMessageDO> logAppMessageDOS = this.getAppConversationMessages(req.getConversationUid());
                Collections.reverse(logAppMessageDOS);
                this._initHistory(req, logAppConversationDO, logAppMessageDOS);

            } else {

                String conversationUid = this.createAppConversationLog(req);

                req.setConversationUid(conversationUid);
            }

            threadExecutor.asyncExecute(() -> {

                this._aexecute(req);
                this._afterExecute(req, null);

            });
        } catch (ServiceException e) {
            log.error("app ServiceException is fail: {}", e.getMessage(), e);


            //直接 会话异常
            this.updateAppConversationLog(req.getConversationUid(), false);

            this._afterExecute(req, e);

            throw e;

        } catch (Exception e) {

            log.error("app exception is fail: {}", e.getMessage(), e);

            //直接 会话异常
            this.updateAppConversationLog(req.getConversationUid(), false);

            this._afterExecute(req, e);

            throw e;
        }

    }

    /**
     * 权益检测
     *
     * @param benefitsType 权益类型
     * @param userId       用户id
     */
    protected void allowExpendBenefits(String benefitsType, Long userId) {
        userBenefitsService.allowExpendBenefits(benefitsType, userId);
    }

    /**
     * 校验
     */
    public void validate(Q req) {
        this._validate(req);
    }

    /**
     * 新增应用
     */
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
    public void update() {
        this.validate(null);
        this._update();
    }

    /**
     * 创建会话
     *
     * @param req 请求参数
     * @return 会话 uid
     */
    protected String createAppConversationLog(Q req) {

        if (StrUtil.isBlank(req.getConversationUid())) {

            LogAppConversationCreateReqVO reqVO = new LogAppConversationCreateReqVO();
            reqVO.setUid(IdUtil.fastSimpleUUID());

            reqVO.setAppUid(this.getUid());
            reqVO.setAppName(this.getName());
            reqVO.setAppMode(this.getModel());
            reqVO.setStatus(LogStatusEnum.ERROR.name());

            reqVO.setEndUser(req.getEndUser());
            reqVO.setCreator(String.valueOf(req.getUserId()));
            reqVO.setUpdater(String.valueOf(req.getUserId()));
            reqVO.setTenantId(this.getTenantId());

            reqVO.setFromScene(req.getScene());

            this._createAppConversationLog(req, reqVO);

            logAppConversationService.createAppConversation(reqVO);

            return reqVO.getUid();
        }

        return req.getConversationUid();
    }

    /**
     * 创建日志消息
     *
     * @param consumer 消息创建
     * @return 消息对象
     */
    protected LogAppMessageCreateReqVO createAppMessage(Consumer<LogAppMessageCreateReqVO> consumer) {

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
     */
    protected void updateAppConversationLog(String conversationUid, Boolean status) {


        logAppConversationService.updateAppConversationStatus(conversationUid, status ? "SUCCESS" : "ERROR");

    }

    /**
     * 获取会话
     *
     * @param conversationId 会话id
     * @return 会话对象
     */
    private LogAppConversationDO getAppConversation(String conversationId) {

        return logAppConversationService.getAppConversation(conversationId);
    }

    /**
     * 获取会话消息
     *
     * @param conversationId 会话id
     * @return 会话消息列表
     */
    private List<LogAppMessageDO> getAppConversationMessages(String conversationId) {

        if (StrUtil.isNotBlank(conversationId)) {
            LogAppMessagePageReqVO reqVO = new LogAppMessagePageReqVO();
            reqVO.setPageSize(100);
            reqVO.setPageNo(1);
            reqVO.setAppConversationUid(conversationId);
            PageResult<LogAppMessageDO> pageResult = logAppMessageService.userMessagePage(reqVO);
            return Optional.ofNullable(pageResult).map(PageResult::getList).orElse(new ArrayList<>());
        }

        return new ArrayList<>();

    }

}
