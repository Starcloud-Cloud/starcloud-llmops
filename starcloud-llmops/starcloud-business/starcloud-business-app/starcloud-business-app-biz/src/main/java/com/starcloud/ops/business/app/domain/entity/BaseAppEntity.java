package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
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

    private static LogAppConversationService logAppConversationService = SpringUtil.getBean(LogAppConversationService.class);

    private static LogAppMessageService logAppMessageService = SpringUtil.getBean(LogAppMessageService.class);

    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);


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

    private String creator;

    private String updater;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    /**
     * 校验
     */
    protected abstract void _validate();

    /**
     * 执行应用
     */
    protected abstract R _execute(Q req);

    /**
     * 执行应用
     */
    protected abstract void _aexecute(Q req);

    /**
     * 历史记录初始化
     */
    protected abstract void _initHistory(Q req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS);

    /**
     * 创建会话记录
     *
     * @param reqVO
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


    protected abstract <C> C _parseConversationConfig(String conversationConfig);


    protected void updateLogConversation(Consumer<LogAppConversationDO> consumer) {
        LogAppConversationDO appConversationDO = new LogAppConversationDO();

        consumer.accept(appConversationDO);
    }


    /**
     * 校验
     */
    public void validate() {

        this._validate();
    }

    private String parseConversationUid(String conversationUid) {

        //@todo 判断是否需要过期

        //新建个
        if (StrUtil.isBlank(conversationUid)) {
            conversationUid = IdUtil.fastSimpleUUID();
        }

        return conversationUid;
    }


    /**
     * 同步执行应用
     */
    public R execute(Q req) {

        try {

            log.info("app start:{}, {}", this.getUid(), this.getName());

            if (req.getUserId() == null) {
                req.setUserId(SecurityFrameworkUtils.getLoginUserId());
            }
            this.validate();

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

            R result = this._execute(req);

            this.updateAppConversationLog(req.getConversationUid(), true);
            log.info("app end: {} {}", this.getUid(), result);
            return result;
        } catch (ServiceException e) {
            log.error("app execute is fail: {}", e.getMessage());
            //应该没有异常的，APP内部执行抓取异常处理了 @todo 这里创建一个异常的 message 对象
            this.updateAppConversationLog(req.getConversationUid(), false);
            throw e;
        } catch (Exception e) {
            log.error("app execute is fail: {}", e.getMessage());
            //应该没有异常的，APP内部执行抓取异常处理了 @todo 这里创建一个异常的 message 对象
            this.updateAppConversationLog(req.getConversationUid(), false);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, e.getMessage());
        }
    }


    /**
     * 异步执行
     * log 交由具体类去实现
     *
     * @param req
     */
    public void aexecute(Q req) {

        try {

            log.info("app async start:{}, {}", this.getUid(), this.getName());
            req.setUserId(SecurityFrameworkUtils.getLoginUserId());
            this.validate();

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

            });

        } catch (Exception e) {

            log.error("app execute is fail: {}", e.getMessage(), e);

            //直接 会话异常
            this.updateAppConversationLog(req.getConversationUid(), false);

            throw e;
        }

    }


    protected void allowExpendBenefits(String benefitsType, Long userId) {

        userBenefitsService.allowExpendBenefits(benefitsType, userId);
    }

    /**
     * 新增应用
     */
    public void insert() {
        this.validate();
        this._insert();
    }

    /**
     * 更新应用
     */
    public void update() {
        this.validate();
        this._update();
    }


    protected String createAppConversationLog(Q req) {

        if (StrUtil.isBlank(req.getConversationUid())) {

            LogAppConversationCreateReqVO reqVO = new LogAppConversationCreateReqVO();
            reqVO.setUid(IdUtil.fastSimpleUUID());

            reqVO.setAppUid(this.getUid());
            reqVO.setAppName(this.getName());
            reqVO.setAppMode(this.getModel());
            reqVO.setStatus(LogStatusEnum.ERROR.name());

            reqVO.setFromScene(req.getScene());

            this._createAppConversationLog(req, reqVO);

            logAppConversationService.createAppConversation(reqVO);

            return reqVO.getUid();
        }

        return req.getConversationUid();
    }

    protected LogAppMessageCreateReqVO createAppMessage(Consumer<LogAppMessageCreateReqVO> consumer) {

        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();

//        messageCreateReqVO.setAppConversationUid(req.getConversationUid());
        messageCreateReqVO.setUid(IdUtil.fastSimpleUUID());

        messageCreateReqVO.setAppUid(this.getUid());
        messageCreateReqVO.setAppMode(this.getModel());

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

    private LogAppConversationDO getAppConversation(String conversationId) {

        return logAppConversationService.getAppConversation(conversationId);
    }

    private List<LogAppMessageDO> getAppConversationMessages(String conversationId) {

        if (StrUtil.isNotBlank(conversationId)) {
            LogAppMessagePageReqVO reqVO = new LogAppMessagePageReqVO();
            reqVO.setPageSize(100);
            reqVO.setPageNo(1);
            reqVO.setAppConversationUid(conversationId);
            PageResult<LogAppMessageDO> pageResult = logAppMessageService.getAppMessagePage(reqVO);
            return Optional.ofNullable(pageResult).map(PageResult::getList).orElse(new ArrayList<>());
        }

        return new ArrayList<>();

    }


}
