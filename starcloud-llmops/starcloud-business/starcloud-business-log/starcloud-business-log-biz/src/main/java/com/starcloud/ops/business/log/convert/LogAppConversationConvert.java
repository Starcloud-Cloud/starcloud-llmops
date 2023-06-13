package com.starcloud.ops.business.log.convert;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.log.api.conversation.vo.*;
import com.starcloud.ops.business.log.controller.admin.LogAppConversationExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 应用执行日志会话 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppConversationConvert {

    LogAppConversationConvert INSTANCE = Mappers.getMapper(LogAppConversationConvert.class);

    LogAppConversationDO convert(LogAppConversationCreateReqVO bean);

    LogAppConversationDO convert(LogAppConversationUpdateReqVO bean);

    LogAppConversationRespVO convert(LogAppConversationDO bean);

    List<LogAppConversationRespVO> convertList(List<LogAppConversationDO> list);

    PageResult<LogAppConversationRespVO> convertPage(PageResult<LogAppConversationDO> page);

    List<LogAppConversationExcelVO> convertList02(List<LogAppConversationDO> list);

}