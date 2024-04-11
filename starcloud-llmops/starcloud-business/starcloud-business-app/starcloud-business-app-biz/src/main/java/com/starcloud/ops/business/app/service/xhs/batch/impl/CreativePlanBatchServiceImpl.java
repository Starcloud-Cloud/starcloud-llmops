package com.starcloud.ops.business.app.service.xhs.batch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.bath.vo.request.CreativePlanBatchListReqVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.enums.xhs.batch.CreativePlanBatchStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreativePlanBatchServiceImpl implements CreativePlanBatchService {

    @Resource
    private CreativePlanBatchMapper creativePlanBatchMapper;

    @Resource
    private CreativeContentService creativeContentService;

    /**
     * 获取批次详情
     *
     * @param uid 批次UID
     * @return 批次详情
     */
    @Override
    public CreativePlanBatchRespVO get(String uid) {
        CreativePlanBatchDO creativePlanBatch = creativePlanBatchMapper.get(uid);
        AppValidate.notNull(creativePlanBatch, "创作计划批次不存在({})!", uid);
        return CreativePlanBatchConvert.INSTANCE.convert(creativePlanBatch);
    }

    /**
     * 根据计划UID获取批次列表
     *
     * @param query 查询条件
     * @return 批次列表
     */
    @Override
    public List<CreativePlanBatchRespVO> list(CreativePlanBatchListReqVO query) {
        List<CreativePlanBatchDO> creativePlanBatch = creativePlanBatchMapper.list(query);
        return CreativePlanBatchConvert.INSTANCE.convert(creativePlanBatch);
    }

    /**
     * 分页查询批次
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<CreativePlanBatchRespVO> page(CreativePlanBatchPageReqVO query) {
        IPage<CreativePlanBatchDO> page = creativePlanBatchMapper.page(query);
        if (Objects.isNull(page) || CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.empty();
        }
        List<CreativePlanBatchRespVO> collect = page.getRecords()
                .stream()
                .map(CreativePlanBatchConvert.INSTANCE::convert)
                .collect(Collectors.toList());
        return PageResult.of(collect, page.getTotal());
    }

    /**
     * 创建批次
     *
     * @param request 请求
     * @return 计划批次UID
     */
    @Override
    public String create(CreativePlanBatchReqVO request) {
        CreativePlanBatchDO bath = CreativePlanBatchConvert.INSTANCE.convert(request);
        bath.setUid(IdUtil.fastSimpleUUID());
        creativePlanBatchMapper.insert(bath);
        return bath.getUid();
    }

    /**
     * 更新批次状态
     *
     * @param batchUid 批次UID
     */
    @Override
    public void updateStatus(String batchUid) {
        // 查询批次
        CreativePlanBatchDO creativePlanBatch = creativePlanBatchMapper.get(batchUid);
        AppValidate.notNull(creativePlanBatch, "创作计划批次不存在({})!", batchUid);

        // 查询该批次下的所有创作内容任务
        CreativeContentListReqVO contentQuery = new CreativeContentListReqVO();
        contentQuery.setBatchUid(batchUid);
        List<CreativeContentRespVO> creativeContentList = creativeContentService.list(contentQuery);

        int successCount = 0, failureCount = 0;
        boolean complete = true;
        for (CreativeContentRespVO creativeContent : creativeContentList) {
            if (CreativeContentStatusEnum.SUCCESS.name().equals(creativeContent.getStatus())) {
                successCount++;
            } else if (CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(creativeContent.getStatus())) {
                failureCount++;
            } else {
                // 批次未全部执行结束
                complete = false;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(creativePlanBatch.getStartTime(), now);
        creativePlanBatch.setSuccessCount(successCount);
        creativePlanBatch.setFailureCount(failureCount);

        if (!complete) {
            creativePlanBatch.setStatus(CreativePlanBatchStatusEnum.RUNNING.name());
        } else if (failureCount == 0) {
            creativePlanBatch.setStatus(CreativePlanBatchStatusEnum.SUCCESS.name());
        } else {
            creativePlanBatch.setStatus(CreativePlanBatchStatusEnum.FAILURE.name());
        }

        creativePlanBatch.setEndTime(now);
        creativePlanBatch.setElapsed(duration.toMillis());
        creativePlanBatch.setUpdateTime(now);
        creativePlanBatchMapper.updateById(creativePlanBatch);
    }

    /**
     * 删除批次
     *
     * @param planUid 计划UID
     */
    @Override
    public void deleteByPlanUid(String planUid) {
        this.creativePlanBatchMapper.deleteByPlanUid(planUid);
    }

}
