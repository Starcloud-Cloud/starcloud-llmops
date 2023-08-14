package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.OptimizePromptReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private AppService appService;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private LogAppConversationMapper logAppConversationMapper;

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    @Resource
    private AppDictionaryService appDictionaryService;

    /**
     * 获取图片元数据
     *
     * @return 图片元数据
     */
    @Override
    public Map<String, List<ImageMetaDTO>> meta() {
        Map<String, List<ImageMetaDTO>> meta = new HashMap<>(8);
        meta.put("model", ImageUtils.engineList());
        meta.put("upscalingModel", ImageUtils.upscalingEngineList());
        meta.put("samples", ImageUtils.samplesList());
        meta.put("imageSize", ImageUtils.imageSizeList());
        meta.put("sampler", ImageUtils.samplerList());
        meta.put("guidancePreset", ImageUtils.guidancePresetList());
        meta.put("stylePreset", ImageUtils.stylePresetList());
        meta.put("examplePrompt", appDictionaryService.examplePrompt());
        return meta;
    }

    /**
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    @Override
    public ImageRespVO historyGenerateImages() {
        ImageRespVO response = new ImageRespVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        LogAppConversationDO conversation = this.getConversation(null, loginUserId);
        response.setConversationUid(conversation.getUid());
        // 查询会话下的消息
        LambdaQueryWrapper<LogAppMessageDO> messageWrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        messageWrapper.select(LogAppMessageDO::getUid, LogAppMessageDO::getCreateTime, LogAppMessageDO::getMessage, LogAppMessageDO::getAnswer, LogAppMessageDO::getAppConfig);
        messageWrapper.eq(LogAppMessageDO::getAppConversationUid, conversation.getUid());
        messageWrapper.eq(LogAppMessageDO::getAppMode, AppModelEnum.BASE_GENERATE_IMAGE.name());
        messageWrapper.eq(LogAppMessageDO::getCreator, Long.toString(WebFrameworkUtils.getLoginUserId()));
        messageWrapper.eq(LogAppMessageDO::getStatus, LogStatusEnum.SUCCESS.name());
        messageWrapper.eq(LogAppMessageDO::getDeleted, Boolean.FALSE);
        messageWrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        List<LogAppMessageDO> messageList = logAppMessageMapper.selectList(messageWrapper);
        // 处理图片消息数据
        List<ImageMessageRespVO> list = CollectionUtil.emptyIfNull(messageList).stream().map(item -> {
            // 如果没有结果，返回 null
            if (StringUtils.isBlank(item.getAnswer())) {
                return null;
            }
            List<ImageDTO> imageList = JSONUtil.toBean(item.getAnswer(), new TypeReference<List<ImageDTO>>() {
            }, true);
            // 排除掉空的和没有 url 的图片
            imageList = imageList.stream().filter(Objects::nonNull).filter(imageItem -> StringUtils.isNotBlank(imageItem.getUrl())).collect(Collectors.toList());
            // 如果没有结果，返回 null
            if (CollectionUtil.isEmpty(imageList)) {
                return null;
            }
            ImageMessageRespVO imageResponse = new ImageMessageRespVO();
            imageResponse.setPrompt(item.getMessage());
            imageResponse.setCreateTime(item.getCreateTime());
            imageResponse.setImages(imageList);
            ImageRequest imageRequest = JSONUtil.toBean(item.getAppConfig(), ImageRequest.class);
            if (imageRequest != null) {
                imageResponse.setNegativePrompt(ImageUtils.handleNegativePrompt(imageRequest.getNegativePrompt(), Boolean.FALSE));
                imageResponse.setEngine(imageRequest.getEngine());
                imageResponse.setWidth(imageRequest.getWidth());
                imageResponse.setHeight(imageRequest.getHeight());
                imageResponse.setSteps(imageRequest.getSteps());
            }
            return imageResponse;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        response.setMessages(list);
        return response;
    }

    /**
     * 文本生成图片
     *
     * @param request 请求参数
     * @return 图片信息
     */
    @Override
    public ImageMessageRespVO generateImage(ImageReqVO request) {
        SseEmitter emitter = new SseEmitter(60000L);
        request.setSseEmitter(emitter);
        // 处理负面提示
        ImageRequest imageRequest = request.getImageRequest();
        String negativePrompt = ImageUtils.handleNegativePrompt(imageRequest.getNegativePrompt(), Boolean.TRUE);
        imageRequest.setNegativePrompt(negativePrompt);
        request.setImageRequest(imageRequest);
        // 构建 ImageAppEntity
        ImageAppEntity factory = AppFactory.factory(request);
        // 生成图片
        return factory.execute(request);
    }

    /**
     * 获取优化提示应用列表
     *
     * @return 应用列表
     */
    @Override
    public List<Option> getOptimizePromptAppList() {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class);
        wrapper.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        wrapper.like(AppMarketDO::getTags, "Optimize Prompt");
        List<AppMarketDO> appMarketList = appMarketMapper.selectList(wrapper);
        return CollectionUtil.emptyIfNull(appMarketList).stream().map(item -> {
            Option option = new Option();
            option.setLabel(item.getName());
            option.setValue(item.getUid());
            option.setDescription(item.getDescription());
            return option;
        }).collect(Collectors.toList());
    }

    /**
     * 优化提示
     *
     * @param request 请求参数
     */
    @Override
    public void optimizePrompt(OptimizePromptReqVO request) {
        AppExecuteReqVO appExecuteReqVO = new AppExecuteReqVO();
        appExecuteReqVO.setSseEmitter(request.getSseEmitter());
        appExecuteReqVO.setScene(AppSceneEnum.WEB_MARKET.name());
        String appUid = request.getAppUid();
        AppValidate.notBlank(appUid, ErrorCodeConstants.APP_UID_IS_REQUIRED, appUid);

        AppMarketDO appMarketDO = appMarketMapper.get(appUid, Boolean.FALSE);
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, appUid);
        AppReqVO appReqVO = AppConvert.INSTANCE.convertRequest(appMarketDO);

        Map<String, Object> variables = new HashMap<>(2);
        variables.put("content", request.getText());
        variables.put("language", request.getTargetLanguage());
        appReqVO.addVariables(variables);
        appExecuteReqVO.setAppReqVO(appReqVO);
        appExecuteReqVO.setAppUid(appMarketDO.getUid());
        appMarketDO.setScenes(AppSceneEnum.WEB_MARKET.name());

        appService.asyncExecute(appExecuteReqVO);
    }

    /**
     * 获取会话记录 <br>
     * 1. 如果会话记录id不为空，则根据会话记录id查询 <br>
     * 2. 如果会话记录id为空，则根据用户id查询最新的一条会话记录
     * 3. 如果没有会话记录，则创建一条会话记录
     *
     * @param conversationUid 会话记录id
     * @param userId          用户id
     * @return 会话记录
     */
    @SuppressWarnings("all")
    private LogAppConversationDO getConversation(String conversationUid, Long userId) {
        LambdaQueryWrapper<LogAppConversationDO> conversationWrapper = Wrappers.lambdaQuery(LogAppConversationDO.class);
        conversationWrapper.eq(LogAppConversationDO::getAppMode, AppModelEnum.BASE_GENERATE_IMAGE.name());
        conversationWrapper.eq(LogAppConversationDO::getCreator, Long.toString(userId));
        conversationWrapper.eq(LogAppConversationDO::getDeleted, Boolean.FALSE);
        if (StringUtils.isNotBlank(conversationUid)) {
            conversationWrapper.eq(LogAppConversationDO::getUid, conversationUid);
        } else {
            conversationWrapper.orderByDesc(LogAppConversationDO::getCreateTime);
            conversationWrapper.last("limit 1");
        }
        LogAppConversationDO conversation = logAppConversationMapper.selectOne(conversationWrapper);
        if (conversation != null) {
            return conversation;
        }

        // 新增一条会话记录
        conversation = new LogAppConversationDO();
        String fastConversationUid = IdUtil.fastSimpleUUID();
        conversation.setUid(fastConversationUid);
        conversation.setAppMode(AppModelEnum.BASE_GENERATE_IMAGE.name());
        conversation.setAppName("AI图片生成");
        conversation.setStatus(LogStatusEnum.ERROR.name());
        conversation.setAppUid(fastConversationUid);
        conversation.setAppConfig(JSONUtil.toJsonStr(new ImageReqVO()));
        conversation.setFromScene(AppSceneEnum.WEB_ADMIN.name());
        conversation.setEndUser(Long.toString(userId));
        logAppConversationMapper.insert(conversation);
        return conversation;
    }

}
