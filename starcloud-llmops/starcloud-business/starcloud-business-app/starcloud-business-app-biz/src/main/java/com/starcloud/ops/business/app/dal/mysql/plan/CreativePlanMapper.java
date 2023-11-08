package com.starcloud.ops.business.app.dal.mysql.plan;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public interface CreativePlanMapper extends BaseMapperX<CreativePlanDO> {

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
}
