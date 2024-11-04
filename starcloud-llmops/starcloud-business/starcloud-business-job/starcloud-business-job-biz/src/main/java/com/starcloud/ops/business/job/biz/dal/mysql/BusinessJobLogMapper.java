package com.starcloud.ops.business.job.biz.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.LibraryJobLogPageReqVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BindAppDetail;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import com.starcloud.ops.business.job.biz.dal.dataobject.JobLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessJobLogMapper extends BaseMapperX<BusinessJobLogDO> {


    default PageResult<BusinessJobLogDO> page(JobLogPageReqVO pageReqVO) {
        LambdaQueryWrapper<BusinessJobLogDO> wrapper = Wrappers.lambdaQuery(BusinessJobLogDO.class)
                .eq(BusinessJobLogDO::getBusinessJobUid, pageReqVO.getBusinessJobUid())
                .orderByDesc(BusinessJobLogDO::getCreateTime);
        return selectPage(pageReqVO, wrapper);
    }

    default PageResult<BusinessJobLogDO> libraryPage(List<String> JobUidList, PageParam pageReqVO) {
        LambdaQueryWrapper<BusinessJobLogDO> wrapper = Wrappers.lambdaQuery(BusinessJobLogDO.class)
                .in(BusinessJobLogDO::getBusinessJobUid, JobUidList)
                .orderByDesc(BusinessJobLogDO::getCreateTime);
        return selectPage(pageReqVO, wrapper);
    }

    Long count();

    List<JobLogDTO> pluginLog(@Param("start") Integer start, @Param("size") Integer size);

    List<BindAppDetail> appDetail(@Param("libraryIdList")List<Long> libraryIdList);

}
