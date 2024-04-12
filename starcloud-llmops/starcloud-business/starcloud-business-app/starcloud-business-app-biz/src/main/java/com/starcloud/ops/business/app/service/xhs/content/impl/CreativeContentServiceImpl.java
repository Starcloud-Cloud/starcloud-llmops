package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgressDTO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentTaskReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeExecuteManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Service
@Slf4j
public class CreativeContentServiceImpl implements CreativeContentService {

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private CreativeExecuteManager creativeExecuteManager;

    @Resource
    @Lazy
    private CreativePlanService creativePlanService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private AppStepStatusCache appStepStatusCache;

    /**
     * 获取创作内容详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    @Override
    public CreativeContentRespVO get(String uid) {
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", uid);
        return CreativeContentConvert.INSTANCE.convert(creativeContent);
    }

    /**
     * 查询详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    @Override
    public CreativeContentRespVO detail(String uid) {
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", uid);
        return CreativeContentConvert.INSTANCE.convert(creativeContent);
    }

    /**
     * 查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    @Override
    public List<CreativeContentRespVO> list(CreativeContentListReqVO query) {
        List<CreativeContentDO> list = creativeContentMapper.list(query);
        return CreativeContentConvert.INSTANCE.convertResponseList(list);
    }

    /**
     * 查询创作内容任务列表
     *
     * @param query 查询条件
     * @return 创作内容任务列表
     */
    @Override
    public List<CreativeContentRespVO> listTask(CreativeContentTaskReqVO query) {
        List<CreativeContentDO> list = creativeContentMapper.listTask(query);
        return CreativeContentConvert.INSTANCE.convertResponseList(list);
    }

    /**
     * 分页查询创作内容
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO query) {
        IPage<CreativeContentDO> page = this.creativeContentMapper.page(query);
        if (Objects.isNull(page) || CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.empty();
        }

        // 处理查询结果
        List<CreativeContentRespVO> collect = page.getRecords()
                .stream()
                .map(item -> {
                    CreativeContentRespVO response = CreativeContentConvert.INSTANCE.convert(item);
                    // 获取执行进度
                    AppExecuteProgressDTO progress = appStepStatusCache.getProgress(response.getConversationUid());
                    response.setProgress(progress);
                    return response;
                })
                .collect(Collectors.toList());

        return PageResult.of(collect, page.getTotal());
    }

    /**
     * 创建装作内容
     *
     * @param request 请求
     * @return 创作内容UID
     */
    @Override
    public String create(CreativeContentCreateReqVO request) {
        CreativeContentDO content = CreativeContentConvert.INSTANCE.convert(request);
        creativeContentMapper.insert(content);
        return content.getUid();
    }

    /**
     * 批量创建创作内容
     *
     * @param requestList 批量请求
     */
    @Override
    public void batchCreate(List<CreativeContentCreateReqVO> requestList) {
        List<CreativeContentDO> convert = CreativeContentConvert.INSTANCE.convertList(requestList);
        creativeContentMapper.insertBatch(convert);
    }

    /**
     * 修改创作内容
     *
     * @param request 修改请求
     * @return 创作内容UID
     */
    @Override
    public String modify(CreativeContentModifyReqVO request) {
        CreativeContentDO content = creativeContentMapper.get(request.getUid());
        AppValidate.notNull(content, "创作内容不存在({})", request.getUid());
        CreativeContentDO modify = CreativeContentConvert.INSTANCE.convert(request);
        modify.setId(content.getId());
        creativeContentMapper.updateById(modify);
        return content.getUid();
    }

    /**
     * 删除创作内容
     *
     * @param uid 创作内容UID
     */
    @Override
    public void delete(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        creativeContentMapper.deleteById(content.getId());
    }

    /**
     * 删除计划下的所有创作内容
     *
     * @param planUid 计划UID
     */
    @Override
    public void deleteByPlanUid(String planUid) {
        creativeContentMapper.deleteByPlanUid(planUid);
    }

    /**
     * 执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @Override
    public CreativeContentExecuteRespVO execute(CreativeContentExecuteReqVO request) {
        CreativeContentExecuteRespVO response = creativeExecuteManager.execute(request);
        creativePlanService.updatePlanStatus(response.getPlanUid(), response.getBatchUid());
        return response;
    }

    /**
     * 批量执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @Override
    public List<CreativeContentExecuteRespVO> batchExecute(List<CreativeContentExecuteReqVO> request) {
        return creativeExecuteManager.bathExecute(request);
    }

    /**
     * 重试创作内容
     *
     * @param uid 创作内容UID
     * @return 重试之后结果
     */
    @Override
    public CreativeContentRespVO regenerate(String uid) {
        CreativeContentRespVO content = this.get(uid);
        AppValidate.notNull(content, "创作内容不存在！");
        CreativeContentExecuteReqVO request = new CreativeContentExecuteReqVO();
        request.setUid(content.getUid());
        request.setPlanUid(content.getPlanUid());
        request.setBatchUid(content.getBatchUid());
        request.setType(content.getType());
        request.setForce(Boolean.TRUE);
        request.setTenantId(content.getTenantId());
        this.execute(request);
        return this.get(uid);
    }

    /**
     * 失败重试
     *
     * @param uid 任务 uid
     */
    @Override
    public void retry(String uid) {
        // 查询任务信息
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);

        if (!CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(content.getStatus())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300500001, "该任务状态不需要进行重试！"), uid);
        }

        // 更新任务状态信息
        CreativeContentDO modify = new CreativeContentDO();
        modify.setId(content.getId());
        modify.setRetryCount(0);
        modify.setStatus(CreativeContentStatusEnum.INIT.name());
        creativeContentMapper.updateById(modify);

        // 更新计划状态信息
        LambdaUpdateWrapper<CreativePlanDO> planUpdateWrapper = Wrappers.lambdaUpdate(CreativePlanDO.class);
        planUpdateWrapper.eq(CreativePlanDO::getUid, content.getPlanUid());
        planUpdateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        creativePlanMapper.update(null, planUpdateWrapper);
    }

    /**
     * 批量绑定创作内容
     *
     * @param uidList 创作内容UID集合
     * @return 绑定之后结果
     */
    @Override
    public List<CreativeContentRespVO> batchBind(List<String> uidList) {
        return null;
    }

    /**
     * 批量解绑创作内容
     *
     * @param uidList 创作内容UID集合
     */
    @Override
    public void batchUnbind(List<String> uidList) {

    }

    /**
     * 点赞
     *
     * @param uid 创作内容UID
     */
    @Override
    public void like(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        CreativeContentDO modify = new CreativeContentDO();
        modify.setId(content.getId());
        modify.setLiked(Boolean.TRUE);
        creativeContentMapper.updateById(modify);
    }

    /**
     * 取消点赞
     *
     * @param uid 创作内容UID
     */
    @Override
    public void unlike(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        CreativeContentDO modify = new CreativeContentDO();
        modify.setId(content.getId());
        modify.setLiked(Boolean.FALSE);
        creativeContentMapper.updateById(modify);
    }
}
