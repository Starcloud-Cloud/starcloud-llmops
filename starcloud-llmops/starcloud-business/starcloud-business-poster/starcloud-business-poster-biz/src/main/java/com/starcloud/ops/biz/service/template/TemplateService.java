package com.starcloud.ops.biz.service.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplatePageReqVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateRespVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.template.TemplateDO;

import javax.validation.Valid;

/**
 * 海报模板 Service 接口
 *
 * @author xhsadmin
 */
public interface TemplateService {

    /**
     * 创建海报模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    TemplateRespVO createTemplate(@Valid TemplateSaveReqVO createReqVO);

    /**
     * 更新海报模板
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplate(@Valid TemplateSaveReqVO updateReqVO);

    /**
     * 删除海报模板
     *
     * @param uid 编号
     */
    void deleteTemplate(String uid);

    /**
     * 获得海报模板
     *
     * @param uid 编号
     * @return 海报模板
     */
    TemplateDO getTemplate(String uid);

    /**
     * 获得海报模板分页
     *
     * @param pageReqVO 分页查询
     * @return 海报模板分页
     */
    PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO);

}