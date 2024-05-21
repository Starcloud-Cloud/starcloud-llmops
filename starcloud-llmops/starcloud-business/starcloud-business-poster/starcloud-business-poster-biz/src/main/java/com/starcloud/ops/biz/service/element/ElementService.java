package com.starcloud.ops.biz.service.element;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementPageReqVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementRespVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.element.ElementDO;

import javax.validation.Valid;


/**
 * 海报元素 Service 接口
 *
 * @author xhsadmin
 */
public interface ElementService {

    /**
     * 创建海报元素
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ElementRespVO createElement(@Valid ElementSaveReqVO createReqVO);

    /**
     * 更新海报元素
     *
     * @param updateReqVO 更新信息
     */
    void updateElement(@Valid ElementSaveReqVO updateReqVO);

    /**
     * 删除海报元素
     *
     * @param uid 编号
     */
    void deleteElement(String uid);

    /**
     * 获得海报元素
     *
     * @param uid 编号
     * @return 海报元素
     */
    ElementDO getElement(String uid);

    /**
     * 获得海报元素分页
     *
     * @param pageReqVO 分页查询
     * @return 海报元素分页
     */
    PageResult<ElementDO> getElementPage(ElementPageReqVO pageReqVO);

}