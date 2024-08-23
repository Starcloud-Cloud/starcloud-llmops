package com.starcloud.ops.business.job.biz.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import org.apache.ibatis.annotations.Mapper;

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

}
