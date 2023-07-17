package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.api.app.vo.request.execute.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import lombok.Data;

import java.util.List;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class AppEntity<Q, R> extends BaseAppEntity<AppExecuteReqVO, LogAppConversationCreateReqVO> {


    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    @Override
    protected void _validate() {
        if (AppModelEnum.COMPLETION.name().equals(this.getModel())) {
            getWorkflowConfig().validate();
        } else if (AppModelEnum.CHAT.name().equals(this.getModel())) {
            getChatConfig().validate();
        }
    }

    @Override
    protected LogAppConversationCreateReqVO _execute(AppExecuteReqVO req) {
        //@todo 执行应用逻辑，主要 回调的处理

        return null;
    }

    @Override
    protected void _initHistory(AppExecuteReqVO req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS) {

    }

    @Override
    protected void _insert() {
        getAppRepository().insert(this);
    }

    @Override
    protected void _update() {
        getAppRepository().update(this);
    }

    @Override
    protected <C> C _parseConversationConfig(String conversationConfig) {
        return null;
    }

}
