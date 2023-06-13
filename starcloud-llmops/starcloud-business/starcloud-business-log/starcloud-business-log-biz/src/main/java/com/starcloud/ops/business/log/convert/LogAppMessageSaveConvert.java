package com.starcloud.ops.business.log.convert;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.messagesave.vo.*;
import com.starcloud.ops.business.log.controller.admin.LogAppMessageSaveExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应用执行日志结果保存 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageSaveConvert {

    LogAppMessageSaveConvert INSTANCE = Mappers.getMapper(LogAppMessageSaveConvert.class);

    LogAppMessageSaveDO convert(LogAppMessageSaveCreateReqVO bean);

    LogAppMessageSaveDO convert(LogAppMessageSaveUpdateReqVO bean);

    LogAppMessageSaveRespVO convert(LogAppMessageSaveDO bean);

    List<LogAppMessageSaveRespVO> convertList(List<LogAppMessageSaveDO> list);

    PageResult<LogAppMessageSaveRespVO> convertPage(PageResult<LogAppMessageSaveDO> page);

    List<LogAppMessageSaveExcelVO> convertList02(List<LogAppMessageSaveDO> list);

}