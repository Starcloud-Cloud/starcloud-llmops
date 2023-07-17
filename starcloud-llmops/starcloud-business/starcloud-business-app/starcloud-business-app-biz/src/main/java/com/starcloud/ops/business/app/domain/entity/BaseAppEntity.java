package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public abstract class BaseAppEntity<Q, R> {


    //@todo 封装为repository
    private static LogAppConversationService logAppConversationService = SpringUtil.getBean(LogAppConversationService.class);

    private static LogAppMessageService logAppMessageService = SpringUtil.getBean(LogAppMessageService.class);


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
     * 历史记录初始化
     */
    protected abstract void _initHistory(Q req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS);


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
     * 执行应用
     */
    public R execute(Q req) {

        try {

            this.validate();

            AppContextReqVO appContextReqVO = (AppContextReqVO) req;

            appContextReqVO.setConversationUid(this.parseConversationUid(appContextReqVO.getConversationUid()));

            //@todo 替换为 repository 实现
            LogAppConversationDO logAppConversationDO = this.getAppConversation(appContextReqVO.getConversationUid());
            List<LogAppMessageDO> logAppMessageDOS = this.getAppConversationMessages(appContextReqVO.getConversationUid());

            this._initHistory(req, logAppConversationDO, logAppMessageDOS);

            R result = this._execute(req);

            return result;

        } catch (Exception e) {

            log.error("app execute is fail: {}", e.getMessage(), e);

            throw e;
        }

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


    protected long createAppMessage(Consumer<LogAppMessageCreateReqVO> consumer) {

        LogAppMessageCreateReqVO reqVO = new LogAppMessageCreateReqVO();

        LogAppMessageCreateReqVO messageCreateReqVO = new LogAppMessageCreateReqVO();
        messageCreateReqVO.setUid(IdUtil.getSnowflakeNextIdStr());
        messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.0200"));

        consumer.accept(reqVO);

        return logAppMessageService.createAppMessage(reqVO);
    }

    protected <C> C getConversationConfig(String conversationId) {

        LogAppConversationDO logAppConversationDO = this.getAppConversation(conversationId);

        return this._parseConversationConfig(logAppConversationDO.getAppConfig());
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
