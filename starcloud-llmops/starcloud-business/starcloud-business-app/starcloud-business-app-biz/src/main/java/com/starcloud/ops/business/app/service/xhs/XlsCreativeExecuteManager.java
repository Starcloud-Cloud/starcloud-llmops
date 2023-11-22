package com.starcloud.ops.business.app.service.xhs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Component
@Slf4j
public class XlsCreativeExecuteManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private XhsService xhsService;

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;


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
        xhsCreativeContentDOList = creativeContentMapper.selectBatchIds(ids).stream().filter(xhsCreativeContentDO -> {
            return xhsCreativeContentDO.getRetryCount() < maxRetry
                    && !XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(xhsCreativeContentDO.getStatus())
                    && XhsCreativeContentTypeEnums.COPY_WRITING.getCode().equalsIgnoreCase(xhsCreativeContentDO.getType());
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(xhsCreativeContentDOList)) {
            log.warn("没有可执行状态的任务，{}", sj);
            return result;
        }

        try {
            List<XhsAppCreativeExecuteRequest> requests = new ArrayList<>(xhsCreativeContentDOList.size());
            for (XhsCreativeContentDO contentDO : xhsCreativeContentDOList) {

                XhsAppCreativeExecuteRequest executeRequest = new XhsAppCreativeExecuteRequest();
                CreativePlanExecuteDTO executeParams = XhsCreativeContentConvert.INSTANCE.toExecuteParams(contentDO.getExecuteParams());
                if (executeParams == null) {
                    continue;
                }
                CreativePlanAppExecuteDTO appExecuteRequest = executeParams.getAppExecuteRequest();
                executeRequest.setUid(appExecuteRequest.getUid());
                executeRequest.setScene(appExecuteRequest.getScene());
                Map<String, Object> params = CollectionUtil.emptyIfNull(appExecuteRequest.getParams()).stream()
                        .collect(Collectors.toMap(VariableItemDTO::getField, item -> {
                            if (Objects.isNull(item.getValue())) {
                                return Optional.ofNullable(item.getDefaultValue()).orElse(StringUtils.EMPTY);
                            }
                            return item.getValue();
                        }));
                executeRequest.setParams(params);
                executeRequest.setUserId(Long.valueOf(contentDO.getCreator()));
                executeRequest.setCreativeContentUid(contentDO.getUid());
                requests.add(executeRequest);
            }

            LocalDateTime start = LocalDateTime.now();
            List<XhsAppCreativeExecuteResponse> resp = xhsService.bathAppCreativeExecute(requests);
            if (CollectionUtils.isEmpty(resp)) {
                return result;
            }
            Map<String, XhsAppCreativeExecuteResponse> respMap = resp.stream().collect(Collectors.toMap(XhsAppCreativeExecuteResponse::getCreativeContentUid, Function.identity()));
            LocalDateTime end = LocalDateTime.now();
            Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            for (XhsCreativeContentDO contentDO : xhsCreativeContentDOList) {
                XhsAppCreativeExecuteResponse executeResponse = respMap.get(contentDO.getUid());
                if (!executeResponse.getSuccess()) {
                    result.put(contentDO.getId(), false);
                    updateDO(contentDO, executeResponse.getErrorMsg(), contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    continue;
                }

                CopyWritingContentDTO copyWriting = executeResponse.getCopyWriting();
                if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                    result.put(contentDO.getId(), false);
                    updateDO(contentDO, "文案内容为空", contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    continue;
                }

                contentDO.setCopyWritingContent(copyWriting.getContent());
                contentDO.setCopyWritingTitle(copyWriting.getTitle());
                contentDO.setCopyWritingCount(copyWriting.getContent().length());
                contentDO.setStartTime(start);
                contentDO.setEndTime(end);
                contentDO.setExecuteTime(executeTime);
                updateDO(contentDO, StringUtils.EMPTY, contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_SUCCESS);
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

    public Map<Long, Boolean> executePicture(List<XhsCreativeContentDO> xhsCreativeContentDO, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(xhsCreativeContentDO.size());
        for (XhsCreativeContentDO sourceContentDO : xhsCreativeContentDO) {
            String key = "xhs-pic-" + sourceContentDO.getUid();
            RLock lock = redissonClient.getLock(key);
            try {
                if (lock != null && !lock.tryLock(0L, 60L, TimeUnit.SECONDS)) {
                    log.warn("图片正在执行中，重复调用 {}", sourceContentDO.getId());
                    result.put(sourceContentDO.getId(), false);
                    continue;
                }
                // 校验状态 重试次数
                XhsCreativeContentDO contentDO = getReadyCreative(sourceContentDO.getId(), force);
                if (contentDO == null) {
                    result.put(sourceContentDO.getId(), false);
                    continue;
                }

                LocalDateTime start = LocalDateTime.now();

                CreativePlanExecuteDTO executeParams = XhsCreativeContentConvert.INSTANCE.toExecuteParams(contentDO.getExecuteParams());
                if (executeParams == null || executeParams.getImageStyleExecuteRequest() == null) {
                    log.warn("图片执行参数不存在： {}", contentDO.getId());
                    result.put(contentDO.getId(), false);
                    continue;
                }

                CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = executeParams.getImageStyleExecuteRequest();
                XhsBathImageExecuteRequest request = XhsCreativeContentConvert.INSTANCE.toExecuteImageStyle(imageStyleExecuteRequest, JSONUtil.parseArray(contentDO.getUsePicture()).toList(String.class));
                List<XhsImageExecuteResponse> resp = xhsService.bathImageExecute(request);

                if (CollectionUtils.isEmpty(resp)) {
                    log.warn("图片执行返回结果为空： {}", contentDO.getId());
                    updateDO(contentDO, "返回结果为空", contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    result.put(contentDO.getId(), false);
                    continue;
                }
                List<XhsImageExecuteResponse> errorList = resp.stream().filter(r -> !r.getSuccess()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(errorList)) {
                    StringJoiner sj = new StringJoiner(",");
                    errorList.forEach(e -> sj.add(e.getErrorMsg()));
                    log.warn("图片执行返回结果失败： {}", sj);
                    updateDO(contentDO, sj.toString(), contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
                    result.put(contentDO.getId(), false);
                    continue;
                }
                LocalDateTime end = LocalDateTime.now();
                List<XhsCreativePictureContentDTO> pictureContent = XhsCreativeContentConvert.INSTANCE.convert2(resp);
                contentDO.setPictureContent(JSONUtil.toJsonStr(pictureContent));
                contentDO.setStartTime(start);
                contentDO.setEndTime(end);
                contentDO.setPictureNum(pictureContent.size());
                Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                contentDO.setExecuteTime(executeTime);
                updateDO(contentDO, StringUtils.EMPTY, contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_SUCCESS);
                result.put(contentDO.getId(), true);
                log.info("图片执行成功： {} ms", executeTime);
            } catch (Exception e) {
                log.error("执行图片生成失败", e);
                result.put(sourceContentDO.getId(), false);
                updateDO(sourceContentDO, e.getMessage(), sourceContentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    private void updateDO(XhsCreativeContentDO xhsCreativeContentDO,
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

    private XhsCreativeContentDO getReadyCreative(Long id, Boolean force) {
        XhsCreativeContentDO contentDO = creativeContentMapper.selectById(id);
        if (contentDO == null) {
            log.warn("未找到对应的创作任务：{}", id);
            return null;
        }
        if (XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(contentDO.getStatus())) {
            log.warn("创作任务在执行中：{}", id);
            return null;
        }

        if (!force && XhsCreativeContentStatusEnums.EXECUTE_SUCCESS.getCode().equals(contentDO.getStatus())) {
            log.warn("创作任务已成功：{}", id);
            return null;
        }

        if (!XhsCreativeContentTypeEnums.PICTURE.getCode().equalsIgnoreCase(contentDO.getType())) {
            log.warn("不是图片类型的任务{}", id);
            return null;
        }
        Integer maxRetry = getMaxRetry(force);
        if (contentDO.getRetryCount() >= maxRetry) {
            log.warn("创作任务: {} 在重试次数：{}， 最多重试次数：{}", id, contentDO.getRetryCount(), maxRetry);
            return null;
        }
        return contentDO;
    }
}
