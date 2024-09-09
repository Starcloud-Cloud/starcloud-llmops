package com.starcloud.ops.business.mission.convert;


import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.note.*;
import com.starcloud.ops.business.mission.api.vo.response.PreSettlementRecordRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.XhsNoteDetailDO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Mapper
public interface XhsNoteDetailConvert {

    XhsNoteDetailConvert INSTANCE = Mappers.getMapper(XhsNoteDetailConvert.class);

    @Mapping(source = "description", target = "desc")
    XhsNoteDetailRespVO convert(XhsNoteDetailDO xhsNoteDetailDO);

    PageResult<PreSettlementRecordRespVO> convert(PageResult<XhsNoteDetailDO> result);

    default XhsNoteDetailDO convertDo(NoteDetail noteDetail) {
        XhsNoteDetailDO xhsNoteDetailDO = new XhsNoteDetailDO();
        update(noteDetail, xhsNoteDetailDO);
        return xhsNoteDetailDO;
    }


    default void update(NoteDetail noteDetail, XhsNoteDetailDO noteDetailDO) {
        if (noteDetail == null) {
            return;
        }
        noteDetailDO.setNoteId(noteDetail.getNoteId());
        noteDetailDO.setTitle(noteDetail.getTitle());
        noteDetailDO.setDescription(formatDesc(noteDetail.getDesc()));
        noteDetailDO.setImageList(JSONUtil.toJsonStr(noteDetail.getImageList()));
        noteDetailDO.setVideo(JSONUtil.toJsonStr(noteDetail.getVideo()));
        NoteInteractInfo interactInfo = noteDetail.getInteractInfo();
        if (interactInfo == null) {
            return;
        }
        noteDetailDO.setLikedCount(interactInfo.getLikedCount());
        noteDetailDO.setCollectedCount(interactInfo.getCollectedCount());
        noteDetailDO.setCommentCount(interactInfo.getCommentCount());
        noteDetailDO.setShareCount(interactInfo.getShareCount());
        noteDetailDO.setUpdateTime(LocalDateTime.now());
        noteDetailDO.setTagList(JSONUtil.toJsonStr(noteDetail.getTagList()));
    }

    default XhsNoteDetailRespVO convert(NoteDetail noteDetail) {
        if (noteDetail == null) {
            return null;
        }
        XhsNoteDetailRespVO respVO = new XhsNoteDetailRespVO();
        respVO.setNodeId(noteDetail.getNoteId());
        respVO.setTitle(noteDetail.getTitle());
        respVO.setDesc(noteDetail.getDesc());
        respVO.setImageList(noteDetail.getImageList());
        respVO.setVideo(noteDetail.getVideo());

//        NoteInteractInfo interactInfo = noteDetail.getInteractInfo();
//        if (interactInfo == null) {
//            return respVO;
//        }
//        respVO.setLikedCount(interactInfo.getLikedCount());
//        respVO.setCollectedCount(interactInfo.getCollectedCount());
//        respVO.setCommentCount(interactInfo.getCommentCount());
//        respVO.setShareCount(interactInfo.getShareCount());
        return respVO;
    }

    default String formatDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return StringUtils.EMPTY;
        }
        int index = desc.indexOf("#");
        if (index != -1) {
            return desc.substring(0, index);
        }
        return desc;
    }

    default List<NoteTag> toTag(String string) {
        if (StringUtils.isBlank(string)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(string).toList(NoteTag.class);
    }

    default List<NoteImage> toImage(String string) {
        if (StringUtils.isBlank(string)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(string).toList(NoteImage.class);
    }

    default NoteVideo toVideo(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.toBean(string, NoteVideo.class);
    }
}
