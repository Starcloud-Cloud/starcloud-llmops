package com.starcloud.ops.business.app.dal.mysql.xhs.plan;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDTO;
import com.starcloud.ops.business.app.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Mapper
public interface CreativePlanMapper extends BaseMapper<CreativePlanDO> {

    /**
     * 根据uid查询
     *
     * @param uid uid
     * @return CreativePlanDO
     */
    default CreativePlanDO get(String uid) {
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CreativePlanDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    /**
     * 根据应用uid查询
     *
     * @param appUid 应用uid
     * @return CreativePlanDO
     */
    default CreativePlanDO getByAppUid(String appUid, String source) {
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CreativePlanDO::getAppUid, appUid);
        wrapper.eq(CreativePlanDO::getSource, source);
        return this.selectOne(wrapper);
    }

    /**
     * 根据应用uid查询
     *
     * @param appUid 应用uid
     * @return CreativePlanDO
     */
    default CreativePlanDO getByAppUid(String appUid, Long userId, String source) {
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CreativePlanDO::getAppUid, appUid);
        wrapper.eq(CreativePlanDO::getCreator, userId);
        wrapper.eq(CreativePlanDO::getSource, source);
        return this.selectOne(wrapper);
    }

    /**
     * 获取创作计划列表
     *
     * @param query 请求参数
     * @return 创作计划列表
     */
    default List<CreativePlanDO> list(CreativePlanListQuery query) {
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery(CreativePlanDO.class);
        wrapper.eq(StringUtils.isNotBlank(query.getUid()), CreativePlanDO::getUid, query.getUid());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), CreativePlanDO::getStatus, query.getStatus());
        return this.selectList(wrapper);
    }

    /**
     * 分页查询
     *
     * @return 分页结果
     */
    default IPage<CreativePlanDO> page(CreativePlanPageQuery query) {
        // 构造分页参数
        Page<CreativePlanDO> page = PageUtil.page(query);
        // 构造查询条件
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery(CreativePlanDO.class);
        wrapper.eq(StringUtils.isNotBlank(query.getUid()), CreativePlanDO::getUid, query.getUid());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), CreativePlanDO::getStatus, query.getStatus());
        wrapper.between(Objects.nonNull(query.getStartTime()) && Objects.nonNull(query.getEndTime()), CreativePlanDO::getCreateTime, query.getStartTime(), query.getEndTime());
        return this.selectPage(page, wrapper);
    }

    List<CreativePlanDTO> list(@Param("currentUserId") String currentUserId);

}
