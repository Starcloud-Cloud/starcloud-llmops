package com.starcloud.ops.business.app.convert.xhs.material;

import cn.hutool.core.bean.BeanPath;
import cn.smallbun.screw.core.util.CollectionUtils;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.api.xhs.note.ImageInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteDetail;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.NoteTag;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

@Mapper
public interface XhsNoteConvert {

    XhsNoteConvert INSTANCE = Mappers.getMapper(XhsNoteConvert.class);

    default XhsNoteDTO convert(NoteDetail noteDetail) {
        XhsNoteDTO xhsNoteDTO = new XhsNoteDTO();
        if (Objects.isNull(noteDetail)) {
            return xhsNoteDTO;
        }

        xhsNoteDTO.setContent(noteDetail.getDesc());
        xhsNoteDTO.setTitle(noteDetail.getTitle());
        xhsNoteDTO.setNoteId(noteDetail.getNoteId());
        List<NoteTag> tagList = noteDetail.getTagList();
        if (CollectionUtils.isNotEmpty(tagList)) {
            String tags = tagList.stream().map(NoteTag::getName).collect(Collectors.joining(","));
            xhsNoteDTO.setTags(tags);
        }
        List<NoteImage> imageList = noteDetail.getImageList();
        List<String> dumpUrls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(imageList)) {
            for (NoteImage noteImage : imageList) {
                List<ImageInfo> infoList = noteImage.getInfoList();
                if (CollectionUtils.isEmpty(infoList)) {
                    continue;
                }
                Optional<ImageInfo> info = infoList.stream().filter(imageInfo -> "WB_DFT".equalsIgnoreCase(imageInfo.getImageScene())).findAny();
                if (info.isPresent() && StringUtils.isNotBlank(info.get().getUrl())) {
                    dumpUrls.add(info.get().getUrl());
                }
            }
        }
        xhsNoteDTO.addImage(dumpUrls);
        return xhsNoteDTO;
    }

    default Map<String, Object> convert(XhsNoteDTO xhsNoteDTO, Map<String, String> fieldMap) {
        Map<String, Object> result = new HashMap<>();
        if (CollectionUtils.isEmpty(fieldMap)) {
            return result;
        }

        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            BeanPath xhsNotePath = new BeanPath(entry.getKey());
            BeanPath resultPath = new BeanPath(entry.getValue());
            resultPath.set(result, xhsNotePath.get(xhsNoteDTO));
        }
        return result;
    }

    default Map<String, Object> convert(NoteDetail noteDetail, Map<String, String> fieldMap) {
        XhsNoteDTO xhsNoteDTO = convert(noteDetail);
        return convert(xhsNoteDTO, fieldMap);
    }

}
