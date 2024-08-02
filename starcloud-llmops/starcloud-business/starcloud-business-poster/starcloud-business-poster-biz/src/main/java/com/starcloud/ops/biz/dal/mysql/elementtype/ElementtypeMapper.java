package com.starcloud.ops.biz.dal.mysql.elementtype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypePageReqVO;
import com.starcloud.ops.biz.dal.dataobject.elementtype.ElementtypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报元素类型 Mapper
 *
 * @author xhsadmin
 */
@Mapper
public interface ElementtypeMapper extends BaseMapperX<ElementtypeDO> {

    default PageResult<ElementtypeDO> selectPage(ElementTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ElementtypeDO>()
                .eqIfPresent(ElementtypeDO::getUid, reqVO.getUid())
                .eqIfPresent(ElementtypeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ElementtypeDO::getLabel, reqVO.getLabel())
                .eqIfPresent(ElementtypeDO::getOrder, reqVO.getOrder())
                .betweenIfPresent(ElementtypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ElementtypeDO::getId));
    }

    default void deleteByUid(String uid) {
        LambdaQueryWrapper<ElementtypeDO> wrapper = Wrappers.lambdaQuery(ElementtypeDO.class)
                .eq(ElementtypeDO::getUid, uid);
        delete(wrapper);
    }

    default ElementtypeDO selectByUid(String uid) {
        LambdaQueryWrapper<ElementtypeDO> wrapper = Wrappers.lambdaQuery(ElementtypeDO.class)
                .eq(ElementtypeDO::getUid, uid);
        return selectOne(wrapper);
    }
}