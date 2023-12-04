package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;

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
     * 爬取新数据预结算
     *
     * @param noteId 小红书笔记id
     */
    XhsNoteDetailRespVO preSettlementByNoteId(String noteId, SingleMissionPostingPriceDTO unitPriceDTO);

    /**
     * 爬取新数据预结算
     *
     * @param noteUrl 小红书访问url
     */
    XhsNoteDetailRespVO preSettlementByUrl(String noteUrl, SingleMissionPostingPriceDTO unitPriceDTO);

    /**
     * 预结算
     */
    XhsNoteDetailRespVO preSettlement(Integer likedCount, Integer commentCount,SingleMissionPostingPriceDTO unitPriceDTO);
}
