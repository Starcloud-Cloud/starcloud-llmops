package com.starcloud.ops.business.app.convert.xhs.batch;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CreativePlanBatchConvert {

    CreativePlanBatchConvert INSTANCE = Mappers.getMapper(CreativePlanBatchConvert.class);

    PageResult<CreativePlanBatchRespVO> convert(PageResult<CreativePlanBatchDO> pageResult);

    List<CreativePlanBatchRespVO> convert(List<CreativePlanBatchDO> planBatchDOList);
}
