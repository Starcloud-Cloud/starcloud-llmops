package com.starcloud.ops.business.job.biz.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessJobMapper extends BaseMapperX<BusinessJobDO> {

    default BusinessJobDO getByJobId(Long jobId) {
        LambdaQueryWrapper<BusinessJobDO> wrapper = Wrappers.lambdaQuery(BusinessJobDO.class)
                .eq(BusinessJobDO::getJobId, jobId);
        return selectOne(wrapper);
    }

    default BusinessJobDO getByForeignKey(String foreignKey) {
        LambdaQueryWrapper<BusinessJobDO> wrapper = Wrappers.lambdaQuery(BusinessJobDO.class)
                .eq(BusinessJobDO::getForeignKey, foreignKey);
        return selectOne(wrapper);
    }

    default BusinessJobDO getByUid(String uid) {
        LambdaQueryWrapper<BusinessJobDO> wrapper = Wrappers.lambdaQuery(BusinessJobDO.class)
                .eq(BusinessJobDO::getUid, uid);
        return selectOne(wrapper);
    }

    default List<BusinessJobDO> getByForeignKey(List<String> foreignKeys) {
        LambdaQueryWrapper<BusinessJobDO> wrapper = Wrappers.lambdaQuery(BusinessJobDO.class)
                .in(BusinessJobDO::getForeignKey, foreignKeys);
        return selectList(wrapper);
    }


    default void decreaseNum(String uid) {
        LambdaUpdateWrapper<BusinessJobDO> wrapper = Wrappers.lambdaUpdate(BusinessJobDO.class)
                .eq(BusinessJobDO::getUid, uid)
                .setSql("remain_count = remain_count - 1");
        update(wrapper);
    }

}
