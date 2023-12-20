package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageCreativeExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageCreativeExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageDTO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeImageCreativeThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Component
@Slf4j
public class CreativeExecuteManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CreativeAppManager creativeAppManager;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private CreativeImageManager creativeImageManager;

    @Resource
    private CreativeImageCreativeThreadPoolHolder creativeImageCreativeThreadPoolHolder;

    /**
     * 批量执行文案生成任务
     *
     * @param contentList 创作内容列表
     * @param force       是否强制执行
     * @return 执行结果
     */
    public Map<Long, Boolean> executeCopyWriting(List<CreativeContentDO> contentList, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(contentList.size());

        // 排序 id 加锁
        List<Long> ids = contentList.stream().map(CreativeContentDO::getId).sorted().collect(Collectors.toList());
        StringJoiner sj = new StringJoiner("-");
        ids.forEach(id -> sj.add(id.toString()));
        String key = "creative-content-copy-writing-" + sj;
        RLock lock = redissonClient.getLock(key);

        if (lock != null && !lock.tryLock()) {
            log.warn("正在执行中，重复调用 {}", sj);
            return result;
        }
        Integer maxRetry = getMaxRetry(force);
        contentList = creativeContentMapper.selectBatchIds(ids).stream().filter(item -> item.getRetryCount() < maxRetry
                && !CreativeContentStatusEnum.EXECUTING.getCode().equals(item.getStatus())
                && CreativeContentTypeEnum.COPY_WRITING.getCode().equalsIgnoreCase(item.getType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentList)) {
            log.warn("没有可执行状态的任务，{}", sj);
            return result;
        }

        try {
            LocalDateTime start = LocalDateTime.now();
            List<XhsAppCreativeExecuteResponse> resp = new ArrayList<>();
            List<XhsAppCreativeExecuteRequest> requests = new ArrayList<>(contentList.size());
            for (CreativeContentDO content : contentList) {
                XhsAppCreativeExecuteRequest executeRequest = new XhsAppCreativeExecuteRequest();
                CreativePlanExecuteDTO executeParams = CreativeContentConvert.INSTANCE.toExecuteParams(content.getExecuteParams());
                if (executeParams == null) {
                    continue;
                }
                CreativePlanAppExecuteDTO appExecuteRequest = executeParams.getAppExecuteRequest();
                executeRequest.setPlanUid(content.getPlanUid());
                executeRequest.setSchemeUid(content.getSchemeUid());
                executeRequest.setBusinessUid(content.getBusinessUid());
                executeRequest.setContentUid(content.getUid());
                executeRequest.setUid(appExecuteRequest.getUid());
                executeRequest.setScene(appExecuteRequest.getScene());
                executeRequest.setUserId(Long.valueOf(content.getCreator()));
                executeRequest.setSchemeMode(executeParams.getSchemeMode());
                if (CreativeSchemeModeEnum.PRACTICAL_IMAGE_TEXT.name().equals(executeParams.getSchemeMode())) {
                    CreativeContentDO business = creativeContentMapper.selectByType(content.getBusinessUid(), CreativeContentTypeEnum.PICTURE.getCode());
                    Map<String, Object> params = CollectionUtil.emptyIfNull(appExecuteRequest.getParams())
                            .stream()
                            .collect(Collectors.toMap(VariableItemDTO::getField, item -> {
                                if (CreativeAppUtils.PARAGRAPH_DEMAND.equals(item.getField())) {
                                    return CreativeImageUtils.handlerParagraphDemand(business);
                                }
                                if (Objects.isNull(item.getValue())) {
                                    return Optional.ofNullable(item.getDefaultValue()).orElse(StringUtils.EMPTY);
                                }
                                return item.getValue();
                            }));
                    executeRequest.setParams(params);
                    // 干货图文：一次执行一个
                    XhsAppCreativeExecuteResponse response = creativeAppManager.creativePracticalExecute(executeRequest);
                    resp.add(response);
                } else {
                    Map<String, Object> params = CollectionUtil.emptyIfNull(appExecuteRequest.getParams())
                            .stream()
                            .collect(Collectors.toMap(VariableItemDTO::getField, item -> {
                                if (Objects.isNull(item.getValue())) {
                                    return Optional.ofNullable(item.getDefaultValue()).orElse(StringUtils.EMPTY);
                                }
                                return item.getValue();
                            }));
                    executeRequest.setParams(params);
                    requests.add(executeRequest);
                }
            }

            if (CollectionUtils.isNotEmpty(requests)) {
                List<XhsAppCreativeExecuteResponse> respRandom = creativeAppManager.bathAppCreativeExecute(requests);
                resp.addAll(respRandom);
            }

            if (CollectionUtils.isEmpty(resp)) {
                return result;
            }
            Map<String, XhsAppCreativeExecuteResponse> respMap = resp.stream().collect(Collectors.toMap(XhsAppCreativeExecuteResponse::getContentUid, Function.identity()));
            LocalDateTime end = LocalDateTime.now();
            Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            // 计算平均执行时间
            executeTime = new BigDecimal(String.valueOf(executeTime)).divide(new BigDecimal(String.valueOf(resp.size())), 2, RoundingMode.HALF_UP).longValue();
            for (CreativeContentDO contentDO : contentList) {
                XhsAppCreativeExecuteResponse executeResponse = respMap.get(contentDO.getUid());
                if (!executeResponse.getSuccess()) {
                    result.put(contentDO.getId(), false);
                    updateFailure(contentDO.getId(), start, executeResponse.getErrorMsg(), contentDO.getRetryCount(), maxRetry);
                    continue;
                }

                CopyWritingContentDTO copyWriting = executeResponse.getCopyWriting();
                if (CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT.name().equals(executeResponse.getSchemeMode())) {
                    if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                        result.put(contentDO.getId(), false);
                        updateFailure(contentDO.getId(), start, "文案内容为空", contentDO.getRetryCount(), maxRetry);
                        continue;
                    }
                } else {
                    CreativePlanExecuteDTO executeParams = CreativeContentConvert.INSTANCE.toExecuteParams(contentDO.getExecuteParams());
                    if (executeParams == null) {
                        continue;
                    }
                    CreativePlanAppExecuteDTO appExecuteRequest = executeParams.getAppExecuteRequest();
                    Map<String, Object> params = CollectionUtil.emptyIfNull(appExecuteRequest.getParams())
                            .stream()
                            .collect(Collectors.toMap(VariableItemDTO::getField, item -> {
                                if (Objects.isNull(item.getValue())) {
                                    return Optional.ofNullable(item.getDefaultValue()).orElse(StringUtils.EMPTY);
                                }
                                return item.getValue();
                            }));
                    Integer count = (Integer) params.get(CreativeAppUtils.PARAGRAPH_COUNT);
                    if (CollectionUtil.isEmpty(copyWriting.getParagraphList()) || copyWriting.getParagraphList().size() != count) {
                        result.put(contentDO.getId(), false);
                        updateFailure(contentDO.getId(), start, "文案段落数据为空或者生成格式不正确(段落数量不正确)", contentDO.getRetryCount(), maxRetry);
                        continue;
                    }
                }

                CreativeContentDO updateContent = new CreativeContentDO();
                updateContent.setId(contentDO.getId());
                // 根据模式把 干货图文的 md 格式 转换为 纯文本格式
                if (CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT.name().equals(executeResponse.getSchemeMode())) {
                    updateContent.setCopyWritingContent(copyWriting.getContent());
                    updateContent.setCopyWritingTitle(copyWriting.getTitle());
                    updateContent.setCopyWritingCount(copyWriting.getContent().length());
                } else {
                    String copyWritingContent = CreativeAppUtils.buildPracticalCopyWritingContent(copyWriting);
                    updateContent.setCopyWritingTitle(copyWriting.getTitle());
                    updateContent.setCopyWritingContent(copyWritingContent);
                    updateContent.setCopyWritingCount(copyWritingContent.length());
                }
                updateContent.setCopyWritingResult(JSONUtil.toJsonStr(copyWriting));
                updateContent.setStartTime(start);
                updateContent.setEndTime(end);
                updateContent.setExecuteTime(executeTime);
                updateContent.setStatus(CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode());
                updateContent.setUpdateTime(LocalDateTime.now());
                updateContent.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
                creativeContentMapper.updateById(updateContent);
                result.put(contentDO.getId(), true);
            }
            log.info("文案执行结束： {} ms", executeTime);
        } catch (Exception e) {
            log.error("文案生成异常", e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
        return result;
    }

    /**
     * 批量执行图片生成任务
     *
     * @param contentList 创作内容列表
     * @param force       是否强制执行
     * @return 执行结果
     */
    public Map<Long, Boolean> executePicture(List<CreativeContentDO> contentList, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(contentList.size());

        if (CollectionUtil.isEmpty(contentList)) {
            log.warn("创作中心：小红书图片生成执行：参数为空！图片生成结束");
            return Collections.emptyMap();
        }

        // 获取异步Future
        ThreadPoolExecutor executor = creativeImageCreativeThreadPoolHolder.executor();
        List<CompletableFuture<XhsImageCreativeExecuteResponse>> imageFutureList = Lists.newArrayList();
        for (CreativeContentDO content : contentList) {
            CompletableFuture<XhsImageCreativeExecuteResponse> future = CompletableFuture.supplyAsync(() -> imageExecute(content, force), executor);
            imageFutureList.add(future);
        }
        // 合并任务
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(imageFutureList.toArray(new CompletableFuture[0]));
        // 等待所有任务执行完成并且获取执行结果
        CompletableFuture<List<XhsImageCreativeExecuteResponse>> allFuture = allOfFuture
                .thenApply(v -> imageFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        // 获取执行结果
        List<XhsImageCreativeExecuteResponse> responses = allFuture.join();
        // 处理执行结果
        Map<String, CreativeContentDO> map = contentList.stream().collect(Collectors.toMap(CreativeContentDO::getUid, Function.identity()));
        for (XhsImageCreativeExecuteResponse response : responses) {
            CreativeContentDO content = map.get(response.getContentUid());
            if (Objects.isNull(content)) {
                continue;
            }
            if (response.getSuccess()) {
                result.put(content.getId(), true);
            } else {
                result.put(content.getId(), false);
            }
        }

        log.info("创作中心：小红书图片生成执行：图片生成结束");
        return result;
    }

    /**
     * 单个图片创作执行
     *
     * @param content 创作内容
     * @return 创作结果
     */
    public XhsImageCreativeExecuteResponse imageExecute(CreativeContentDO content, Boolean force) {
        // 获取锁
        String lockKey = "creative-content-image-" + content.getId();
        RLock lock = redissonClient.getLock(lockKey);
        if (lock != null && !lock.tryLock()) {
            log.warn("创作中心：生成图片正在执行中，重复调用(内容ID：{})！", content.getId());
            return failure(content, 350600110, formatErrorMsg("创作中心：生成图片正在执行中，重复调用(内容ID：%s)！", content.getId()), null);
        }

        try {
            // 开始执行时间
            LocalDateTime start = LocalDateTime.now();
            log.info("创作中心：生成图片正在执行开始：{}，{}", content.getId(), start);
            // 最大重试次数
            Integer maxRetry = getMaxRetry(force);
            // 获取文案执行结果
            CopyWritingContentDTO copyWriting = getCopyWritingContent(content, start, maxRetry);
            // 获取最新的创作内容并且校验
            CreativeContentDO latestContent = getImageContent(content.getId(), start, maxRetry, force);
            try {
                // 构建请求
                XhsImageCreativeExecuteRequest request = new XhsImageCreativeExecuteRequest();
                request.setContentUid(latestContent.getUid());
                request.setPlanUid(latestContent.getPlanUid());
                request.setSchemeUid(latestContent.getSchemeUid());
                request.setBusinessUid(latestContent.getBusinessUid());
                // 图片执行参数需要抽象。
                request.setImageStyleRequest(CreativeImageUtils.getImageStyleExecuteRequest(latestContent, copyWriting, force));
                // 执行请求
                XhsImageCreativeExecuteResponse response = creativeImageManager.creativeExecute(request);

                // 校验结果
                if (Objects.isNull(response)) {
                    throw exception(350600115, "创作中心：图片生成结果为空(ID: %s)！", latestContent.getUid());
                }
                // 是否成功
                if (!response.getSuccess()) {
                    Integer errorCode = Objects.isNull(response.getErrorCode()) ? 350600119 : response.getErrorCode();
                    String message = StringUtils.isBlank(response.getErrorMessage()) ? "创作中心：图片生成失败！" : response.getErrorMessage();
                    throw exception(errorCode, message);
                }
                // 图片生成结果
                List<XhsImageExecuteResponse> imageResponseList = CollectionUtil.emptyIfNull(response.getImageStyleResponse().getImageResponses());
                if (CollectionUtils.isEmpty(imageResponseList)) {
                    throw exception(350600120, "创作中心：图片生成结果为空(ID: %s)！", latestContent.getUid());
                }

                // 更新创作内容
                LocalDateTime end = LocalDateTime.now();
                long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                executeTime = new BigDecimal(String.valueOf(executeTime)).divide(new BigDecimal(String.valueOf(imageResponseList.size())), 2, RoundingMode.HALF_UP).longValue();
                List<CreativeImageDTO> pictureContent = CreativeContentConvert.INSTANCE.convert2(imageResponseList);

                CreativeContentDO updateContent = new CreativeContentDO();
                updateContent.setId(latestContent.getId());
                updateContent.setPictureContent(JSONUtil.toJsonStr(pictureContent));
                updateContent.setPictureNum(pictureContent.size());
                updateContent.setStartTime(start);
                updateContent.setEndTime(end);
                updateContent.setExecuteTime(executeTime);
                updateContent.setStatus(CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode());
                updateContent.setUpdateTime(end);
                updateContent.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
                creativeContentMapper.updateById(updateContent);

                log.info("创作中心：图片执行成功：ID：{}, 耗时： {} ms", latestContent.getId(), executeTime);
                return response;
            } catch (Exception e) {
                updateFailure(latestContent.getId(), start, e.getMessage(), latestContent.getRetryCount(), maxRetry);
                throw e;
            }
        } catch (ServiceException exception) {
            log.error("创作中心：图片执行失败：错误码: {}, 错误信息: {}", exception.getCode(), exception.getMessage(), exception);
            return failure(content, exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.error("创作中心：图片执行失败： 错误信息: {}", exception.getMessage(), exception);
            return failure(content, 350600130, exception.getMessage(), null);
        } finally {
            if (lock != null) {
                lock.unlock();
                log.info("解锁成功：{}", lockKey);
            }
        }
    }

    /**
     * 获取文案执行结果
     *
     * @param content  图片创作内容
     * @param start    开始时间
     * @param maxRetry 最大重试次数
     * @return 文案执行结果
     */
    @NotNull
    private CopyWritingContentDTO getCopyWritingContent(CreativeContentDO content, LocalDateTime start, Integer maxRetry) {
        // 查询文案执行情况。需要文案执行成功才能执行图片
        CreativeContentDO business = creativeContentMapper.selectByType(content.getBusinessUid(), CreativeContentTypeEnum.COPY_WRITING.getCode());
        if (Objects.isNull(business)) {
            // 此时说明数据存在问题，直接更新为最终失败
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案任务不存在，不能执行图片生成(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600141, "创作中心：文案任务不存在，不能执行图片生成(ID: %s)！", content.getUid());
        }
        if (CreativeContentStatusEnum.INIT.getCode().equals(business.getStatus())) {
            // 文案未执行，直接跳出，不执行图片生成，等待下次执行
            throw exception(350600142, "创作中心：文案任务初始化中，不能执行图片生成(ID: %s)！", content.getUid());

        } else if (CreativeContentStatusEnum.EXECUTING.getCode().equals(business.getStatus())) {
            // 文案执行中，直接跳出，不执行图片生成，等待下次执行
            throw exception(350600143, "创作中心：文案任务执行中，不能执行图片生成(ID: %s)！", content.getUid());

        } else if (CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode().equals(business.getStatus())) {
            // 文案最终执行失败，图片更新为最终失败
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案任务执行失败，不能执行图片生成(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600144, "创作中心：文案任务执行失败，不能执行图片生成(ID: %s)！", content.getUid());

        } else if (CreativeContentStatusEnum.EXECUTE_ERROR.getCode().equals(business.getStatus())) {
            // 文案执行失败
            if (business.getRetryCount() >= maxRetry) {
                updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案任务执行失败，不能执行图片生成(ID: %s)！", content.getUid()), maxRetry);
            }
            throw exception(350600145, "创作中心：文案任务执行失败，不能执行图片生成(ID: %s)！", content.getUid());

        } else if (CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(business.getStatus())) {
            // 文案执行成功，但是文案执行结果为空，说明数据存在问题，直接更新为最终失败
            if (StringUtils.isBlank(business.getCopyWritingResult())) {
                updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案任务执行结果不存在，不能执行图片生成(ID: %s)！", content.getUid()), maxRetry);
                throw exception(350600146, "创作中心：文案任务执行结果不存在，不能执行图片生成(ID: %s)！", content.getUid());
            }
        } else {
            // 文案状态异常。直接更新为最终失败
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案任务执行状态异常，不能执行图片生成(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600147, "创作中心：文案任务执行状态异常，不能执行图片生成(ID: %s)！", content.getUid());
        }

        // 文案执行结果
        CopyWritingContentDTO copyWriting = JSONUtil.toBean(business.getCopyWritingResult(), CopyWritingContentDTO.class);
        if (Objects.isNull(copyWriting)) {
            // 文案执行结果为空，说明数据存在问题，直接更新为最终失败
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：文案执行结果为空，执行文案不存在(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600148, "创作中心：文案执行结果为空，执行文案不存在(ID: %s)！", content.getUid());
        }
        return copyWriting;
    }

    /**
     * 获取图片执行任务
     *
     * @param id    任务ID
     * @param force 是否强制执行
     * @return 图片执行任务
     */
    private CreativeContentDO getImageContent(Long id, LocalDateTime start, Integer maxRetry, Boolean force) {
        CreativeContentDO content = creativeContentMapper.selectById(id);
        if (Objects.isNull(content)) {
            throw exception(350600211, "未找到对应的创作任务(ID: %s)！", id);
        }

        if (!CreativeContentTypeEnum.PICTURE.getCode().equalsIgnoreCase(content.getType())) {
            throw exception(350600212, "创作任务类型不是图片(ID: %s)！", id);
        }

        if (CreativeContentStatusEnum.EXECUTING.getCode().equals(content.getStatus())) {
            throw exception(350600213, "创作任务正在执行(ID: %s)！", id);
        }

        if (!force) {
            if (CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(content.getStatus())) {
                throw exception(350600214, "创作任务已经执行成功(ID: %s)！", id);
            }
            if (CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode().equals(content.getStatus()) || content.getRetryCount() >= maxRetry) {
                throw exception(350600215, "创作任务: %s，重试次数：%s，最多重试次数：%s ！", id, content.getRetryCount(), maxRetry);
            }
        }

        // 获取并且校验使用图片
        List<String> useImageList = JSONUtil.parseArray(content.getUsePicture()).toList(String.class);
        if (CollectionUtils.isEmpty(useImageList)) {
            // 可用图片为空，说明该任务数据存在问题。需要更新状态
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：图片执行可使用图片为空，请联系管理员(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600113, "创作中心：图片执行可使用图片为空，请联系管理员(ID: %s)！", content.getUid());
        }
        // 获取并且校验执行参数
        CreativePlanExecuteDTO executeParams = CreativeContentConvert.INSTANCE.toExecuteParams(content.getExecuteParams());
        if (Objects.isNull(executeParams) || Objects.isNull(executeParams.getImageStyleExecuteRequest())) {
            // 执行参数为空，说明该任务数据存在问题。需要更新状态
            updateFailureFinished(content.getId(), start, formatErrorMsg("创作中心：图片执行参数不存在，请联系管理员(ID: %s)！", content.getUid()), maxRetry);
            throw exception(350600114, "创作中心：图片执行参数不存在，请联系管理员(ID: %s)！", content.getUid());
        }
        return content;
    }

    /**
     * 执行失败，更新创作内容
     *
     * @param id       创作内容ID
     * @param start    开始时间
     * @param errorMsg 错误信息
     * @param retry    重试次数
     * @param maxRetry 最大重试次数
     */
    private void updateFailure(Long id, LocalDateTime start, String errorMsg, Integer retry, Integer maxRetry) {
        // 重试次数大于阈值，更新为最终失败
        if (retry >= (maxRetry - 1)) {
            updateFailureFinished(id, start, errorMsg, maxRetry);
            return;
        }
        CreativeContentDO content = new CreativeContentDO();
        content.setId(id);
        content.setErrorMsg(errorMsg);
        content.setRetryCount(retry + 1);
        content.setStatus(CreativeContentStatusEnum.EXECUTE_ERROR.getCode());
        content.setStartTime(start);
        LocalDateTime end = LocalDateTime.now();
        content.setEndTime(end);
        Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        content.setExecuteTime(executeTime);
        content.setUpdateTime(end);
        content.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        creativeContentMapper.updateById(content);
    }

    /**
     * 执行失败，更新创作内容, 更新为最终失败
     *
     * @param id       创作内容ID
     * @param start    开始时间
     * @param errorMsg 错误信息
     */
    private void updateFailureFinished(Long id, LocalDateTime start, String errorMsg, Integer maxRetry) {
        CreativeContentDO content = new CreativeContentDO();
        content.setId(id);
        content.setErrorMsg(errorMsg);
        content.setRetryCount(maxRetry);
        content.setStatus(CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode());
        content.setStartTime(start);
        LocalDateTime end = LocalDateTime.now();
        content.setEndTime(end);
        Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        content.setExecuteTime(executeTime);
        content.setUpdateTime(end);
        content.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        creativeContentMapper.updateById(content);
    }

    /**
     * 获取最大重试次数
     *
     * @param force 是否强制执行
     * @return 最大失败次数
     */
    private Integer getMaxRetry(Boolean force) {
        if (BooleanUtils.isTrue(force)) {
            return Integer.MAX_VALUE;
        }
        try {
            DictDataDO dictDataDO = dictDataService.parseDictData("xhs", "max_retry");
            if (dictDataDO == null || dictDataDO.getValue() == null) {
                return 3;
            } else {
                return Integer.valueOf(dictDataDO.getValue());
            }
        } catch (Exception exception) {
            return 3;
        }
    }

    /**
     * 执行失败，更新创作内容
     *
     * @param code    错误码
     * @param message 错误信息
     * @param args    参数
     * @return {@link ServiceException}
     */
    private static ServiceException exception(Integer code, String message, Object... args) {
        return ServiceExceptionUtil.exception(ofError(code, message, args));
    }

    /**
     * 执行失败，更新创作内容
     *
     * @param code    错误码
     * @param message 错误信息
     * @param args    参数
     * @return {@link ErrorCode}
     */
    private static ErrorCode ofError(Integer code, String message, Object... args) {
        return new ErrorCode(code, formatErrorMsg(message, args));
    }

    /**
     * 格式化错误信息
     *
     * @param message 错误信息
     * @param args    参数
     * @return 格式化后的错误信息
     */
    private static String formatErrorMsg(String message, Object... args) {
        return String.format(message, args);
    }

    /**
     * 失败响应
     *
     * @param content            请求
     * @param errorCode          错误码
     * @param errorMessage       错误信息
     * @param imageStyleResponse 图片风格响应参数
     * @return 失败响应
     */
    public static XhsImageCreativeExecuteResponse failure(CreativeContentDO content, Integer errorCode, String errorMessage, XhsImageStyleExecuteResponse imageStyleResponse) {
        XhsImageCreativeExecuteResponse response = new XhsImageCreativeExecuteResponse();
        if (Objects.nonNull(content)) {
            response.setPlanUid(content.getPlanUid());
            response.setSchemeUid(content.getSchemeUid());
            response.setBusinessUid(content.getBusinessUid());
            response.setContentUid(content.getUid());
        }
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setErrorCode(errorCode);
        response.setImageStyleResponse(imageStyleResponse);
        return response;
    }
}
