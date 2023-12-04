package com.starcloud.ops.business.app.service.xhs.crawler;

import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;

public interface XhsNoteDetailWrapper {

    /**
     * @param noteId 小红书noteId
     */
    ServerRequestInfo requestDetail(String noteId);
}
