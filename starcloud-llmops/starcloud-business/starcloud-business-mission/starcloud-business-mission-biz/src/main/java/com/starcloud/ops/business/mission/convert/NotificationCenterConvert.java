package com.starcloud.ops.business.mission.convert;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dto.PostingUnitPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Mapper
public interface NotificationCenterConvert {

    NotificationCenterConvert INSTANCE = Mappers.getMapper(NotificationCenterConvert.class);

    NotificationCenterDO convert(NotificationCreateReqVO createMissionReqVO);

    PageResult<NotificationRespVO> convert(PageResult<NotificationCenterDO> page);

    NotificationRespVO convert(NotificationCenterDO centerDO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(NotificationModifyReqVO reqVO, @MappingTarget NotificationCenterDO centerDO);

    default LocalDateTime format(String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY);
        return LocalDate.parse(time, dateTimeFormatter).atStartOfDay();
    }


    default String toStr(PostingUnitPriceDTO unitPriceDTO) {
        return JSONUtil.toJsonStr(unitPriceDTO);
    }

    default PostingUnitPriceDTO toPrice(String str) {
        return JSONUtil.toBean(str, PostingUnitPriceDTO.class);
    }
}