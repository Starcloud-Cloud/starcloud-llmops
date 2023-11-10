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
    DraftRespVO saveDraftVersion(DraftReqVO saveReqVO);

    /**
     * 批量导出草稿
     *
     * @param ids
     * @return
     */
    List<DraftDetailExcelVO> export(List<Long> ids);

    /**
     * 批量删除草稿
     *
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 绑定&分析关键词
     *
     * @param reqVO
     */
    void addKeyword(DraftOperationReqVO reqVO);

    /**
     * 批量执行
     *
     * @param ids
     */
    void batchExecute(List<Long> ids);

    /**
     * 移除关键词
     *
     * @param reqVO
     */
    void removeKeyword(DraftOperationReqVO reqVO);

    /**
     * 导入词库中的关键词
     *
     * @param reqVO
     */
    DraftRespVO importDict(ImportDictReqVO reqVO);

    /**
     * 计算得分
     *
     * @param reqVO
     */
    DraftRespVO score(DraftReqVO reqVO);

    /**
     * 克隆草稿
     *
     * @param reqVO
     * @return
     */
    DraftRespVO cloneDraft(DraftOperationReqVO reqVO);

    /**
     * 搜索词智能推荐
     *
     * @param uid
     * @param version
     * @return
     */
    String searchTermRecommend(String uid, Integer version);

    /**
     * 刷新关键词搜索量
     *
     * @param uid
     * @param version
     */
    void refresh(String uid, Integer version);
}
