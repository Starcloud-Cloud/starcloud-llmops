package com.starcloud.ops.business.app.dal.mysql.xhs.batch;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreativePlanBatchMapper extends BaseMapperX<CreativePlanBatchDO> {

    default CreativePlanBatchDO selectBatch(String planUid, Long batch) {
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class)
                .eq(CreativePlanBatchDO::getPlanUid, planUid)
                .eq(CreativePlanBatchDO::getBatch, batch);
        return selectOne(wrapper);
    }

    default PageResult<CreativePlanBatchDO> page(CreativePlanBatchPageReqVO pageReqVO) {
        LambdaQueryWrapper<CreativePlanBatchDO> wrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class)
                .eq(CreativePlanBatchDO::getPlanUid, pageReqVO.getPlanUid())
                .orderByDesc(CreativePlanBatchDO::getBatch)
                ;
        return selectPage(pageReqVO,wrapper);
    }

    List<CreativePlanBatchDO> latestBatch(@Param("planUidList") List<String> planUidList);
}
