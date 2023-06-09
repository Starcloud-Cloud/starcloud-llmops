package com.starcloud.ops.business.log.convert;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.log.api.conversation.vo.*;
import com.starcloud.ops.business.log.controller.admin.LogAppConversationExcelVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 应用执行日志会话 Convert
 *
 * @author 芋道源码
 */
@Mapper(imports = {BigDecimal.class})
public interface LogAppConversationConvert {

    LogAppConversationConvert INSTANCE = Mappers.getMapper(LogAppConversationConvert.class);

    LogAppConversationDO convert(LogAppConversationCreateReqVO bean);

    LogAppConversationDO convert(LogAppConversationUpdateReqVO bean);

    LogAppConversationRespVO convert(LogAppConversationDO bean);

    List<LogAppConversationRespVO> convertList(List<LogAppConversationDO> list);

    PageResult<LogAppConversationRespVO> convertPage(PageResult<LogAppConversationDO> page);

    List<LogAppConversationExcelVO> convertList02(List<LogAppConversationDO> list);

    @Mappings({
            @Mapping(target = "totalElapsed",
                    expression = "java( infoPO.getTotalElapsed().divide(new BigDecimal(1000)))")

    })
    LogAppConversationInfoRespVO convertInfoPO(LogAppConversationInfoPO infoPO);



    PageResult<LogAppConversationInfoRespVO> convertInfoPage(PageResult<LogAppConversationInfoPO> page);



    @Mappings({
            @Mapping(target = "elapsedTotal",
                    expression = "java( statisticsListPO.getElapsedTotal().divide(new BigDecimal(1000)))"),
            @Mapping(target = "elapsedAvg",
                    expression = "java( statisticsListPO.getElapsedAvg().divide(new BigDecimal(1000)))")
    })
    LogAppMessageStatisticsListVO convertStatistics(LogAppMessageStatisticsListPO statisticsListPO);


    List<LogAppMessageStatisticsListVO> convertStatisticsList(List<LogAppMessageStatisticsListPO> page);


}