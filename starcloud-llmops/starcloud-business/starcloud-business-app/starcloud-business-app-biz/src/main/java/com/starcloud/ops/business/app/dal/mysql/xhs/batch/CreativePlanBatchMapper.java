package com.starcloud.ops.business.app.dal.mysql.xhs.batch;


import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreativePlanBatchMapper extends BaseMapperX<CreativePlanBatchDO> {

    /**
     * 根据UID查询创作计划批量任务
     *
     * @param uid 批次UID
     * @return 创作计划批量任务
     */
    default CreativePlanBatchDO get(String uid) {
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class);
        wrapper.eq(CreativePlanBatchDO::getUid, uid);
        return selectOne(wrapper);
    }

    /**
     * 根据创作计划UID查询创作计划批量任务
     *
     * @param query 查询条件
     * @return 创作计划批次
     */
    default List<CreativePlanBatchDO> list(CreativePlanBatchListReqVO query) {
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class);
        wrapper.eq(StringUtils.isNotBlank(query.getPlanUid()), CreativePlanBatchDO::getPlanUid, query.getPlanUid());
        return selectList(wrapper);
    }

    /**
     * 根据创作计划UID查询创作计划批量任务
     *
     * @param query 查询条件
     * @return 创作计划批次
     */
    default List<CreativePlanBatchDO> listStatus(CreativePlanBatchListReqVO query) {
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class);
        // 只查询ID，UID，status 字段
        wrapper.select(CreativePlanBatchDO::getId, CreativePlanBatchDO::getUid, CreativePlanBatchDO::getStatus);
        wrapper.eq(StringUtils.isNotBlank(query.getPlanUid()), CreativePlanBatchDO::getPlanUid, query.getPlanUid());
        return selectList(wrapper);
    }

    /**
     * 分页查询创作计划批量任务
     *
     * @param query 查询条件
     * @return 分页结果
     */
    default IPage<CreativePlanBatchDO> page(CreativePlanBatchPageReqVO query) {
        Page<CreativePlanBatchDO> page = PageUtil.page(query);
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class);
        // 不查询 configuration 字段
        wrapper.select(CreativePlanBatchDO.class, item -> !"configuration".equalsIgnoreCase(item.getColumn()));
        wrapper.eq(CreativePlanBatchDO::getPlanUid, query.getPlanUid());
        wrapper.orderByDesc(CreativePlanBatchDO::getId);
        return this.selectPage(page, wrapper);
    }

    /**
     * 根据创作计划UID删除创作计划批量任务
     *
     * @param planUid 创作计划UID
     */
    default void deleteByPlanUid(String planUid) {
        LambdaUpdateWrapper<CreativePlanBatchDO> updateWrapper = Wrappers.lambdaUpdate(CreativePlanBatchDO.class);
        updateWrapper.eq(CreativePlanBatchDO::getPlanUid, planUid);
        this.delete(updateWrapper);
    }

}
