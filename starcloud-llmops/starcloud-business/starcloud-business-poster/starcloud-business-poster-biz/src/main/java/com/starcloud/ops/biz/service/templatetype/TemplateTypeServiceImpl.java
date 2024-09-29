package com.starcloud.ops.biz.service.templatetype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeRespVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeSaveReqVO;
import com.starcloud.ops.biz.convert.TemplatetypeConvert;
import com.starcloud.ops.biz.dal.dataobject.templatetype.TemplatetypeDO;
import com.starcloud.ops.biz.dal.mysql.templatetype.TemplateTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
// import static com.starcloud.ops.api.enums.ErrorCodeConstants.TEMPLATETYPE_NOT_EXISTS;

/**
 * 海报模板类型 Service 实现类
 *
 * @author xhsadmin
 */
@Service
public class TemplateTypeServiceImpl implements TemplateTypeService {

    @Resource
    private TemplateTypeMapper templateTypeMapper;

    @Override
    public TemplateTypeRespVO createTemplatetype(TemplateTypeSaveReqVO createReqVO) {
        // 插入
        TemplatetypeDO templatetype = TemplatetypeConvert.INSTANCE.convert(createReqVO);
        templateTypeMapper.insert(templatetype);
        // 返回
        return TemplatetypeConvert.INSTANCE.convert(templatetype);
    }

    @Override
    public void updateTemplatetype(TemplateTypeSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplatetypeExists(updateReqVO.getUid());
        // 更新
        TemplatetypeDO updateObj = TemplatetypeConvert.INSTANCE.convert(updateReqVO);
        templateTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteTemplatetype(String uid) {
        // 校验存在
        validateTemplatetypeExists(uid);
        // 删除
        templateTypeMapper.deleteByUid(uid);
    }

    private void validateTemplatetypeExists(String uid) {
        if (getTemplatetype(uid) == null) {
            // throw exception(TEMPLATETYPE_NOT_EXISTS);
        }
    }

    @Override
    public TemplatetypeDO getTemplatetype(String uid) {
        return templateTypeMapper.selectByUid(uid);
    }

    @Override
    public PageResult<TemplatetypeDO> getTemplatetypePage(TemplateTypePageReqVO pageReqVO) {
        return templateTypeMapper.selectPage(pageReqVO);
    }

}