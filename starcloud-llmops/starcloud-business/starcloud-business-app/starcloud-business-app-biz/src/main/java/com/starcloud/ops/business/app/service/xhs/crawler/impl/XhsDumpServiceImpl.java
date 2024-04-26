package com.starcloud.ops.business.app.service.xhs.crawler.impl;

import cn.hutool.core.io.IoUtil;
import com.starcloud.ops.business.app.api.xhs.note.ImageInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

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
                    String imageName = traceId + "-" + i + ".jpg";
                    String url = imageInfo.getUrl();
                    String ossUrl = dumpToOss(url, imageName);
                    infoList.get(i).setUrl(ossUrl);
                }
            }
        }
        return serverRequestInfo;

    }

    private String dumpToOss(String xhsUrl, String imageName) {
        InputStream inputStream = null;
        try {
            URL url = new URL(xhsUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            inputStream = urlConnection.getInputStream();

            return ImageUploadUtils.uploadImage(imageName, RELATIVE_PATH, IoUtil.readBytes(inputStream)).getUrl();
        } catch (Exception e) {
            log.info("dump to oss error", e);
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.info("input close error", e);
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
