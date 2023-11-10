package com.starcloud.ops.business.app.dal.mysql.plan;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 根据uid查询
     *
     * @param uid uid
     * @return CreativePlanPO
     */
    CreativePlanPO getByUid(String uid);

    /**
     * 分页查询
     *
     * @param page  分页
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<CreativePlanPO> pageCreativePlan(IPage<CreativePlanDO> page, @Param("query") CreativePlanPageQuery query);

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return 是否存在
     */
    default Boolean distinctName(String name) {
        LambdaQueryWrapper<CreativePlanDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CreativePlanDO::getName, name);
        return this.selectCount(wrapper) > 0;
    }
}
