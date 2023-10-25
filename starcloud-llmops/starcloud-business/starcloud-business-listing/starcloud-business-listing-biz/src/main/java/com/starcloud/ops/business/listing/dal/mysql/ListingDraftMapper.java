package com.starcloud.ops.business.listing.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftOperationReqVO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ListingDraftMapper extends BaseMapperX<ListingDraftDO> {

    default ListingDraftDO getVersion(String uid, Integer version) {
        LambdaQueryWrapper<ListingDraftDO> wrapper = Wrappers.lambdaQuery(ListingDraftDO.class)
                .eq(ListingDraftDO::getUid, uid)
                .eq(ListingDraftDO::getVersion, version);
        return selectOne(wrapper);
    }

    default ListingDraftDO getLatest(String uid) {
        LambdaQueryWrapper<ListingDraftDO> wrapper = Wrappers.lambdaQuery(ListingDraftDO.class)
                .eq(ListingDraftDO::getUid, uid)
                .orderByDesc(ListingDraftDO::getVersion)
                .last(" limit 1");
        return selectOne(wrapper);
    }

    default void delete(DraftOperationReqVO operationReqVO) {
        LambdaQueryWrapper<ListingDraftDO> wrapper = Wrappers.lambdaQuery(ListingDraftDO.class)
                .eq(ListingDraftDO::getUid, operationReqVO.getUid())
                .eq(ListingDraftDO::getVersion, operationReqVO.getVersion());
        delete(wrapper);
    }

    default List<ListingDraftDO> listVersion(String uid) {
        LambdaQueryWrapper<ListingDraftDO> wrapper = Wrappers.lambdaQuery(ListingDraftDO.class)
                .eq(ListingDraftDO::getUid, uid)
                .orderByDesc(ListingDraftDO::getVersion);
        return selectList(wrapper);
    }


    Long count();

    List<ListingDraftDO> getLatestDrafts(@Param("start") Integer start, @Param("end") Integer end, @Param("field") String field,@Param("type") String type);

}
