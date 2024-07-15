package com.starcloud.ops.business.mission.convert;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.api.vo.response.AppSingleMissionRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingContentDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingUnitPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionDetailVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDTO;
import com.starcloud.ops.business.mission.dal.dataobject.XhsNoteDetailDO;
import com.starcloud.ops.business.mission.task.XhsTaskContentParams;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Mapper
public interface SingleMissionConvert {

    SingleMissionConvert INSTANCE = Mappers.getMapper(SingleMissionConvert.class);

    SingleMissionRespVO convert(SingleMissionDO singleMissionDO);

    SingleMissionDetailVO convertDetail(MissionNotificationDTO singleMissionDO);

    List<SingleMissionExportVO> convert(List<SingleMissionDO> missionList);

    List<SingleMissionRespVO> pageConvert(List<SingleMissionDTO> singleMissionList);

    SingleMissionRespVO convert(SingleMissionDTO dto);

    PageResult<AppSingleMissionRespVO> convert(PageResult<SingleMissionDO> doPageResult);

    @Mapping(target = "unitPrice", source = "singleMissionDO.unitPrice")
    @Mapping(target = "updateTime", source = "singleMissionDO.updateTime")
    AppSingleMissionRespVO appConvert(SingleMissionDO singleMissionDO, XhsNoteDetailDO noteDetailDO);

    List<AppSingleMissionRespVO> appConvert(List<MissionNotificationDTO> missionDOList);

    AppSingleMissionRespVO appConvert(MissionNotificationDTO singleMissionDO);

    AppSingleMissionRespVO appConvert(SingleMissionDO singleMissionDO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(SingleMissionModifyReqVO reqVO, @MappingTarget SingleMissionDO singleMissionDO);

    SingleMissionQueryReqVO convert(XhsTaskContentParams params);

    default SingleMissionPostingPriceDTO convert(NotificationCenterDO notificationCenterDO) {
        PostingUnitPriceDTO price = NotificationCenterConvert.INSTANCE.toPrice(notificationCenterDO.getUnitPrice());
        SingleMissionPostingPriceDTO priceDTO = new SingleMissionPostingPriceDTO();
        priceDTO.setSingleBudget(notificationCenterDO.getSingleBudget());
        priceDTO.setNotificationBudget(notificationCenterDO.getNotificationBudget());
        priceDTO.setLikeUnitPrice(price.getLikeUnitPrice());
        priceDTO.setReplyUnitPrice(price.getReplyUnitPrice());
        priceDTO.setPostingUnitPrice(price.getPostingUnitPrice());
        return priceDTO;
    }

    default SingleMissionDO convert(CreativeContentRespVO creativeContentResp, NotificationCenterDO notificationCenterDO) {
        if (creativeContentResp == null) {
            return null;
        }
        SingleMissionDO singleMissionDO = new SingleMissionDO();
        singleMissionDO.setUid(IdUtil.fastSimpleUUID());
        singleMissionDO.setNotificationUid(notificationCenterDO.getUid());
        singleMissionDO.setCreativeUid(creativeContentResp.getUid());
        singleMissionDO.setCreativePlanUid(creativeContentResp.getPlanUid());
        singleMissionDO.setType(notificationCenterDO.getType());
        PostingContentDTO postingContentDTO = new PostingContentDTO();
        CreativeContentExecuteResult response = creativeContentResp.getExecuteResult();
        postingContentDTO.setTitle(response.getCopyWriting().getTitle());
        postingContentDTO.setText(response.getCopyWriting().getContent());
        postingContentDTO.setPicture(response.getImageList());

        singleMissionDO.setContent(toStr(postingContentDTO));
        singleMissionDO.setStatus(SingleMissionStatusEnum.init.getCode());
        return singleMissionDO;
    }

    default String toStr(SingleMissionPostingPriceDTO postingPriceDTO) {
        return JSONUtil.toJsonStr(postingPriceDTO);
    }

    default SingleMissionPostingPriceDTO toPriceDTO(String str) {
        return JSONUtil.toBean(str, SingleMissionPostingPriceDTO.class);
    }

    default String toStr(PostingContentDTO postingContentDTO) {
        return JSON.toJSONString(postingContentDTO);
    }

    default String format(LocalDateTime time) {
        if (time == null) {
            return StringUtils.EMPTY;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
        return time.format(dateTimeFormatter);
    }

    default PostingContentDTO toPostingContent(String string) {
        return JSON.parseObject(string, PostingContentDTO.class);
    }

}
