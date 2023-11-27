package com.starcloud.ops.business.mission.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SingleMissionMapper extends BaseMapperX<SingleMissionDO> {

    default PageResult<SingleMissionDO> page(SinglePageQueryReqVO reqVO) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .eq(SingleMissionDO::getNotificationUid, reqVO.getNotificationUid())
                .orderByDesc(SingleMissionDO::getId);
        return selectPage(reqVO, wrapper);
    }

    default List<SingleMissionDO> listByCreativeUids(List<String> creativeUids) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .in(SingleMissionDO::getCreativeUid, creativeUids)
                .orderByAsc(SingleMissionDO::getId);
        return selectList(wrapper);
    }

    default SingleMissionDO getByUid(String uid) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .eq(SingleMissionDO::getUid, uid)
                ;
        return selectOne(wrapper);
    }

    default List<SingleMissionDO> getByNotificationUid(String notificationUid) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .eq(SingleMissionDO::getNotificationUid, notificationUid)
                ;
        return selectList(wrapper);
    }

    List<Long> selectIds(@Param("reqVO") SingleMissionQueryReqVO reqVO);
}
