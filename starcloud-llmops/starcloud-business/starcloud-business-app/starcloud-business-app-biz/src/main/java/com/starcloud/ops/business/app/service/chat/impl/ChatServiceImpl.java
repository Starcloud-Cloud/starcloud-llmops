package com.starcloud.ops.business.app.service.chat.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.recommend.RecommendAppFactory;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.FILE_TYPE_NOT_IMAGES;

/**
 * @author starcloud
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private AppService appService;

    @Resource
    private LogAppMessageMapper messageMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private UserBenefitsService benefitsService;

    @Resource
    private LogAppMessageService messageService;

    @Resource
    private LogAppConversationService conversationService;

    @Resource
    private DictDataService dictDataService;

    private static final String DEFAULT_AVATAR = "default_avatar";


    public void chatEndUser(ChatRequestVO request) {

        //分享ID，应用ID
        ChatAppEntity appEntity = AppFactory.factory(request);

        appEntity.asyncExecute(request);

    }

    @Override
    public void chat(ChatRequestVO request) {
        //分享ID，应用ID
        ChatAppEntity appEntity = AppFactory.factory(request);

        if (request.getSseEmitter() != null) {
            appEntity.asyncExecute(request);
        } else {
            appEntity.execute(request);
        }
    }

    @Override
    public List<String> chatSuggestion(String conversationUid) {
        List<String> suggestion = new ArrayList<>();
        String resultText = StringUtils.EMPTY;
        try {
            Long userId = WebFrameworkUtils.getLoginUserId();
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), userId);
            ChatMessageHistory history = preHistory(conversationUid, AppModelEnum.CHAT.name());
            String messageTemp = PromptTempletEnum.SUGGESTED_QUESTIONS.getTemp();
            ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Collections.singletonList(
                            HumanMessagePromptTemplate.fromTemplate(messageTemp)
                    )
            );
            ChatOpenAI chatOpenAi = new ChatOpenAI();
            ConversationBufferMemory memory = new ConversationBufferMemory();
            memory.setChatHistory(history);
            List<BaseVariable> variables = memory.loadMemoryVariables();
            PromptValue promptValue = chatPromptTemplate.formatPrompt(variables);
            BaseLLMResult<ChatCompletionResult> result = chatOpenAi.generatePrompt(Collections.singletonList(promptValue));
            resultText = result.getText();
            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), result.getUsage().getTotalTokens(), userId, conversationUid);
            return JSON.parseArray(resultText, String.class);
        } catch (Exception e) {
            log.error("suggestion error, openai result: {}.", resultText, e);
            return suggestion;
        }
    }

    @Override
    public PageResult<LogAppMessageDO> chatHistory(String conversationUid, Integer pageNo, Integer pageSize) {
        LogAppMessagePageReqVO logAppMessagePageReqVO = new LogAppMessagePageReqVO();
        logAppMessagePageReqVO.setAppConversationUid(conversationUid);
        logAppMessagePageReqVO.setPageNo(pageNo);
        logAppMessagePageReqVO.setPageSize(pageSize);
        PageResult<LogAppMessageDO> appMessagePage = messageService.userMessagePage(logAppMessagePageReqVO);
        Collections.reverse(appMessagePage.getList());
        return appMessagePage;
    }

    @Override
    public List<LogAppConversationRespVO> listConversation(String scene, String appUid) {
        LogAppConversationExportReqVO reqVO = new LogAppConversationExportReqVO();
        reqVO.setFromScene(scene);
        reqVO.setAppUid(appUid);
        List<LogAppConversationDO> appConversationList = conversationService.getAppConversationList(reqVO);
        return LogAppConversationConvert.INSTANCE.convertList(appConversationList);
    }

    @Override
    public LogAppConversationRespVO getConversation(String appUid, String scene) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        LogAppConversationDO conversation = conversationService.getUserRecentlyConversation(appUid, String.valueOf(loginUserId), scene);
        return LogAppConversationConvert.INSTANCE.convert(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createChatApp(String uid, String name) {
        AppRespVO recommendApp;
        if (StringUtils.isBlank(uid)) {
            recommendApp = RecommendAppFactory.emptyChatRobotApp();
        } else {
            recommendApp = appService.getRecommendApp(uid);
        }

        AppEntity appEntity = AppConvert.INSTANCE.convertApp(recommendApp);
        appEntity.setUid(null);
        appEntity.setName(name);
        appEntity.insert();
        return appEntity.getUid();
    }

    @Override
    public String updateAppAvatar(String appUid, MultipartFile file) throws IOException {
        String suffix = getSuffix(file.getOriginalFilename());
        checkFileType(suffix);
        String avatar = fileApi.createFile("avatar/" + IdUtil.fastSimpleUUID() + "." + suffix, IoUtil.readBytes(file.getInputStream()));
        AppUpdateReqVO appUpdateReqVO = new AppUpdateReqVO();
        appUpdateReqVO.setUid(appUid);
        appUpdateReqVO.setImages(Arrays.asList(avatar));
        appService.modify(appUpdateReqVO);
        return avatar;
    }

    private void checkFileType(String suffix) {
        List<String> imageTypes = Arrays.asList("jpg", "jpeg", "png");
        if (!imageTypes.contains(suffix.toLowerCase())) {
            throw exception(FILE_TYPE_NOT_IMAGES);
        }
    }

    private String getSuffix(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1).toLowerCase();
            }
        }
        return "";
    }

    @Override
    public List<String> defaultAvatar() {
        DictDataExportReqVO dataExportReqVO = new DictDataExportReqVO();
        dataExportReqVO.setDictType(DEFAULT_AVATAR);
        dataExportReqVO.setStatus(0);
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(dataExportReqVO);
        return dictDataList.stream().map(DictDataDO::getValue).collect(Collectors.toList());
    }

    private ChatMessageHistory preHistory(String conversationId, String appMode) {
        ChatMessageHistory history = new ChatMessageHistory();
        LambdaQueryWrapper<LogAppMessageDO> queryWrapper = Wrappers.lambdaQuery(LogAppMessageDO.class)
                .eq(LogAppMessageDO::getAppConversationUid, conversationId)
                .eq(LogAppMessageDO::getAppMode, appMode)
                .eq(LogAppMessageDO::getStatus, "SUCCESS")
                .orderByAsc(LogAppMessageDO::getId);
        List<LogAppMessageDO> appMessages = messageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(appMessages)) {
            return history;
        }
        for (LogAppMessageDO appMessage : appMessages) {
            history.addUserMessage(appMessage.getMessage());
            history.addAiMessage(appMessage.getAnswer());
        }
        return history;
    }


}
