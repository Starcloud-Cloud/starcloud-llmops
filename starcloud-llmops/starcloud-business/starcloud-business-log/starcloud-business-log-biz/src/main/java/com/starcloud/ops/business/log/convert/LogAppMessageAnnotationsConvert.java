package com.starcloud.ops.business.log.convert;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.annotations.vo.*;
import com.starcloud.ops.business.log.controller.admin.LogAppMessageAnnotationsExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * 应用执行日志结果反馈标注 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageAnnotationsConvert {

    LogAppMessageAnnotationsConvert INSTANCE = Mappers.getMapper(LogAppMessageAnnotationsConvert.class);

    LogAppMessageAnnotationsDO convert(LogAppMessageAnnotationsCreateReqVO bean);

    LogAppMessageAnnotationsDO convert(LogAppMessageAnnotationsUpdateReqVO bean);

    LogAppMessageAnnotationsRespVO convert(LogAppMessageAnnotationsDO bean);

    List<LogAppMessageAnnotationsRespVO> convertList(List<LogAppMessageAnnotationsDO> list);

    PageResult<LogAppMessageAnnotationsRespVO> convertPage(PageResult<LogAppMessageAnnotationsDO> page);

    List<LogAppMessageAnnotationsExcelVO> convertList02(List<LogAppMessageAnnotationsDO> list);

}