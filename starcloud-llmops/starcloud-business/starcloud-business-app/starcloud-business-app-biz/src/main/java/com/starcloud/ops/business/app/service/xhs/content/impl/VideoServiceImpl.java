package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.VideoGenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.VideoResult;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.VideoGeneratorClient;
import com.starcloud.ops.business.app.feign.dto.PosterImageParam;
import com.starcloud.ops.business.app.feign.dto.video.*;
import com.starcloud.ops.business.app.feign.response.VideoGeneratorResponse;
import com.starcloud.ops.business.app.model.content.*;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.service.xhs.content.VideoService;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.VIDEO_ERROR;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_BEAN_NOT_ENOUGH;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoGeneratorClient videoGeneratorClient;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private LogAppMessageService logAppMessageService;

    @Resource
    private LogAppConversationService conversationService;

    @Resource
    private CreativeThreadPoolHolder creativeThreadPoolHolder;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AdminUserRightsApi adminUserRightsApi;

    private static final String VIDEO_PREFIX = "video-";
    private static final String VIDEO_RUNNING = "video-running-";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    public VideoResult generateResult(String creativeContentUid) {
        Object running = redissonClient.getBucket(VIDEO_RUNNING + creativeContentUid).get();
        if (Objects.nonNull(running) && BooleanUtils.isTrue((Boolean) running)) {
            return new VideoResult();
        }
        CreativeContentDO creativeContent = creativeContentMapper.get(creativeContentUid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", creativeContentUid);
        CreativeContentRespVO contentRespVO = CreativeContentConvert.INSTANCE.convert(creativeContent);

        VideoResult videoResult = new VideoResult();
        videoResult.setFinished(true);
        videoResult.setVideo(contentRespVO.getExecuteResult().getVideo());
        return videoResult;
    }

    @Override
    public void generateVideo(VideoGenerateReqVO reqVO) {
        CreativeContentDO creativeContent = creativeContentMapper.get(reqVO.getCreativeContentUid());
        AppValidate.notNull(creativeContent, "创作内容不存在({})", reqVO.getCreativeContentUid());
        CreativeContentRespVO contentRespVO = CreativeContentConvert.INSTANCE.convert(creativeContent);

        if (!adminUserRightsApi.calculateUserRightsEnough(WebFrameworkUtils.getLoginUserId(), AdminUserRightsTypeEnum.MATRIX_BEAN, null)) {
            throw exception(USER_RIGHTS_BEAN_NOT_ENOUGH);
        }

        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        redissonClient.getBucket(VIDEO_RUNNING + reqVO.getCreativeContentUid()).set(true, 5, TimeUnit.MINUTES);
        executorService.execute(() -> {
            log.info("开始生成视频，creativeContentUid={}", reqVO.getCreativeContentUid());
            String conversationUid = null;
            RLock lock = redissonClient.getLock(VIDEO_PREFIX + reqVO.getCreativeContentUid());

            try {
                UserContextHolder.setUserId(loginUserId);
                if (!lock.tryLock(0, 5, TimeUnit.MINUTES)) {
                    log.info("视频正在生成中，creativeContentUid={}", reqVO.getCreativeContentUid());
                    return;
                }
                conversationUid = Optional.ofNullable(contentRespVO.getExecuteResult())
                        .map(CreativeContentExecuteResult::getVideo)
                        .map(VideoContentInfo::getConversationUid)
                        .orElseGet(() -> logConversationCreate(contentRespVO, LogStatusEnum.ERROR, null, null));

                long start = System.currentTimeMillis();
                VideoContentInfo videoContentInfo = generate(contentRespVO, reqVO);
                videoContentInfo.setConversationUid(conversationUid);
                long end = System.currentTimeMillis();
                int costPoint = 1;

                if (videoContentInfo.getMerged()) {
                    logAppMessage(contentRespVO, conversationUid, LogStatusEnum.SUCCESS, null, videoContentInfo, costPoint, end - start);
                    logConversationUpdate(conversationUid, LogStatusEnum.SUCCESS, null, null);
                    reduceRights(loginUserId, creativeContent.getConversationUid(), costPoint);
                } else {
                    logAppMessage(contentRespVO, conversationUid, LogStatusEnum.ERROR, videoContentInfo.getMergeMsg(), videoContentInfo, 0, end - start);
                    logConversationUpdate(conversationUid, LogStatusEnum.ERROR, null, videoContentInfo.getMergeMsg());
                }
                updateVideo(creativeContent.getUid(), contentRespVO, videoContentInfo, reqVO.getQuickConfiguration());
            } catch (Exception e) {
                log.info("video Exception,creativeContentUid={}", reqVO.getCreativeContentUid(), e);
                logConversationUpdate(conversationUid, LogStatusEnum.ERROR, null, ExceptionUtil.stackTraceToString(e, 1000));

                VideoContentInfo videoContentInfo = new VideoContentInfo();
                videoContentInfo.setConversationUid(conversationUid);
                videoContentInfo.setMerged(false);
                videoContentInfo.setMergeMsg(e.getMessage());
                updateVideo(reqVO.getCreativeContentUid(), contentRespVO, videoContentInfo, reqVO.getQuickConfiguration());
            } finally {
                redissonClient.getBucket(VIDEO_RUNNING + reqVO.getCreativeContentUid()).delete();
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                UserContextHolder.clear();
            }
        });
    }

    private VideoContentInfo generate(CreativeContentRespVO contentRespVO, VideoGenerateReqVO reqVO) {
        AppMarketRespVO appInformation = contentRespVO.getExecuteParam().getAppInformation();
        WorkflowStepWrapperRespVO posterStepWrapper = CreativeUtils.getPosterStepWrapper(appInformation);
        PosterStyleDTO posterStyleDTO = CreativeUtils.getPosterStyleByStepWrapper(posterStepWrapper);
        Map<String, PosterTemplateDTO> templateCodeMap = posterStyleDTO.getTemplateList().stream().collect(Collectors.toMap(PosterTemplateDTO::getCode, Function.identity(), (a, b) -> a));
        List<ImageContent> imageList = contentRespVO.getExecuteResult().getImageList();

        Map<String, VideoContent> videoContentMap = Optional.ofNullable(contentRespVO.getExecuteResult().getVideo())
                .map(VideoContentInfo::getVideoList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(video -> Objects.equals("completed", video.getStatus()))
                .collect(Collectors.toMap(VideoContent::getImageUrl, Function.identity(), (a, b) -> a));

        Map<String, Future<VideoContent>> generatorFuture = new HashMap<>(imageList.size());
        ThreadPoolExecutor threadPoolExecutor = creativeThreadPoolHolder.executor();
        for (ImageContent imageContent : imageList) {
            PosterTemplateDTO posterTemplateDTO = templateCodeMap.get(imageContent.getCode());
            if (BooleanUtils.isNotTrue(posterTemplateDTO.getOpenVideoMode())) {
                continue;
            }

            if (BooleanUtils.isTrue(reqVO.getRetry()) && videoContentMap.containsKey(imageContent.getUrl())) {
                // 失败重试 跳过已生成的单条视频
                continue;
            }

            VideoGeneratorConfig videoConfig = buildVideoConfig(posterTemplateDTO, reqVO.getQuickConfiguration(), imageContent);

            Future<VideoContent> future = threadPoolExecutor.submit(() -> {
                try {
                    return generator(videoConfig, imageContent.getCode(), imageContent.getUrl());
                } catch (Exception e) {
                    log.info("generator error, creativeContentUid={}, image={}", reqVO.getCreativeContentUid(), imageContent.getUrl(), e);
                    return VideoContent.fail(imageContent.getCode(), imageContent.getUrl(), e.getMessage());
                }
            });
            generatorFuture.put(imageContent.getUrl(), future);
        }
        List<VideoMergeConfig.Videos> videos = new ArrayList<>(generatorFuture.size());
        List<VideoContent> videoContentList = new ArrayList<>(generatorFuture.size());
        boolean allSuccess = true;
        for (ImageContent imageContent : imageList) {
            Future<VideoContent> videoContentFuture = generatorFuture.get(imageContent.getUrl());
            if (BooleanUtils.isTrue(reqVO.getRetry()) && videoContentMap.containsKey(imageContent.getUrl())) {
                VideoContent videoContent = videoContentMap.get(imageContent.getUrl());
                VideoMergeConfig.Videos video = new VideoMergeConfig.Videos();
                video.setUrl(videoContent.getVideoUrl());
                videos.add(video);
                videoContentList.add(videoContent);
                continue;
            }

            if (Objects.isNull(videoContentFuture)) {
                continue;
            }
            try {
                VideoContent videoContent = videoContentFuture.get();
                videoContentList.add(videoContent);
                VideoMergeConfig.Videos video = new VideoMergeConfig.Videos();
                if (Objects.equals(videoContent.getStatus(), "failed")
                        || Objects.equals(videoContent.getStatus(), "unknown")) {
                    allSuccess = false;
                } else {
                    video.setUrl(videoContent.getVideoUrl());
                    videos.add(video);
                }
            } catch (Exception e) {
                log.info("get future error", e);
                allSuccess = false;
                videoContentList.add(VideoContent.fail(imageContent.getCode(), imageContent.getUrl(), e.getMessage()));
            }
        }

        VideoContentInfo videoContentInfo = new VideoContentInfo();
        videoContentInfo.setVideoList(videoContentList);
        if (!allSuccess || videos.isEmpty()) {
            // 未成功 不合并
            videoContentInfo.setMergeMsg("单个图片生成视频未全部成功");
            videoContentInfo.setMerged(false);
            return videoContentInfo;
        }

        if (videoContentList.size() == 1) {
            videoContentInfo.setCompleteVideoUrl(videoContentList.get(0).getVideoUrl());
            videoContentInfo.setCompleteAudioUrl(videoContentList.get(0).getAudioUrl());
            videoContentInfo.setMerged(true);
            return videoContentInfo;
        }

        VideoMergeConfig videoMergeConfig = new VideoMergeConfig();
        videoMergeConfig.setVideos(videos);
        log.info("request video merge, {}", JSONUtil.toJsonPrettyStr(videoMergeConfig));
        VideoGeneratorResponse<VideoMergeResult> merged = videoGeneratorClient.mergeVideos(videoMergeConfig);
        log.info("video merged, {}", JSONUtil.toJsonPrettyStr(merged));
        if (!Objects.equals(merged.getCode(), 0)) {
            // 合并失败
            videoContentInfo.setMergeMsg(merged.getMsg());
            videoContentInfo.setMerged(false);
        } else {
            videoContentInfo.setCompleteAudioUrl(merged.getData().getAudio_url());
            videoContentInfo.setCompleteVideoUrl(merged.getData().getUrl());
            videoContentInfo.setMerged(true);
        }
        return videoContentInfo;
    }

    private void updateVideo(String creativeContentUid, CreativeContentRespVO contentRespVO, VideoContentInfo videoContentInfo, String quickConfig) {
        CreativeContentExecuteParam executeParam = contentRespVO.getExecuteParam();
        executeParam.setQuickConfiguration(quickConfig);
        CreativeContentExecuteResult executeResult = contentRespVO.getExecuteResult();
        executeResult.setVideo(videoContentInfo);
        CreativeContentDO updateContent = new CreativeContentDO();
        updateContent.setUid(creativeContentUid);
        updateContent.setExecuteParam(JsonUtils.toJsonString(executeParam));
        updateContent.setExecuteResult(JsonUtils.toJsonString(executeResult));

        creativeContentMapper.updateByUid(updateContent);
    }

    private VideoGeneratorConfig buildVideoConfig(PosterTemplateDTO posterTemplateDTO, String quickConfigurationJson, ImageContent imageContent) {
        VideoGeneratorConfig videoConfig = JSONUtil.toBean(posterTemplateDTO.getVideoConfig(), VideoGeneratorConfig.class);

        if (Objects.isNull(videoConfig.getGlobalSettings())) {
            videoConfig.setGlobalSettings(new VideoGeneratorConfig.GlobalSettings());
        }

        if (JSONUtil.isTypeJSONObject(quickConfigurationJson)) {
            JSONObject quickConfiguration = JSONObject.parseObject(quickConfigurationJson);
            for (Map.Entry<String, Object> entry : quickConfiguration.entrySet()) {
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                BeanPath beanPath = new BeanPath("globalSettings." + entry.getKey());
                try {
                    beanPath.set(videoConfig, entry.getValue());
                } catch (Exception ignored) {
                    log.warn("字段不存在 {}", entry.getKey());
                }
            }
        }

        Map<String, String> resources = new HashMap<>();
        for (Map.Entry<String, PosterImageParam> entry : imageContent.getFinalParams().entrySet()) {
            if (Objects.nonNull(entry.getValue()) && StringUtils.isNoneBlank(entry.getValue().getText())) {
                resources.put(entry.getKey(), entry.getValue().getText());
            }
        }
        if (Objects.isNull(videoConfig.getGlobalSettings().getBackground())) {
            videoConfig.getGlobalSettings().setBackground(new VideoGeneratorConfig.Background());
        }
        videoConfig.getGlobalSettings().getBackground().setSource(imageContent.getUrl());
        videoConfig.setResources(resources);
        videoConfig.setId(null);
        return videoConfig;
    }

    private VideoContent generator(VideoGeneratorConfig videoConfig, String templateCode, String imageUrl) throws InterruptedException {
        log.info("request video generator, {}", JSONUtil.toJsonPrettyStr(videoConfig));
        VideoGeneratorResponse<VideoGeneratorResult> generatorResponse = videoGeneratorClient.videoGenerator(videoConfig);
        if (!Objects.equals(generatorResponse.getCode(), 0)) {
            return VideoContent.fail(templateCode, imageUrl, generatorResponse.getMsg());
        }
        String taskId = generatorResponse.getData().getTaskId();
        // 100 * 3 s
        int loop = 100;
        while (loop > 0) {
            loop--;
            TimeUnit.SECONDS.sleep(3);
            VideoGeneratorResponse<VideoRecordResult> generatorResult = videoGeneratorClient.getGeneratorResult(taskId);
            log.info("request video generator result,loop={} {}", loop, JSONUtil.toJsonPrettyStr(generatorResult));
            if (!Objects.equals(generatorResult.getCode(), 0)) {
                return VideoContent.fail(templateCode, imageUrl, generatorResult.getMsg());
            }

            if (Objects.equals(generatorResult.getData().getStatus(), "pending")
                    || Objects.equals(generatorResult.getData().getStatus(), "processing")) {
                continue;
            }

            VideoRecordResult data = generatorResult.getData();
            VideoContent content = new VideoContent();
            content.setVideoUid(taskId);
            content.setVideoUrl(data.getUrl());
            content.setProgress(data.getProgress());
            content.setStage(data.getStage());
            content.setStatus(data.getStatus());
            content.setError(data.getError());
            content.setCode(templateCode);
            content.setImageUrl(imageUrl);
            content.setAudioUrl(data.getAudioUrl());
            return content;
        }
        throw exception(VIDEO_ERROR, "time out");
    }

    private void reduceRights(Long userId, String conversationUid, int costPoint) {
        ReduceRightsDTO reduceRights = new ReduceRightsDTO();
        reduceRights.setUserId(userId);
        reduceRights.setTeamOwnerId(null);
        reduceRights.setTeamId(null);
        reduceRights.setRightType(AdminUserRightsTypeEnum.MATRIX_BEAN.getType());
        reduceRights.setReduceNums(costPoint);
        reduceRights.setBizType(UserRightSceneUtils.getUserRightsBizType(AppSceneEnum.XHS_VIDEO.name()).getType());
        reduceRights.setBizId(conversationUid);
        adminUserRightsApi.reduceRights(reduceRights);
    }

    private String logConversationCreate(CreativeContentRespVO contentRespVO, LogStatusEnum statusEnum, String errorCode, String errorMsg) {
        AppMarketRespVO appInformation = contentRespVO.getExecuteParam().getAppInformation();
        LogAppConversationCreateReqVO createRequest = new LogAppConversationCreateReqVO();
        createRequest.setUid(IdUtil.fastSimpleUUID());
        createRequest.setAppUid(appInformation.getUid());
        createRequest.setAppName(appInformation.getName());
        createRequest.setAppMode(AppModelEnum.VIDEO.name());
        createRequest.setAppConfig(JSONUtil.toJsonStr(appInformation.getWorkflowConfig()));
        createRequest.setStatus(statusEnum.name());
        createRequest.setErrorCode(errorCode);
        createRequest.setErrorMsg(errorMsg);
        createRequest.setFromScene(AppSceneEnum.XHS_VIDEO.name());
        conversationService.createAppLogConversation(createRequest);
        return createRequest.getUid();
    }

    private void logConversationUpdate(String conversationUid, LogStatusEnum statusEnum, String errorCode, String errorMsg) {
        LogAppConversationStatusReqVO updateRequest = new LogAppConversationStatusReqVO();
        updateRequest.setUid(conversationUid);
        updateRequest.setStatus(statusEnum.name());
        updateRequest.setErrorCode(errorCode);
        updateRequest.setErrorMsg(errorMsg);
        conversationService.updateAppLogConversationStatus(updateRequest);
    }

    public void logAppMessage(CreativeContentRespVO contentRespVO, String conversationUid, LogStatusEnum statusEnum, String msg, VideoContentInfo videoContentInfo, int costPoint, long elapsed) {
        AppMarketRespVO appInformation = contentRespVO.getExecuteParam().getAppInformation();
        LogAppMessageCreateReqVO logMessage = new LogAppMessageCreateReqVO();
        logMessage.setUid(IdUtil.fastSimpleUUID());
        logMessage.setAppConversationUid(conversationUid);
        logMessage.setAppUid(appInformation.getUid());
        logMessage.setAppMode(AppModelEnum.VIDEO.name());
        logMessage.setAppStep("视频生成");
        logMessage.setFromScene(AppSceneEnum.XHS_VIDEO.name());
        logMessage.setAppConfig("{}");
        logMessage.setMessageTokens(0);
        logMessage.setMessageUnitPrice(BigDecimal.ZERO);
        logMessage.setAnswer(JSONUtil.toJsonStr(videoContentInfo));
        logMessage.setAnswerTokens(0);
        logMessage.setAnswerUnitPrice(BigDecimal.ZERO);
        logMessage.setTotalPrice(BigDecimal.ZERO);
        logMessage.setCostPoints(costPoint);
        logMessage.setElapsed(elapsed);
        logMessage.setStatus(statusEnum.name());
        logMessage.setErrorMsg(msg);
        logAppMessageService.createAppLogMessage(logMessage);
    }


}
