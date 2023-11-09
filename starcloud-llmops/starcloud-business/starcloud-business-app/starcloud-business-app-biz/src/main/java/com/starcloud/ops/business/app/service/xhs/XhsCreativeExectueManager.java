package com.starcloud.ops.business.app.service.xhs;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExecuteParamsDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativePictureContentDTO;
import com.starcloud.ops.business.app.convert.xhs.XhsCreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsCreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class XhsCreativeExectueManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private XhsService xhsService;

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;


    public Map<Long, Boolean> executeCopyWriting(List<XhsCreativeContentDO> xhsCreativeContentDO, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(xhsCreativeContentDO.size());
        // 排序 id 加锁



        return result;
    }

    public Map<Long, Boolean> executePicture(List<XhsCreativeContentDO> xhsCreativeContentDO, Boolean force) {
        Map<Long, Boolean> result = new HashMap<>(xhsCreativeContentDO.size());
        for (XhsCreativeContentDO contentDO : xhsCreativeContentDO) {
            String key = "xhs-content-" + contentDO.getUid();
            RLock lock = redissonClient.getLock(key);
            try {
                if (lock != null && !lock.tryLock(0L, 60L, TimeUnit.SECONDS)) {
                    log.warn("图片正在执行中，重复调用 {}", contentDO.getId());
                    result.put(contentDO.getId(), false);
                    continue;
                }
                // 校验状态 重试次数
                contentDO = getReadyCreative(contentDO.getId(), force);
                if (contentDO == null) {
                    log.warn("图片重试次数超过限制： {}", contentDO.getId());
                    result.put(contentDO.getId(), false);
                    continue;
                }

                LocalDateTime start = LocalDateTime.now();

                XhsCreativeContentExecuteParamsDTO executeParams = XhsCreativeContentConvert.INSTANCE.toExecuteParams(contentDO.getExecuteParams());
                if (executeParams == null || executeParams.getBathImageExecuteRequest() == null) {
                    log.warn("图片执行参数不存在： {}", contentDO.getId());
                    result.put(contentDO.getId(), false);
                    continue;
                }

                XhsBathImageExecuteRequest request = executeParams.getBathImageExecuteRequest();
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
                log.warn("图片执行成功： {} ms", executeTime);
            } catch (Exception e) {
                log.error("执行图片生成失败", e);
                result.put(contentDO.getId(), false);
                updateDO(contentDO, e.getMessage(), contentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
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
        Integer maxRetry = getMaxRetry(force);
        if (contentDO.getRetryCount() >= maxRetry) {
            log.warn("创作任务: {} 在重试次数：{}， 最多重试次数：{}", id, contentDO.getRetryCount(), maxRetry);
            return null;
        }
        return contentDO;
    }
}
