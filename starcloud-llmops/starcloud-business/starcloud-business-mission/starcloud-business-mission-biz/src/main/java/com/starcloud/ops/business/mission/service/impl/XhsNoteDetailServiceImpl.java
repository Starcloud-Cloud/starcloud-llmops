package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.convert.XhsNoteDetailConvert;
import com.starcloud.ops.business.mission.dal.dataobject.XhsNoteDetailDO;
import com.starcloud.ops.business.mission.dal.mysql.XhsNoteDetailMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.crawler.XhsNoteDetailWrapper;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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
    public XhsNoteDetailRespVO preSettlementByUrl(String missionUid, String noteUrl, SingleMissionPostingPriceDTO unitPriceDTO) {
        XhsDetailConstants.validNoteUrl(noteUrl);
        String noteId = ReUtil.delAll(XhsDetailConstants.DOMAIN, noteUrl);
        return preSettlementByNoteId(missionUid, noteId, unitPriceDTO);
    }

    @Override
    public XhsNoteDetailRespVO preSettlementByNoteId(String missionUid, String noteId, SingleMissionPostingPriceDTO unitPriceDTO) {
        ServerRequestInfo requestInfo = xhsNoteDetailWrapper.requestDetail(noteId);
        XhsNoteDetailDO xhsNoteDetailDO = XhsNoteDetailConvert.INSTANCE.convertDo(requestInfo.getNoteDetail());
        // 结算时间 金额
        BigDecimal amount = unitPriceDTO.calculationAmount(xhsNoteDetailDO.getLikedCount(), xhsNoteDetailDO.getCommentCount());
        xhsNoteDetailDO.setAmount(amount);
        xhsNoteDetailDO.setUnitPrice(SingleMissionConvert.INSTANCE.toStr(unitPriceDTO));
        xhsNoteDetailDO.setMissionUid(missionUid);
        noteDetailMapper.insert(xhsNoteDetailDO);
        return XhsNoteDetailConvert.INSTANCE.convert(xhsNoteDetailDO);
    }

    @Override
    public XhsNoteDetailRespVO preSettlement(String missionUid, Integer likedCount, Integer commentCount, SingleMissionPostingPriceDTO unitPriceDTO) {
        BigDecimal amount = unitPriceDTO.calculationAmount(likedCount, commentCount);
        XhsNoteDetailDO xhsNoteDetailDO = new XhsNoteDetailDO();
        xhsNoteDetailDO.setAmount(amount);
        xhsNoteDetailDO.setUnitPrice(SingleMissionConvert.INSTANCE.toStr(unitPriceDTO));
        xhsNoteDetailDO.setNoteId(StringUtils.EMPTY);
        xhsNoteDetailDO.setTitle(StringUtils.EMPTY);
        xhsNoteDetailDO.setDescription(StringUtils.EMPTY);
        xhsNoteDetailDO.setTitle(StringUtils.EMPTY);
        xhsNoteDetailDO.setLikedCount(likedCount);
        xhsNoteDetailDO.setCommentCount(commentCount);
        xhsNoteDetailDO.setMissionUid(missionUid);
        noteDetailMapper.insert(xhsNoteDetailDO);
        return XhsNoteDetailConvert.INSTANCE.convert(xhsNoteDetailDO);
    }
}
