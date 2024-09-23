package com.starcloud.ops.biz.service.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplatePageReqVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateRespVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateSaveReqVO;
import com.starcloud.ops.biz.convert.TemplateConvert;
import com.starcloud.ops.biz.dal.dataobject.template.TemplateDO;
import com.starcloud.ops.biz.dal.mysql.template.TemplateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
// import static com.starcloud.ops.api.enums.ErrorCodeConstants.TEMPLATE_NOT_EXISTS;

/**
 * 海报模板 Service 实现类
 *
 * @author xhsadmin
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    @Override
    public TemplateRespVO createTemplate(TemplateSaveReqVO createReqVO) {
        // 插入
        TemplateDO template = TemplateConvert.INSTANCE.convert(createReqVO);
        templateMapper.insert(template);
        // 返回
        return TemplateConvert.INSTANCE.convert(template);
    }

    @Override
    public void updateTemplate(TemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateExists(updateReqVO.getUid());
        // 更新
        TemplateDO updateObj = TemplateConvert.INSTANCE.convert(updateReqVO);
        templateMapper.updateById(updateObj);
    }

    @Override
    public void deleteTemplate(String uid) {
        // 校验存在
        validateTemplateExists(uid);
        // 删除
        templateMapper.deleteByUid(uid);
    }

    private void validateTemplateExists(String uid) {
        if (getTemplate(uid) == null) {
            // throw exception(TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public TemplateDO getTemplate(String uid) {
        return templateMapper.selectByUid(uid);
    }

    @Override
    public PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO) {
        return templateMapper.selectPage(pageReqVO);
    }

}