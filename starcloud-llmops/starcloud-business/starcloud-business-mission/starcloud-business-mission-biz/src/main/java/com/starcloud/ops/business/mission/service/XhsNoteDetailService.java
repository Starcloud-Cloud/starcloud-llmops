package com.starcloud.ops.business.mission.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.mission.api.vo.request.PreSettlementRecordReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.XhsNoteDetailDO;

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
     *
     * @param noteUrl
     * @param materialType 素材类型
     * @return
     */
    AbstractCreativeMaterialDTO mapMaterialDetail(String noteUrl, String materialType);

    /**
     * 实时数据
     */
    XhsNoteDetailRespVO remoteDetail(String noteUrl);

    /**
     * 爬取新数据预结算
     *
     * @param noteId 小红书笔记id
     */
    XhsNoteDetailRespVO preSettlementByNoteId(String missionUid, String noteId, SingleMissionPostingPriceDTO unitPriceDTO);

    /**
     * 爬取新数据预结算
     *
     * @param noteUrl 小红书访问url
     */
    XhsNoteDetailRespVO preSettlementByUrl(String missionUid, String noteUrl, SingleMissionPostingPriceDTO unitPriceDTO);

    /**
     * 预结算
     */
    XhsNoteDetailRespVO preSettlement(String missionUid, Integer likedCount, Integer commentCount, SingleMissionPostingPriceDTO unitPriceDTO);

    /**
     * 预结算数据
     */
    XhsNoteDetailDO getById(Long id);

    /**
     * 预结算记录
     */
    PageResult<XhsNoteDetailDO> preSettlementRecord(PreSettlementRecordReqVO reqVO);

    /**
     * 取消认领
     */
    void abandonMission(String missionUid);
}
