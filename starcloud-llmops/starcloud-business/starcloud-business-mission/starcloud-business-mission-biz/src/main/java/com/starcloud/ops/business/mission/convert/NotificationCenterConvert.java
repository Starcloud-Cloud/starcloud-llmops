package com.starcloud.ops.business.mission.convert;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingUnitPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Mapper
public interface NotificationCenterConvert {

    NotificationCenterConvert INSTANCE = Mappers.getMapper(NotificationCenterConvert.class);

    NotificationCenterDO convert(NotificationCreateReqVO createMissionReqVO);

    PageResult<NotificationRespVO> convert(PageResult<NotificationCenterDO> page);

    NotificationRespVO convert(NotificationCenterDO centerDO);

    List<NotificationRespVO> convert(List<NotificationCenterDTO> centerDTOList);

    NotificationRespVO convert(NotificationCenterDTO centerDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(NotificationModifyReqVO reqVO, @MappingTarget NotificationCenterDO centerDO);

    default LocalDateTime format(String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY);
        return LocalDate.parse(time, dateTimeFormatter).atStartOfDay();
    }


    default String toStr(PostingUnitPriceDTO unitPriceDTO) {
        BigDecimal likeUnitPrice = Optional.ofNullable(unitPriceDTO.getLikeUnitPrice()).orElse(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP);
        BigDecimal replyUnitPrice = Optional.ofNullable(unitPriceDTO.getReplyUnitPrice()).orElse(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP);
        BigDecimal postingUnitPrice = Optional.ofNullable(unitPriceDTO.getPostingUnitPrice()).orElse(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP);
        unitPriceDTO.setLikeUnitPrice(likeUnitPrice);
        unitPriceDTO.setReplyUnitPrice(replyUnitPrice);
        unitPriceDTO.setPostingUnitPrice(postingUnitPrice);
        return JSONUtil.toJsonStr(unitPriceDTO);
    }

    default PostingUnitPriceDTO toPrice(String str) {
        return JSONUtil.toBean(str, PostingUnitPriceDTO.class);
    }
}
