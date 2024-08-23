package com.starcloud.ops.business.job.biz.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessJobLogMapper extends BaseMapperX<BusinessJobLogDO> {


    default PageResult<BusinessJobLogDO> page(JobLogPageReqVO pageReqVO) {
        LambdaQueryWrapper<BusinessJobLogDO> wrapper = Wrappers.lambdaQuery(BusinessJobLogDO.class)
                .eq(BusinessJobLogDO::getBusinessJobUid, pageReqVO.getBusinessJobUid())
                .orderByDesc(BusinessJobLogDO::getCreateTime)
                ;
        return selectPage(pageReqVO,wrapper);
    }
}
