package com.starcloud.ops.biz.dal.mysql.template;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplatePageReqVO;
import com.starcloud.ops.biz.dal.dataobject.template.TemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报模板 Mapper
 *
 * @author xhsadmin
 */
@Mapper
public interface TemplateMapper extends BaseMapperX<TemplateDO> {

    default PageResult<TemplateDO> selectPage(TemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TemplateDO>()
                .eqIfPresent(TemplateDO::getUid, reqVO.getUid())
                .eqIfPresent(TemplateDO::getStatus, reqVO.getStatus())
                .eqIfPresent(TemplateDO::getLabel, reqVO.getLabel())
                .eqIfPresent(TemplateDO::getJson, reqVO.getJson())
                .eqIfPresent(TemplateDO::getTempUrl, reqVO.getTempUrl())
                .eqIfPresent(TemplateDO::getOrder, reqVO.getOrder())
                .eqIfPresent(TemplateDO::getTemplateTypeUid, reqVO.getTemplateTypeUid())
                .eqIfPresent(TemplateDO::getParams, reqVO.getParams())
                .betweenIfPresent(TemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TemplateDO::getId));
    }

    default void deleteByUid(String uid) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery(TemplateDO.class)
                .eq(TemplateDO::getUid, uid);
        delete(wrapper);
    }

    default TemplateDO selectByUid(String uid) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery(TemplateDO.class)
                .eq(TemplateDO::getUid, uid);
        return selectOne(wrapper);
    }
}