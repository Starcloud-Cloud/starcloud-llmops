package com.starcloud.ops.business.app.service.xhs.batch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
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
        List<CreativePlanBatchDO> records = page.getRecords();
        // 获取批次创作人集合
        List<Long> creatorList = records.stream()
                .map(CreativePlanBatchDO::getCreator)
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
        // 获取批次创作人集合
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        // 处理分页数据
        List<CreativePlanBatchRespVO> collect = page.getRecords()
                .stream()
                .map(CreativePlanBatchConvert.INSTANCE::convert)
                .peek(item -> item.setCreator(creatorMap.getOrDefault(Long.valueOf(item.getCreator()), item.getCreator())))
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
     * 开始执行批次，将批次状态更改为执行中
     *
     * @param batchUid 批次UID
     */
    @Override
    public void startBatch(String batchUid) {
        LambdaUpdateWrapper<CreativePlanBatchDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanBatchDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanBatchDO::getStartTime, LocalDateTime.now());
        updateWrapper.set(CreativePlanBatchDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanBatchDO::getUid, batchUid);
        creativePlanBatchMapper.update(updateWrapper);
    }

    /**
     * 取消批次
     *
     * @param batchUid 批次UID
     */
    @Override
    public void cancelBatch(String batchUid) {
        LambdaUpdateWrapper<CreativePlanBatchDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanBatchDO::getStatus, CreativePlanStatusEnum.CANCELED.name());
        updateWrapper.set(CreativePlanBatchDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.set(CreativePlanBatchDO::getEndTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanBatchDO::getUid, batchUid);
        creativePlanBatchMapper.update(updateWrapper);
    }

    /**
     * 更新批次状态
     *
     * @param batchUid 批次UID
     */
    @Override
    public void updateStatus(String batchUid) {

        log.info("更新计划批次状态【开始】，batchUid: {}", batchUid);

        // 查询批次
        CreativePlanBatchDO creativePlanBatch = creativePlanBatchMapper.get(batchUid);
        AppValidate.notNull(creativePlanBatch, "创作计划批次不存在({})!", batchUid);

        if (CreativePlanStatusEnum.CANCELED.name().equals(creativePlanBatch.getStatus())) {
            log.info("创作批次不需要进行修改！状态: {}", creativePlanBatch.getStatus());
            return;
        }

        // 查询该批次下的所有创作内容任务
        CreativeContentListReqVO contentQuery = new CreativeContentListReqVO();
        contentQuery.setBatchUid(batchUid);
        List<CreativeContentRespVO> contentList = CollectionUtil.emptyIfNull(creativeContentService.listStatus(contentQuery));

        // 计算执行成功数量，失败数量
        int successCount = 0, failureCount = 0;
        for (CreativeContentRespVO content : contentList) {
            // 状态为成功，成功数量 +1
            if (CreativeContentStatusEnum.SUCCESS.name().equals(content.getStatus())) {
                successCount++;
                continue;
            }
            // 状态为失败，失败数量 +1
            if (CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(content.getStatus())) {
                failureCount++;
            }
        }

        // 重新计算装条信息
        String status = creativePlanBatch.getStatus();
        // 全部执行成功才算执行完成
        if (successCount == contentList.size()) {
            log.info("将要更新计划批次为【完成】状态，batchUid: {}", batchUid);
            status = CreativePlanStatusEnum.COMPLETE.name();
        }
        // 有一条执行彻底失败就算批次执行失败
        else if (failureCount > 0) {
            log.info("将要更新计划批次为【失败】状态，batchUid: {}", batchUid);
            status = CreativePlanStatusEnum.FAILURE.name();
        }

        // 计算之后的状态如果还是待执行或者执行中，则不进行更新
        if (CreativePlanStatusEnum.PENDING.name().equals(status) ||
                CreativePlanStatusEnum.RUNNING.name().equals(status)) {
            log.info("创作批次不需要进行修改！状态: {}, 成功数量: {}，失败数量: {}", status, successCount, failureCount);
            return;
        }

        // 获取执行时间
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(creativePlanBatch.getStartTime(), now);

        // 更新计划批次信息
        CreativePlanBatchDO updateBath = new CreativePlanBatchDO();
        updateBath.setId(creativePlanBatch.getId());
        updateBath.setSuccessCount(successCount);
        updateBath.setFailureCount(failureCount);
        updateBath.setStatus(status);
        updateBath.setEndTime(now);
        updateBath.setElapsed(duration.toMillis());
        updateBath.setUpdater(creativePlanBatch.getUpdater());
        updateBath.setUpdateTime(now);
        creativePlanBatchMapper.updateById(updateBath);

        log.info("更新计划批次状态【结束】，batchUid: {}", batchUid);
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
