package com.starcloud.ops.business.mission.convert;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.dto.PostingContentDTO;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SingleMissionConvert {

    SingleMissionConvert INSTANCE = Mappers.getMapper(SingleMissionConvert.class);


    PageResult<SingleMissionRespVO> convert(PageResult<SingleMissionDO> page);

    SingleMissionRespVO convert(SingleMissionDO singleMissionDO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(SingleMissionModifyReqVO reqVO, @MappingTarget SingleMissionDO singleMissionDO);


    default SingleMissionDO convert(XhsCreativeContentResp creativeContentResp, NotificationCenterDO notificationCenterDO) {
        if (creativeContentResp == null) {
            return null;
        }
        SingleMissionDO singleMissionDO = new SingleMissionDO();
        singleMissionDO.setUid(IdUtil.fastSimpleUUID());
        singleMissionDO.setNotificationUid(notificationCenterDO.getUid());
        singleMissionDO.setCreativeUid(creativeContentResp.getBusinessUid());
        singleMissionDO.setType(notificationCenterDO.getType());
        PostingContentDTO postingContentDTO = new PostingContentDTO();
        postingContentDTO.setTitle(creativeContentResp.getCopyWritingTitle());
        postingContentDTO.setText(creativeContentResp.getCopyWritingContent());
        postingContentDTO.setPicture(creativeContentResp.getPictureContent());
        singleMissionDO.setContent(toStr(postingContentDTO));
        singleMissionDO.setStatus(SingleMissionStatusEnum.init.getCode());
        return singleMissionDO;
    }

    default String toStr(PostingContentDTO postingContentDTO) {
        return JSONUtil.toJsonStr(postingContentDTO);
    }

    default PostingContentDTO toPostingContent(String string) {
        return JSONUtil.toBean(string, PostingContentDTO.class);
    }
}
