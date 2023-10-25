package com.starcloud.ops.business.listing.service;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictKeyPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictKeyPageRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;

import java.util.List;

public interface DictService {

    /**
     * 新建词库
     *
     * @param reqVO
     * @return
     */
    DictRespVO create(DictCreateReqVO reqVO);

    /**
     * 分页查询字典列表
     *
     * @param dictPageReqVO
     * @return
     */
    PageResult<DictRespVO> getDictPage(DictPageReqVO dictPageReqVO);

    /**
     * 删除字典
     *
     * @param uids
     */
    void deleteDict(List<String> uids);

    /**
     * 修改词库
     *
     * @param modifyReqVO
     * @return
     */
    void modify(DictModifyReqVO modifyReqVO);

    /**
     * 词库详情
     *
     * @param uid
     * @return
     */
    DictRespVO dictDetail(String uid);

    /**
     * 新增关键词
     *
     * @param uid
     * @param keys
     */
    void addKeyword(String uid, List<String> keys);


    /**
     * 删除关键词
     *
     * @param uid
     * @param keys
     */
    void removeKey(String uid, List<String> keys);

    /**
     * 分页查询关键词数据
     *
     * @param pageReqVO
     * @return
     */
    DictKeyPageRespVO queryMetaData(DictKeyPageReqVO pageReqVO);
}
