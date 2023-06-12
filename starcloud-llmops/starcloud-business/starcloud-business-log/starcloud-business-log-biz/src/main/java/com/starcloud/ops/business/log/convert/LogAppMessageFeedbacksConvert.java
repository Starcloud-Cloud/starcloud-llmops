package com.starcloud.ops.business.log.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.feedbacks.vo.*;
import com.starcloud.ops.business.log.controller.admin.LogAppMessageFeedbacksExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageFeedbacksDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应用执行日志结果反馈 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageFeedbacksConvert {

    LogAppMessageFeedbacksConvert INSTANCE = Mappers.getMapper(LogAppMessageFeedbacksConvert.class);

    LogAppMessageFeedbacksDO convert(LogAppMessageFeedbacksCreateReqVO bean);

    LogAppMessageFeedbacksDO convert(LogAppMessageFeedbacksUpdateReqVO bean);

    LogAppMessageFeedbacksRespVO convert(LogAppMessageFeedbacksDO bean);

    List<LogAppMessageFeedbacksRespVO> convertList(List<LogAppMessageFeedbacksDO> list);

    PageResult<LogAppMessageFeedbacksRespVO> convertPage(PageResult<LogAppMessageFeedbacksDO> page);

    List<LogAppMessageFeedbacksExcelVO> convertList02(List<LogAppMessageFeedbacksDO> list);

}