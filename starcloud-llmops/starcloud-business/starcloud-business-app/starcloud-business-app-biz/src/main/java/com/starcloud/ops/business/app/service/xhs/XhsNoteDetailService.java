package com.starcloud.ops.business.app.service.xhs;

import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;

public interface XhsNoteDetailService {

    /**
     * @param noteId 小红书笔记id
     */
    XhsNoteDetailRespVO selectByNoteId(String noteId);

    /**
     * @param noteUrl 小红书访问url
     */
    XhsNoteDetailRespVO selectByNoteUrl(String noteUrl);

    /**
     * 刷新并返回实时数据
     *
     * @param noteId 小红书笔记id
     */
    XhsNoteDetailRespVO refreshByNoteId(String noteId);

    /**
     * 刷新并返回实时数据
     *
     * @param noteUrl 小红书访问url
     */
    XhsNoteDetailRespVO refreshByNoteUrl(String noteUrl);
}
