package com.starcloud.ops.business.log.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.controller.admin.LogAppMessageExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应用执行日志结果 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageConvert {

    LogAppMessageConvert INSTANCE = Mappers.getMapper(LogAppMessageConvert.class);

    LogAppMessageDO convert(LogAppMessageCreateReqVO bean);

    LogAppMessageDO convert(LogAppMessageUpdateReqVO bean);

    LogAppMessageRespVO convert(LogAppMessageDO bean);

    LogAppMessageInfoRespVO convertInfo(LogAppMessageDO bean);

    List<LogAppMessageRespVO> convertList(List<LogAppMessageDO> list);

    PageResult<LogAppMessageRespVO> convertPage(PageResult<LogAppMessageDO> page);

    Page<LogAppMessageRespVO> convertPage(Page<LogAppMessageDO> page);

    List<LogAppMessageExcelVO> convertList02(List<LogAppMessageDO> list);

}