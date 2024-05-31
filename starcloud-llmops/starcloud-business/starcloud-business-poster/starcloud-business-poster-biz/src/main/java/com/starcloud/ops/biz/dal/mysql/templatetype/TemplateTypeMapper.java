package com.starcloud.ops.biz.dal.mysql.templatetype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypePageReqVO;
import com.starcloud.ops.biz.dal.dataobject.templatetype.TemplatetypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报模板类型 Mapper
 *
 * @author xhsadmin
 */
@Mapper
public interface TemplateTypeMapper extends BaseMapperX<TemplatetypeDO> {

    default PageResult<TemplatetypeDO> selectPage(TemplateTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TemplatetypeDO>()
                .eqIfPresent(TemplatetypeDO::getUid, reqVO.getUid())
                .eqIfPresent(TemplatetypeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(TemplatetypeDO::getLabel, reqVO.getLabel())
                .eqIfPresent(TemplatetypeDO::getOrder, reqVO.getOrder())
                .betweenIfPresent(TemplatetypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TemplatetypeDO::getId));
    }

    default void deleteByUid(String uid) {
        LambdaQueryWrapper<TemplatetypeDO> wrapper = Wrappers.lambdaQuery(TemplatetypeDO.class)
                .eq(TemplatetypeDO::getUid, uid);
        delete(wrapper);
    }

    default TemplatetypeDO selectByUid(String uid) {
        LambdaQueryWrapper<TemplatetypeDO> wrapper = Wrappers.lambdaQuery(TemplatetypeDO.class)
                .eq(TemplatetypeDO::getUid, uid);
        return selectOne(wrapper);
    }
}