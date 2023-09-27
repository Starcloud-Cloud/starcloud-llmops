package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.image.vo.query.HistoryGenerateImagePageQuery;
import com.starcloud.ops.business.app.api.image.vo.request.GenerateImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.GenerateImageResponse;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageHandlerHolder;
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Collections;
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
    private LogAppMessageMapper logAppMessageMapper;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private ImageHandlerHolder imageHandlerHolder;

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
    public PageResult<GenerateImageResponse> historyGenerateImages(HistoryGenerateImagePageQuery query) {
        // 查询日志消息记录
        Page<LogAppMessageDO> page = pageHistoryGenerateImageMessage(query);
        List<LogAppMessageDO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new PageResult<>(Collections.emptyList(), page.getTotal());
        }
        // 处理图片消息数据
        List<GenerateImageResponse> list = records.stream().map(ImageServiceImpl::buildHistoryResponse).filter(Objects::nonNull).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
    }

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    @Override
    public UploadImageInfoDTO upload(MultipartFile image) {
        log.info("开始上传图片，ContentType: {}, imageName: {}", image.getContentType(), image.getOriginalFilename());
        return ImageUploadUtils.uploadImage(image, ImageUploadUtils.UPLOAD);
    }

    /**
     * 文本生成图片
     *
     * @param request 请求参数
     * @return 图片信息
     */
    @SuppressWarnings("all")
    @Override
    public ImageRespVO execute(ImageReqVO request) {
        // 获取SSE
        request.setSseEmitter(new SseEmitter(60000L));
        // 获取图片处理器
        BaseImageHandler handler = imageHandlerHolder.getHandler(request.getScene());
        request.setImageHandler(handler);
        // 初始化请求
        handler.handleRequest(request.getImageRequest());
        // 构建 ImageAppEntity
        ImageAppEntity factory = AppFactory.factory(request);
        // 生成图片
        return factory.execute(request);
    }

    /**
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    private Page<LogAppMessageDO> pageHistoryGenerateImageMessage(HistoryGenerateImagePageQuery query) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        LambdaQueryWrapper<LogAppMessageDO> messageWrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        messageWrapper.select(LogAppMessageDO::getUid, LogAppMessageDO::getCreateTime, LogAppMessageDO::getMessage, LogAppMessageDO::getAnswer, LogAppMessageDO::getAppConfig, LogAppMessageDO::getVariables);
        messageWrapper.eq(LogAppMessageDO::getAppMode, AppModelEnum.IMAGE.name());
        messageWrapper.eq(LogAppMessageDO::getFromScene, AppSceneEnum.WEB_IMAGE.name());
        messageWrapper.eq(LogAppMessageDO::getCreator, Long.toString(loginUserId));
        messageWrapper.eq(LogAppMessageDO::getStatus, LogStatusEnum.SUCCESS.name());
        messageWrapper.eq(LogAppMessageDO::getDeleted, Boolean.FALSE);
        messageWrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        return logAppMessageMapper.selectPage(PageUtil.page(query), messageWrapper);
    }

    /**
     * 构建历史图片响应
     *
     * @param message 日志消息
     * @return 历史图片响应
     */
    private static GenerateImageResponse buildHistoryResponse(LogAppMessageDO message) {
        // 如果没有结果，返回 null
        if (StringUtils.isBlank(message.getAnswer())) {
            return null;
        }
        // 新的数据结构
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getAnswer(), GenerateImageResponse.class);
        } catch (Exception exception) {
            log.error("新结构数据序列化失败：尝试使用旧结构序列化图片日志消息数据：{}", exception.getMessage());
            try {
                List<ImageDTO> imageList = JSONUtil.toBean(message.getAnswer(), new TypeReference<List<ImageDTO>>() {
                }, true);
                // 排除掉空的和没有 url 的图片
                imageList = imageList.stream().filter(Objects::nonNull).filter(imageItem -> StringUtils.isNotBlank(imageItem.getUrl())).collect(Collectors.toList());
                // 如果没有结果，返回 null
                if (CollectionUtil.isEmpty(imageList)) {
                    return null;
                }
                GenerateImageResponse imageResponse = new GenerateImageResponse();
                imageResponse.setPrompt(message.getMessage());
                imageResponse.setImages(imageList);
                GenerateImageRequest imageRequest = JSONUtil.toBean(message.getVariables(), GenerateImageRequest.class);
                if (Objects.nonNull(imageRequest)) {
                    imageResponse.setNegativePrompt(ImageUtils.handleNegativePrompt(imageRequest.getNegativePrompt(), Boolean.FALSE));
                    imageResponse.setEngine(imageRequest.getEngine());
                    imageResponse.setWidth(imageRequest.getWidth());
                    imageResponse.setHeight(imageRequest.getHeight());
                    imageResponse.setSteps(imageRequest.getSteps());
                    imageResponse.setStylePreset(imageRequest.getStylePreset());
                }
                return imageResponse;
            } catch (Exception exception1) {
                log.error("序列化图片日志消息响应失败: {}", exception1.getMessage());
                return null;
            }
        }
    }

}
