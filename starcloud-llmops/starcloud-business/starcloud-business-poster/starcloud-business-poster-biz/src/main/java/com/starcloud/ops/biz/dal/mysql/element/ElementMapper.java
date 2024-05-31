package com.starcloud.ops.biz.dal.mysql.element;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementPageReqVO;
import com.starcloud.ops.biz.dal.dataobject.element.ElementDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报元素 Mapper
 *
 * @author xhsadmin
 */
@Mapper
public interface ElementMapper extends BaseMapperX<ElementDO> {

    default PageResult<ElementDO> selectPage(ElementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ElementDO>()
                .eqIfPresent(ElementDO::getUid, reqVO.getUid())
                .eqIfPresent(ElementDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ElementDO::getLabel, reqVO.getLabel())
                .eqIfPresent(ElementDO::getJson, reqVO.getJson())
                .eqIfPresent(ElementDO::getOrder, reqVO.getOrder())
                .eqIfPresent(ElementDO::getElementTypeUid, reqVO.getElementTypeUid())
                .eqIfPresent(ElementDO::getElementUrl, reqVO.getElementUrl())
                .betweenIfPresent(ElementDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ElementDO::getId));
    }

    default void deleteByUid(String uid) {
        LambdaQueryWrapper<ElementDO> wrapper = Wrappers.lambdaQuery(ElementDO.class)
                .eq(ElementDO::getUid, uid);
        delete(wrapper);
    }

    default ElementDO selectByUid(String uid) {
        LambdaQueryWrapper<ElementDO> wrapper = Wrappers.lambdaQuery(ElementDO.class)
                .eq(ElementDO::getUid, uid);
        return selectOne(wrapper);
    }
}