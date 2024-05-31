package com.starcloud.ops.biz.service.elementtype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeRespVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.elementtype.ElementtypeDO;

import javax.validation.Valid;

/**
 * 海报元素类型 Service 接口
 *
 * @author xhsadmin
 */
public interface ElementTypeService {

    /**
     * 创建海报元素类型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ElementTypeRespVO createElementtype(@Valid ElementTypeSaveReqVO createReqVO);

    /**
     * 更新海报元素类型
     *
     * @param updateReqVO 更新信息
     */
    void updateElementtype(@Valid ElementTypeSaveReqVO updateReqVO);

    /**
     * 删除海报元素类型
     *
     * @param uid 编号
     */
    void deleteElementtype(String uid);

    /**
     * 获得海报元素类型
     *
     * @param uid 编号
     * @return 海报元素类型
     */
    ElementtypeDO getElementtype(String uid);

    /**
     * 获得海报元素类型分页
     *
     * @param pageReqVO 分页查询
     * @return 海报元素类型分页
     */
    PageResult<ElementtypeDO> getElementtypePage(ElementTypePageReqVO pageReqVO);

}