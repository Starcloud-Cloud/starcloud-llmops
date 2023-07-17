package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonParamsEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Slf4j
@Data
public class ImageAppEntity extends BaseAppEntity<ImageReqVO, JsonParamsEntity> {


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

    /**
     * 校验
     */
    @Override
    protected void _validate() {
        getChatConfig().validate();
    }

    /**
     * 历史记录初始化
     */
    @Override
    protected void _initHistory(ImageReqVO req, LogAppConversationDO logAppConversationDO, List<LogAppMessageDO> logAppMessageDOS) {

        //preHistory(request.getConversationUid(), AppModelEnum.CHAT.name());

        ImageConfigEntity imageConfig = this._parseConversationConfig(logAppConversationDO.getAppConfig());

        //用 req 的参数 在去覆盖默认参数

    }

    @Override
    protected void _createAppConversationLog(ImageReqVO req, LogAppConversationCreateReqVO reqVO) {

    }


    @Override
    protected JsonParamsEntity _execute(ImageReqVO req) {


        return new JsonParamsEntity();

    }

    @Override
    protected void _aexecute(ImageReqVO req) {

        this._execute(req);

        req.getSseEmitter().complete();

    }

    @Override
    protected ImageConfigEntity _parseConversationConfig(String conversationConfig) {


        ImageConfigEntity imageConfig = JSON.parseObject(conversationConfig, ImageConfigEntity.class);
        return imageConfig;
    }

    /**
     * 新增应用
     */
    @Override
    protected void _insert() {

        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    protected void _update() {

        getAppRepository().update(this);
    }


}
