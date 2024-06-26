package com.starcloud.ops.business.app.service.xhs.crawler.impl;

import com.starcloud.ops.business.app.api.xhs.note.ImageInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 小红书原图片地址会触发反爬 转存oss
 */
@Slf4j
@Component
public class XhsDumpServiceImpl extends XhsNoteDetailWrapperImpl {

    public static final String RELATIVE_PATH = "material" + File.separator + "xhs";

    public ServerRequestInfo requestDetail(String noteId) {

        ServerRequestInfo serverRequestInfo = super.requestDetail(noteId);

        if (!CollectionUtils.isEmpty(serverRequestInfo.getNoteDetail().getImageList())) {
            for (NoteImage noteImage : serverRequestInfo.getNoteDetail().getImageList()) {
                String traceId = noteImage.getTraceId();
                List<ImageInfo> infoList = noteImage.getInfoList();
                if (CollectionUtils.isEmpty(infoList)) {
                    continue;
                }
                for (int i = 0; i < infoList.size(); i++) {
                    ImageInfo imageInfo = infoList.get(i);
                    if (!"WB_DFT".equalsIgnoreCase(imageInfo.getImageScene())) {
                        continue;
                    }
                    String imageName = traceId + "-" + i;
                    String url = imageInfo.getUrl();
                    String ossUrl = ImageUploadUtils.dumpToOss(url, imageName, RELATIVE_PATH);
                    infoList.get(i).setUrl(ossUrl);
                }
            }
        }
        return serverRequestInfo;

    }
}
