package com.starcloud.ops.business.app.service.xhs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativePictureContentDTO;
import com.starcloud.ops.business.app.convert.xhs.XhsCreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsCreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @author admin
 */
@Component
@Slf4j
public class XhsCreativeExecuteManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private XhsService xhsService;

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private XhsImageCreativeThreadPoolHolder xhsImageCreativeThreadPoolHolder;


    public Map<Long, Boolean> executeCopyWriting(List<XhsCreativeContentDO> xhsCreativeContentDOList, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(xhsCreativeContentDOList.size());

        // 排序 id 加锁
        List<Long> ids = xhsCreativeContentDOList.stream().map(XhsCreativeContentDO::getId).sorted().collect(Collectors.toList());
        StringJoiner sj = new StringJoiner("-");
        ids.forEach(id -> sj.add(id.toString()));
        String key = "xhs-pic-" + sj;
        RLock lock = redissonClient.getLock(key);

        if (lock != null && !lock.tryLock()) {
            log.warn("正在执行中，重复调用 {}", sj);
            return result;
        }
        Integer maxRetry = getMaxRetry(force);
        xhsCreativeContentDOList = creativeContentMapper.selectBatchIds(ids).stream().filter(xhsCreativeContentDO -> xhsCreativeContentDO.getRetryCount() < maxRetry
                && !XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(xhsCreativeContentDO.getStatus())
                && XhsCreativeContentTypeEnums.COPY_WRITING.getCode().equalsIgnoreCase(xhsCreativeContentDO.getType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(xhsCreativeContentDOList)) {
            log.warn("没有可执行状态的任务，{}", sj);
            return result;
        }

        try {
            List<XhsAppCreativeExecuteRequest> requests = new ArrayList<>(xhsCreativeContentDOList.size());
            for (XhsCreativeContentDO contentDO : xhsCreativeContentDOList) {

                CreativePlanExecuteDTO executeParams = XhsCreativeContentConvert.INSTANCE.toExecuteParams(contentDO.getExecuteParams());
                if (executeParams == null) {
                    continue;
                }
                CreativePlanAppExecuteDTO appExecuteRequest = executeParams.getAppExecuteRequest();
                Map<String, Object> params = CollectionUtil.emptyIfNull(appExecuteRequest.getParams()).stream()
                        .collect(Collectors.toMap(VariableItemDTO::getField, item -> {
                            if (Objects.isNull(item.getValue())) {
                                return Optional.ofNullable(item.getDefaultValue()).orElse(StringUtils.EMPTY);
                            }
                            return item.getValue();
                        }));

                XhsAppCreativeExecuteRequest executeRequest = new XhsAppCreativeExecuteRequest();
                executeRequest.setPlanUid(contentDO.getPlanUid());
                executeRequest.setSchemeUid(contentDO.getSchemeUid());
                executeRequest.setBusinessUid(contentDO.getBusinessUid());
                executeRequest.setContentUid(contentDO.getUid());
                executeRequest.setUid(appExecuteRequest.getUid());
                executeRequest.setScene(appExecuteRequest.getScene());
                executeRequest.setParams(params);
                executeRequest.setUserId(Long.valueOf(contentDO.getCreator()));
                requests.add(executeRequest);
            }

            LocalDateTime start = LocalDateTime.now();
            List<XhsAppCreativeExecuteResponse> resp = xhsService.bathAppCreativeExecute(requests);
            if (CollectionUtils.isEmpty(resp)) {
                return result;
            }
            Map<String, XhsAppCreativeExecuteResponse> respMap = resp.stream().collect(Collectors.toMap(XhsAppCreativeExecuteResponse::getContentUid, Function.identity()));
            LocalDateTime end = LocalDateTime.now();
            Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            // 计算平均执行时间
            executeTime = new BigDecimal(String.valueOf(executeTime)).divide(new BigDecimal(String.valueOf(resp.size())), 2, RoundingMode.HALF_UP).longValue();
            for (XhsCreativeContentDO contentDO : xhsCreativeContentDOList) {
                XhsAppCreativeExecuteResponse executeResponse = respMap.get(contentDO.getUid());
                if (!executeResponse.getSuccess()) {
                    result.put(contentDO.getId(), false);
                    updateContent(contentDO, executeResponse.getErrorMsg(), contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    continue;
                }

                CopyWritingContentDTO copyWriting = executeResponse.getCopyWriting();
                if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                    result.put(contentDO.getId(), false);
                    updateContent(contentDO, "文案内容为空", contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    continue;
                }

                contentDO.setCopyWritingContent(copyWriting.getContent());
                contentDO.setCopyWritingTitle(copyWriting.getTitle());
                contentDO.setCopyWritingCount(copyWriting.getContent().length());
                contentDO.setCopyWritingResult(JSONUtil.toJsonStr(copyWriting));
                contentDO.setStartTime(start);
                contentDO.setEndTime(end);
                contentDO.setExecuteTime(executeTime);
                updateContent(contentDO, StringUtils.EMPTY, 0, XhsCreativeContentStatusEnums.EXECUTE_SUCCESS);
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

    public Map<Long, Boolean> executePicture(List<XhsCreativeContentDO> contentList, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(contentList.size());

        if (CollectionUtil.isEmpty(contentList)) {
            log.warn("创作中心：小红书图片生成执行：参数为空！图片生成结束");
            return Collections.emptyMap();
        }
        // 获取异步Future
        ThreadPoolExecutor executor = xhsImageCreativeThreadPoolHolder.executor();
        List<CompletableFuture<XhsImageCreativeExecuteResponse>> imageFutureList = Lists.newArrayList();
        for (XhsCreativeContentDO content : contentList) {
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
        Map<String, XhsCreativeContentDO> map = contentList.stream().collect(Collectors.toMap(XhsCreativeContentDO::getUid, Function.identity()));
        for (XhsImageCreativeExecuteResponse response : responses) {
            XhsCreativeContentDO content = map.get(response.getContentUid());
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
    public XhsImageCreativeExecuteResponse imageExecute(XhsCreativeContentDO content, Boolean force) {
        // 获取锁
        String lockKey = "xhs-image-creative-" + content.getId();
        RLock lock = redissonClient.getLock(lockKey);

        if (lock != null && !lock.tryLock()) {
            log.warn("创作中心：生成图片正在执行中，重复调用(内容ID：{})！", content.getId());
            throw ServiceExceptionUtil.exception(new ErrorCode(350600112, "图片执行：创作内容为空"));
        }

        try {
            // 开始执行时间
            LocalDateTime start = LocalDateTime.now();
            log.info("创作中心：生成图片正在执行开始：{}，{}", content.getId(), start);
            // 获取最新的创作内容并且校验
            XhsCreativeContentDO latestContent = getImageContentAndValidate(content.getId(), force);
            // 获取并且校验使用图片
            List<String> useImageList = JSONUtil.parseArray(latestContent.getUsePicture()).toList(String.class);
            if (CollectionUtils.isEmpty(useImageList)) {
                // 需要更新状态
                updateFailureContent(latestContent, start, "创作中心：图片执行可使用图片为空！");
                throw ServiceExceptionUtil.exception(new ErrorCode(350600113, "创作中心：图片执行可使用图片为空(ID: " + latestContent.getId() + ")！"));
            }
            // 获取并且校验执行参数
            CreativePlanExecuteDTO executeParams = XhsCreativeContentConvert.INSTANCE.toExecuteParams(latestContent.getExecuteParams());
            if (Objects.isNull(executeParams) || Objects.isNull(executeParams.getImageStyleExecuteRequest())) {
                // 需要更新状态
                updateFailureContent(latestContent, start, "创作中心：图片执行参数为空！");
                throw ServiceExceptionUtil.exception(new ErrorCode(350600114, "创作中心：图片执行参数为空(ID: " + latestContent.getId() + ")！"));
            }
            // 查询文案执行情况。需要文案执行成功才能执行图片
            XhsCreativeContentDO business = creativeContentMapper.selectByType(latestContent.getBusinessUid(), XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
            if (Objects.isNull(business) || !XhsCreativeContentStatusEnums.EXECUTE_SUCCESS.getCode().equals(business.getStatus()) ||
                    StringUtils.isBlank(business.getCopyWritingResult())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350600115, "创作中心：文案未执行成功，不能执行图片生成(ID: " + latestContent.getId() + ")！"));
            }
            // 文案执行结果
            CopyWritingContentDTO copyWriting = JSONUtil.toBean(business.getCopyWritingResult(), CopyWritingContentDTO.class);
            if (Objects.isNull(copyWriting)) {
                // 需要更新状态
                updateFailureContent(latestContent, start, "创作中心：图片执行参数为空，执行文案不存在");
                throw ServiceExceptionUtil.exception(new ErrorCode(350600117, "创作中心：图片执行参数为空，执行文案不存在(ID: " + latestContent.getId() + ")！"));
            }

            // 构建请求
            CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = executeParams.getImageStyleExecuteRequest();
            XhsImageStyleExecuteRequest imageStyleRequest = XhsCreativeContentConvert.INSTANCE.toExecuteImageStyle(imageStyleExecuteRequest, useImageList, copyWriting);
            XhsImageCreativeExecuteRequest request = new XhsImageCreativeExecuteRequest();
            request.setPlanUid(latestContent.getPlanUid());
            request.setSchemeUid(latestContent.getSchemeUid());
            request.setBusinessUid(latestContent.getBusinessUid());
            request.setContentUid(latestContent.getUid());
            request.setImageStyleRequest(imageStyleRequest);
            // 执行请求
            XhsImageCreativeExecuteResponse response = xhsService.imageCreativeExecute(request);

            // 校验结果
            if (Objects.isNull(response)) {
                updateFailureContent(latestContent, start, "创作中心：图片生成结果为空！");
                throw ServiceExceptionUtil.exception(new ErrorCode(350600118, "创作中心：图片生成结果为空(ID: " + latestContent.getId() + ")！"));
            }
            // 是否成功
            if (!response.getSuccess()) {
                Integer errorCode = Objects.isNull(response.getErrorCode()) ? 350600119 : response.getErrorCode();
                String message = StringUtils.isBlank(response.getErrorMessage()) ? "创作中心：图片生成失败！" : response.getErrorMessage();
                updateFailureContent(latestContent, start, response.getErrorMessage());
                throw ServiceExceptionUtil.exception(new ErrorCode(errorCode, message));
            }
            // 图片生成结果
            List<XhsImageExecuteResponse> imageResponseList = CollectionUtil.emptyIfNull(response.getImageStyleResponse().getImageResponses());
            if (CollectionUtils.isEmpty(imageResponseList)) {
                updateFailureContent(latestContent, start, "创作中心：图片生成结果为空！");
                throw ServiceExceptionUtil.exception(new ErrorCode(350600120, "创作中心：图片生成结果为空(ID: " + latestContent.getId() + ")！"));
            }
            // 更新创作内容
            LocalDateTime end = LocalDateTime.now();
            List<XhsCreativePictureContentDTO> pictureContent = XhsCreativeContentConvert.INSTANCE.convert2(imageResponseList);
            latestContent.setPictureContent(JSONUtil.toJsonStr(pictureContent));
            latestContent.setStartTime(start);
            latestContent.setEndTime(end);
            latestContent.setPictureNum(pictureContent.size());
            long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            // 计算平均执行时间
            executeTime = new BigDecimal(String.valueOf(executeTime)).divide(new BigDecimal(String.valueOf(imageResponseList.size())), 2, RoundingMode.HALF_UP).longValue();
            latestContent.setExecuteTime(executeTime);
            updateContent(latestContent, StringUtils.EMPTY, latestContent.getRetryCount(), XhsCreativeContentStatusEnums.EXECUTE_SUCCESS);
            log.info("创作中心：图片执行成功：ID：{}, 好事： {} ms", latestContent.getId(), executeTime);

            return response;
        } catch (ServiceException exception) {
            log.error("创作中心：图片执行失败：错误码: {}, 错误信息: {}", exception.getCode(), exception.getMessage(), exception);
            return XhsImageCreativeExecuteResponse.failure(content, exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.error("创作中心：图片执行失败： 错误信息: {}", exception.getMessage(), exception);
            return XhsImageCreativeExecuteResponse.failure(content, 350600130, exception.getMessage(), null);
        } finally {
            if (lock != null) {
                lock.unlock();
                log.info("解锁成功：{}", lockKey);
            }
        }
    }

    private void updateFailureContent(XhsCreativeContentDO content, LocalDateTime start, String errorMsg) {
        content.setErrorMsg(errorMsg);
        content.setRetryCount(content.getRetryCount() + 1);
        content.setStatus(XhsCreativeContentStatusEnums.EXECUTE_ERROR.getCode());
        content.setStartTime(start);
        LocalDateTime end = LocalDateTime.now();
        content.setEndTime(end);
        Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        content.setExecuteTime(executeTime);
        content.setUpdateTime(end);
        creativeContentMapper.updateById(content);
    }

    private void updateContent(XhsCreativeContentDO xhsCreativeContentDO,
                               String errorMsg, Integer retryCount,
                               XhsCreativeContentStatusEnums statusEnums) {
        xhsCreativeContentDO.setErrorMsg(errorMsg);
        xhsCreativeContentDO.setRetryCount(retryCount);
        xhsCreativeContentDO.setStatus(statusEnums.getCode());
        xhsCreativeContentDO.setUpdateTime(LocalDateTime.now());
        creativeContentMapper.updateById(xhsCreativeContentDO);
    }

    private Integer getMaxRetry(Boolean force) {
        if (BooleanUtils.isTrue(force)) {
            return Integer.MAX_VALUE;
        }
        DictDataDO dictDataDO = dictDataService.parseDictData("xhs", "max_retry");
        if (dictDataDO == null || dictDataDO.getValue() == null) {
            return 3;
        }
        return Integer.valueOf(dictDataDO.getValue());
    }

    private XhsCreativeContentDO getImageContentAndValidate(Long id, Boolean force) {
        XhsCreativeContentDO content = creativeContentMapper.selectById(id);
        if (Objects.isNull(content)) {
            log.warn("未找到对应的创作任务：{}", id);
            throw ServiceExceptionUtil.exception(new ErrorCode(350600211, "未找到对应的创作任务(ID: " + id + ")！"));
        }

        if (XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(content.getStatus())) {
            log.warn("创作任务在执行中：{}", id);
            throw ServiceExceptionUtil.exception(new ErrorCode(350600212, "创作任务在执行中(ID: " + id + ")！"));
        }

        if (!force && XhsCreativeContentStatusEnums.EXECUTE_SUCCESS.getCode().equals(content.getStatus())) {
            log.warn("创作任务已成功：{}", id);
            throw ServiceExceptionUtil.exception(new ErrorCode(350600213, "创作任务已成功(ID: " + id + ")！"));
        }

        if (!XhsCreativeContentTypeEnums.PICTURE.getCode().equalsIgnoreCase(content.getType())) {
            log.warn("不是图片类型的任务{}", id);
            throw ServiceExceptionUtil.exception(new ErrorCode(350600214, "不是图片类型的任务(ID: " + id + ")！"));
        }

        Integer maxRetry = getMaxRetry(force);
        if (content.getRetryCount() >= maxRetry) {
            log.warn("创作任务: {} 在重试次数：{}， 最多重试次数：{}", id, content.getRetryCount(), maxRetry);
            throw ServiceExceptionUtil.exception(new ErrorCode(350600215, "创作任务: " + id + "，在重试次数：" + content.getRetryCount() + "， 最多重试次数：" + maxRetry + "！"));
        }

        return content;
    }
}
