package com.starcloud.ops.business.mission.service.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.crawler.XhsNoteDetailWrapper;
import com.starcloud.ops.business.app.service.xhs.crawler.impl.XhsNoteDetailWrapperImpl;
import com.starcloud.ops.business.mission.api.vo.request.PreSettlementRecordReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.convert.XhsNoteDetailConvert;
import com.starcloud.ops.business.mission.dal.dataobject.XhsNoteDetailDO;
import com.starcloud.ops.business.mission.dal.mysql.XhsNoteDetailMapper;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
public class XhsNoteDetailServiceImpl implements XhsNoteDetailService {

    @Resource
    private XhsNoteDetailMapper noteDetailMapper;

    @Resource(name = "xhsNoteDetailWrapperImpl")
    private XhsNoteDetailWrapperImpl xhsNoteDetailWrapper;

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
        String noteId = XhsDetailConstants.parsingNoteId(noteUrl);
        return selectByNoteId(noteId);
    }

    @Override
    public AbstractCreativeMaterialDTO mapMaterialDetail(String noteUrl, String materialType) {
        XhsNoteDetailRespVO respVO = selectByNoteUrl(noteUrl);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", materialType);
        jsonObject.put("title", respVO.getTitle());
        jsonObject.put("content", respVO.getDesc());
        jsonObject.put("link", noteUrl);
        return JsonUtils.parseObject(jsonObject.toJSONString(), AbstractCreativeMaterialDTO.class);
    }

    @Override
    public XhsNoteDetailRespVO remoteDetail(String noteUrl) {
        XhsDetailConstants.validNoteUrl(noteUrl);
        String noteId = XhsDetailConstants.parsingNoteId(noteUrl);
        ServerRequestInfo requestInfo = xhsNoteDetailWrapper.requestDetail(noteId);
        return XhsNoteDetailConvert.INSTANCE.convert(requestInfo.getNoteDetail());
    }

    @Override
    public XhsNoteDetailRespVO preSettlementByUrl(String missionUid, String noteUrl, SingleMissionPostingPriceDTO unitPriceDTO) {
        XhsDetailConstants.validNoteUrl(noteUrl);
        String noteId = XhsDetailConstants.parsingNoteId(noteUrl);
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

    @Override
    public XhsNoteDetailDO getById(Long id) {
        return noteDetailMapper.selectById(id);
    }

    @Override
    public PageResult<XhsNoteDetailDO> preSettlementRecord(PreSettlementRecordReqVO reqVO) {
        return noteDetailMapper.page(reqVO);
    }

    @Override
    public void abandonMission(String missionUid) {
        noteDetailMapper.deleteByMissionUid(missionUid);
    }
}
