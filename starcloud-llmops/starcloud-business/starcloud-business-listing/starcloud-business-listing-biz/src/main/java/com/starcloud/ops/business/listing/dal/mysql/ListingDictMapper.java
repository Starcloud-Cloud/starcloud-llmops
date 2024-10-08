package com.starcloud.ops.business.listing.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ListingDictMapper extends BaseMapperX<ListingDictDO> {

    default ListingDictDO getByUid(String uid) {
        LambdaQueryWrapper<ListingDictDO> wrapper = Wrappers.lambdaQuery(ListingDictDO.class)
                .eq(ListingDictDO::getUid, uid);
        return selectOne(wrapper);
    }

    default ListingDictDO getByName(String name) {
        LambdaQueryWrapper<ListingDictDO> wrapper = Wrappers.lambdaQuery(ListingDictDO.class)
                .eq(ListingDictDO::getName, name)
                .last(" limit 1")
                ;
        return selectOne(wrapper);
    }

    default PageResult<ListingDictDO> page(DictPageReqVO reqVO) {
        LambdaQueryWrapper<ListingDictDO> wrapper = Wrappers.lambdaQuery(ListingDictDO.class)
                .like(StringUtils.isNotBlank(reqVO.getName()), ListingDictDO::getName, reqVO.getName())
                .eq(StringUtils.isNotBlank(reqVO.getEndpoint()), ListingDictDO::getEndpoint, reqVO.getEndpoint())
                .eq(reqVO.getEnable() != null, ListingDictDO::getEnable, reqVO.getEnable())
                .last(reqVO.orderSql());
        return selectPage(reqVO, wrapper);
    }


    default void delete(List<String> uids) {
        LambdaQueryWrapper<ListingDictDO> wrapper = Wrappers.lambdaQuery(ListingDictDO.class)
                .in(ListingDictDO::getUid, uids);
        delete(wrapper);
    }


    List<ListingDictDTO> limitList(@Param("reqVO") DictPageReqVO reqVO,
                                     @Param("orderSql") String orderSql,
                                     @Param("begin") Integer begin,
                                     @Param("pageSize") Integer pageSize);

    Long count(@Param("reqVO") DictPageReqVO reqVO);
}
