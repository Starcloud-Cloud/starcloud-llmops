package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeQueryReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.convert.xhs.XhsCreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsCreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class XhsCreativeContentServiceImpl implements XhsCreativeContentService {

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private RedissonClient redissonClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<XhsCreativeContentCreateReq> createReqs) {

        for (XhsCreativeContentCreateReq createReq : createReqs) {
            XhsCreativeContentDO contentDO = XhsCreativeContentConvert.INSTANCE.convert(createReq);
            contentDO.setUid(IdUtil.fastSimpleUUID());
            contentDO.setStatus(XhsCreativeContentStatusEnums.INIT.getCode());
            creativeContentMapper.insert(contentDO);
        }
    }

    @Override
    public void execute(List<Long> ids, String type, Boolean force) {
        Integer maxRetry = getMaxRetry(force);
        List<XhsCreativeContentDO> contentList = creativeContentMapper.selectBatchIds(ids)
                .stream().filter(content -> {
                    return content.getRetryCount() < maxRetry
                            && !XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(content.getStatus());
                }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(contentList)) {
            throw exception(NO_CREATIVE_CONTENT_CAN_EXECUTE);
        }
        if (XhsCreativeContentTypeEnums.PICTURE.getCode().equals(type)) {
            executeCopyWriting(contentList);
        } else if (XhsCreativeContentTypeEnums.COPY_WRITING.getCode().equals(type)) {
            executePicture(contentList);
        } else {
            throw exception(UNSUPPORTED_TYPE, type);
        }
    }

    @Override
    public void retry(String businessUid) {
        XhsCreativeContentDO textDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        XhsCreativeContentDO picDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.PICTURE.getCode());
        // 校验
        executePicture(Collections.singletonList(picDO));
        executeCopyWriting(Collections.singletonList(textDO));
    }

    @Override
    public List<XhsCreativeContentDO> batchSelect(XhsCreativeQueryReq queryReq) {
        return creativeContentMapper.jobQuery(queryReq);
    }

    @Override
    public PageResult<XhsCreativeContentResp> page(XhsCreativeContentPageReq req) {
        Long count = creativeContentMapper.selectCount(req);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<XhsCreativeContentDTO> pageSelect = creativeContentMapper.pageSelect(req, PageUtils.getStart(req), req.getPageSize());
        return new PageResult<>(XhsCreativeContentConvert.INSTANCE.convertDto(pageSelect), count);
    }

    @Override
    public XhsCreativeContentResp detail(String businessUid) {
        XhsCreativeContentDTO detail = byBusinessUid(businessUid);
        return XhsCreativeContentConvert.INSTANCE.convert(detail);
    }

    @Override
    public XhsCreativeContentResp modify(XhsCreativeContentModifyReq modifyReq) {
        XhsCreativeContentDO contentDO = creativeContentMapper.selectByType(modifyReq.getBusinessUid(), XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        if (contentDO == null) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, modifyReq.getBusinessUid());
        }
        XhsCreativeContentConvert.INSTANCE.updateSelective(modifyReq, contentDO);
        creativeContentMapper.updateById(contentDO);
        return XhsCreativeContentConvert.INSTANCE.convert(contentDO);
    }


    private void executeCopyWriting(List<XhsCreativeContentDO> xhsCreativeContentDO) {

    }

    private void executePicture(List<XhsCreativeContentDO> xhsCreativeContentDO) {

    }

    /**
     * 执行生成任务  状态不能是执行中
     */
    private void execute(XhsCreativeContentDO xhsCreativeContentDO, Boolean force) {
        String key = "xhs-content-" + xhsCreativeContentDO.getUid();
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(0L, 60L, TimeUnit.SECONDS)) {
                return;
            }
            // 加锁后校验状态 重试次数
            xhsCreativeContentDO = creativeContentMapper.selectById(xhsCreativeContentDO.getId());
            if (XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(xhsCreativeContentDO.getStatus())) {
                lock.unlock();
                return;
            }

            Integer maxRetry = getMaxRetry(force);
            if (xhsCreativeContentDO.getRetryCount() >= maxRetry) {
                lock.unlock();
                return;
            }
            updateDO(xhsCreativeContentDO, StringUtils.EMPTY,
                    xhsCreativeContentDO.getRetryCount() + 1,
                    XhsCreativeContentStatusEnums.EXECUTING);

            XhsCreativeContentDO finalXhsCreativeContentDO = xhsCreativeContentDO;

            try {
                log.info("xhs execute start");
                LocalDateTime start = LocalDateTime.now();
                LocalDateTime end = LocalDateTime.now();


                finalXhsCreativeContentDO.setStartTime(start);
                finalXhsCreativeContentDO.setEndTime(end);
                Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                finalXhsCreativeContentDO.setExecuteTime(executeTime);
                updateDO(finalXhsCreativeContentDO, StringUtils.EMPTY, finalXhsCreativeContentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_SUCCESS);
            } catch (Exception e) {
                log.error("xhs execute error", e);
                updateDO(finalXhsCreativeContentDO, e.getMessage(), finalXhsCreativeContentDO.getRetryCount() + 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
            } finally {
                lock.unlock();
            }

        } catch (RejectedExecutionException e) {
            log.error("", e);
            // 线程池打满 不计入重试次数
            updateDO(xhsCreativeContentDO, "RejectedExecutionException", xhsCreativeContentDO.getRetryCount() - 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
            lock.unlock();
        } catch (Exception e) {
            log.error("", e);
            updateDO(xhsCreativeContentDO, e.getMessage(), xhsCreativeContentDO.getRetryCount() - 1, XhsCreativeContentStatusEnums.EXECUTE_ERROR);
            lock.unlock();
        }
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

    private XhsCreativeContentDTO byBusinessUid(String businessUid) {
        XhsCreativeContentDTO detail = creativeContentMapper.detail(businessUid);
        if (detail == null) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
        }
        return detail;
    }

    private void updateDO(XhsCreativeContentDO xhsCreativeContentDO,
                          String errorMsg, Integer retryCount,
                          XhsCreativeContentStatusEnums statusEnums) {
        xhsCreativeContentDO.setErrorMsg(errorMsg);
        xhsCreativeContentDO.setRetryCount(retryCount);
        xhsCreativeContentDO.setStatus(statusEnums.getCode());
        creativeContentMapper.updateById(xhsCreativeContentDO);
    }

}
