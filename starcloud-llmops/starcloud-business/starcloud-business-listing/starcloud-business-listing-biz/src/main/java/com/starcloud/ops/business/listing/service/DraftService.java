package com.starcloud.ops.business.listing.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.*;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;

import java.util.List;

public interface DraftService {
    /**
     * 新建草稿
     *
     * @param reqVO
     * @return
     */
    DraftRespVO create(DraftCreateReqVO reqVO);

    /**
     * 分页查询最高版本草稿
     *
     * @param draftPageReqVO
     * @return
     */
    PageResult<DraftRespVO> getDraftPage(DraftPageReqVO draftPageReqVO);

    /**
     * 查询草稿的版本列表
     *
     * @param uid
     * @return
     */
    List<DraftRespVO> listVersion(String uid);

    /**
     * 查询指定版本详情
     *
     * @param uid
     * @param version
     * @return
     */
    DraftRespVO detail(String uid, Integer version);

    /**
     * 保存一个新版本
     * 不包含关键词
     *
     * @param saveReqVO
     */
    void saveDraft(DraftSaveReqVO saveReqVO);

    /**
     * 批量导出草稿
     *
     * @param operationReq
     * @return
     */
    List<DraftDetailExcelVO> export(List<DraftOperationReqVO> operationReq);

    /**
     * 批量删除草稿
     *
     * @param operationReq
     */
    void delete(List<DraftOperationReqVO> operationReq);

    /**
     * 绑定&分析关键词
     *
     * @param reqVO
     */
    void addKeyword(DraftSaveReqVO reqVO);

    /**
     * 批量执行
     *
     * @param operationReq
     */
    void batchExecute(List<DraftOperationReqVO> operationReq);
}
