package com.starcloud.ops.biz.service.templatetype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeRespVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.templatetype.TemplatetypeDO;

import javax.validation.Valid;

/**
 * 海报模板类型 Service 接口
 *
 * @author xhsadmin
 */
public interface TemplateTypeService {

    /**
     * 创建海报模板类型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    TemplateTypeRespVO createTemplatetype(@Valid TemplateTypeSaveReqVO createReqVO);

    /**
     * 更新海报模板类型
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplatetype(@Valid TemplateTypeSaveReqVO updateReqVO);

    /**
     * 删除海报模板类型
     *
     * @param uid 编号
     */
    void deleteTemplatetype(String uid);

    /**
     * 获得海报模板类型
     *
     * @param uid 编号
     * @return 海报模板类型
     */
    TemplatetypeDO getTemplatetype(String uid);

    /**
     * 获得海报模板类型分页
     *
     * @param pageReqVO 分页查询
     * @return 海报模板类型分页
     */
    PageResult<TemplatetypeDO> getTemplatetypePage(TemplateTypePageReqVO pageReqVO);

}