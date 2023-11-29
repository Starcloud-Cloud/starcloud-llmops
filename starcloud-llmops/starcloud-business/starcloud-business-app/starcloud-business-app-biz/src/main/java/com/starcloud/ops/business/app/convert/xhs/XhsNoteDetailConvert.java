package com.starcloud.ops.business.app.convert.xhs;


import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.xhs.note.NoteDetail;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.NoteInteractInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteVideo;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsNoteDetailDO;
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

    @Mapping(source = "description",target = "desc")
    XhsNoteDetailRespVO convert(XhsNoteDetailDO xhsNoteDetailDO);

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
        noteDetailDO.setDescription(noteDetail.getDesc());
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

        NoteInteractInfo interactInfo = noteDetail.getInteractInfo();
        if (interactInfo == null) {
            return respVO;
        }
        respVO.setLikedCount(interactInfo.getLikedCount());
        respVO.setCollectedCount(interactInfo.getCollectedCount());
        respVO.setCommentCount(interactInfo.getCommentCount());
        respVO.setShareCount(interactInfo.getShareCount());
        return respVO;
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
