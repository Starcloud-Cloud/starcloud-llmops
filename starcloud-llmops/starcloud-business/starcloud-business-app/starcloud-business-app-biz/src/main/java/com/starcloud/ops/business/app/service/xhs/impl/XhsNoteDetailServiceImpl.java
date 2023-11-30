package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.util.ReUtil;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.convert.xhs.XhsNoteDetailConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsNoteDetailDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsNoteDetailMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.XhsNoteDetailService;
import com.starcloud.ops.business.app.service.xhs.XhsNoteDetailWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_URL_ERROR;

@Slf4j
@Service
public class XhsNoteDetailServiceImpl implements XhsNoteDetailService {

    @Resource
    private XhsNoteDetailMapper noteDetailMapper;

    @Resource
    private XhsNoteDetailWrapper xhsNoteDetailWrapper;


    @Override
    public XhsNoteDetailRespVO selectByNoteId(String noteId) {
        XhsNoteDetailDO xhsNoteDetailDO = noteDetailMapper.selectByNoteId(noteId);
        if (xhsNoteDetailDO == null) {
            ServerRequestInfo requestInfo = xhsNoteDetailWrapper.requestDetail(noteId);
            xhsNoteDetailDO = XhsNoteDetailConvert.INSTANCE.convertDo(requestInfo.getNoteDetail());
            noteDetailMapper.insert(xhsNoteDetailDO);
        }
        return XhsNoteDetailConvert.INSTANCE.convert(xhsNoteDetailDO);
    }

    @Override
    public XhsNoteDetailRespVO selectByNoteUrl(String noteUrl) {
        XhsDetailConstants.validNoteUrl(noteUrl);
        String noteId = ReUtil.delAll(XhsDetailConstants.DOMAIN, noteUrl);
        return selectByNoteId(noteId);
    }

    @Override
    public XhsNoteDetailRespVO refreshByNoteUrl(String noteUrl) {
        XhsDetailConstants.validNoteUrl(noteUrl);
        String noteId = ReUtil.delAll(XhsDetailConstants.DOMAIN, noteUrl);
        return refreshByNoteId(noteId);
    }

    @Override
    public XhsNoteDetailRespVO refreshByNoteId(String noteId) {
        XhsNoteDetailDO xhsNoteDetailDO = noteDetailMapper.selectByNoteId(noteId);
        ServerRequestInfo requestInfo = xhsNoteDetailWrapper.requestDetail(noteId);
        if (xhsNoteDetailDO == null) {
            xhsNoteDetailDO = XhsNoteDetailConvert.INSTANCE.convertDo(requestInfo.getNoteDetail());
            noteDetailMapper.insert(xhsNoteDetailDO);
        } else {
            XhsNoteDetailConvert.INSTANCE.update(requestInfo.getNoteDetail(), xhsNoteDetailDO);
            noteDetailMapper.updateById(xhsNoteDetailDO);
        }
        return XhsNoteDetailConvert.INSTANCE.convert(xhsNoteDetailDO);
    }

}
