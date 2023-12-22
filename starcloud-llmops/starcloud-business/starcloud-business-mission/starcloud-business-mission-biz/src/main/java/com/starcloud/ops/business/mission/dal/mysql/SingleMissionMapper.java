package com.starcloud.ops.business.mission.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.mission.api.vo.request.ClaimedMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SingleMissionMapper extends BaseMapperX<SingleMissionDO> {

//    default List<SingleMissionDO> export(SinglePageQueryReqVO reqVO) {
//        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
//                .eq(SingleMissionDO::getNotificationUid, reqVO.getNotificationUid())
//                .eq(StringUtils.isNotBlank(reqVO.getStatus()), SingleMissionDO::getStatus, reqVO.getStatus())
//                .eq(StringUtils.isNotBlank(reqVO.getClaimUsername()), SingleMissionDO::getClaimUsername, reqVO.getClaimUsername())
//                .eq(StringUtils.isNotBlank(reqVO.getClaimUserId()), SingleMissionDO::getClaimUserId, reqVO.getClaimUserId())
//                .orderByDesc(SingleMissionDO::getId);
//        return selectList(wrapper);
//    }

    default void updateMission(SingleMissionDO singleMissionDO) {
        LambdaUpdateWrapper<SingleMissionDO> updateWrapper = Wrappers.lambdaUpdate(SingleMissionDO.class).eq(SingleMissionDO::getId, singleMissionDO.getId())
                .set(SingleMissionDO::getClaimUserId, singleMissionDO.getClaimUserId())
                .set(SingleMissionDO::getClaimUsername, singleMissionDO.getClaimUsername())
                .set(SingleMissionDO::getClaimTime, singleMissionDO.getClaimTime())
                .set(SingleMissionDO::getPublishUrl, singleMissionDO.getPublishUrl())
                .set(SingleMissionDO::getPublishTime, singleMissionDO.getPublishTime())
                .set(SingleMissionDO::getStatus, singleMissionDO.getStatus())
                .set(SingleMissionDO::getPreSettlementTime, singleMissionDO.getPreSettlementTime())
                .set(SingleMissionDO::getEstimatedAmount, singleMissionDO.getEstimatedAmount())
                .set(SingleMissionDO::getCloseMsg, singleMissionDO.getCloseMsg())
                .set(SingleMissionDO::getPreSettlementMsg, singleMissionDO.getPreSettlementMsg())
                .set(SingleMissionDO::getNoteDetailId, singleMissionDO.getNoteDetailId())
                .set(SingleMissionDO::getRunTime, singleMissionDO.getRunTime());
        update(null, updateWrapper);
    }

    List<SingleMissionExportVO> export(@Param("reqVO") SinglePageQueryReqVO reqVO);

    default List<SingleMissionDO> listByNotification(String notificationUid) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .in(SingleMissionDO::getNotificationUid, notificationUid);
        return selectList(wrapper);
    }

    default List<SingleMissionDO> listByCreativeUids(List<String> creativeUids) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .in(SingleMissionDO::getCreativeUid, creativeUids)
                .orderByAsc(SingleMissionDO::getId);
        return selectList(wrapper);
    }

    default SingleMissionDO getByUid(String uid) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .eq(SingleMissionDO::getUid, uid);
        return selectOne(wrapper);
    }

    default List<SingleMissionDO> listByUids(List<String> uids) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .in(SingleMissionDO::getUid, uids)
                .orderByAsc(SingleMissionDO::getId);
        return selectList(wrapper);
    }

    default void batchDelete(List<String> uids) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .in(SingleMissionDO::getUid, uids);
        delete(wrapper);
    }

    default List<SingleMissionDO> getByNotificationUid(String notificationUid) {
        LambdaQueryWrapper<SingleMissionDO> wrapper = Wrappers.lambdaQuery(SingleMissionDO.class)
                .eq(SingleMissionDO::getNotificationUid, notificationUid);
        return selectList(wrapper);
    }

    MissionNotificationDTO detail(@Param("uid") String uid);

    List<Long> selectIds(@Param("reqVO") SingleMissionQueryReqVO reqVO);


    Long pageCount(@Param("reqVO") SinglePageQueryReqVO reqVO);

    List<SingleMissionDTO> pageDetail(@Param("reqVO") SinglePageQueryReqVO reqVO,
                                      @Param("start") Integer start, @Param("size") Integer size);

    Long claimedMissionCount(@Param("reqVO") ClaimedMissionQueryReqVO reqVO);


    List<MissionNotificationDTO> claimedMissionPage(@Param("reqVO") ClaimedMissionQueryReqVO reqVO,
                                                    @Param("start") Integer start, @Param("size") Integer size);
}
