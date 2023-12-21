package com.starcloud.ops.business.mission.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.mission.api.vo.request.AppNotificationQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.dal.dataobject.AppNotificationDTO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationCenterMapper extends BaseMapperX<NotificationCenterDO> {

    default PageResult<NotificationCenterDO> page(NotificationPageQueryReqVO reqVO) {
        LambdaQueryWrapper<NotificationCenterDO> wrapper = Wrappers.lambdaQuery(NotificationCenterDO.class)
                .orderByDesc(NotificationCenterDO::getCreateTime);
        return selectPage(reqVO, wrapper);
    }

    default NotificationCenterDO selectByUid(String uid) {
        LambdaQueryWrapper<NotificationCenterDO> wrapper = Wrappers.lambdaQuery(NotificationCenterDO.class)
                .eq(NotificationCenterDO::getUid, uid);
        return selectOne(wrapper);
    }

    default NotificationCenterDO selectByName(String name) {
        LambdaQueryWrapper<NotificationCenterDO> wrapper = Wrappers.lambdaQuery(NotificationCenterDO.class)
                .eq(NotificationCenterDO::getName, name)
                .last(" limit 1");
        return selectOne(wrapper);
    }

    default Long count(String creator) {
        LambdaQueryWrapper<NotificationCenterDO> wrapper = Wrappers.lambdaQuery(NotificationCenterDO.class)
                .eq(NotificationCenterDO::getCreator, creator);
        return this.selectCount(wrapper);
    }

    default void update(NotificationCenterDO notificationCenterDO) {
        LambdaUpdateWrapper<NotificationCenterDO> updateWrapper = Wrappers.lambdaUpdate(NotificationCenterDO.class)
                .eq(NotificationCenterDO::getId, notificationCenterDO.getId())
                .set(NotificationCenterDO::getName, notificationCenterDO.getName())
                .set(NotificationCenterDO::getPlatform, notificationCenterDO.getPlatform())
                .set(NotificationCenterDO::getField, notificationCenterDO.getField())
                .set(NotificationCenterDO::getType, notificationCenterDO.getType())
                .set(NotificationCenterDO::getUnitPrice, notificationCenterDO.getUnitPrice())
                .set(NotificationCenterDO::getStartTime, notificationCenterDO.getStartTime())
                .set(NotificationCenterDO::getEndTime, notificationCenterDO.getEndTime())
                .set(NotificationCenterDO::getNotificationBudget, notificationCenterDO.getNotificationBudget())
                .set(NotificationCenterDO::getSingleBudget, notificationCenterDO.getSingleBudget())
                .set(NotificationCenterDO::getDescription, notificationCenterDO.getDescription())
                .set(NotificationCenterDO::getRemark, notificationCenterDO.getRemark())
                .set(NotificationCenterDO::getClaimLimit, notificationCenterDO.getClaimLimit())
                .set(NotificationCenterDO::getMinFansNum, notificationCenterDO.getMinFansNum())
                .set(NotificationCenterDO::getOpen, notificationCenterDO.getOpen());
        update(null, updateWrapper);
    }

    Long pageCount(@Param("reqVO") NotificationPageQueryReqVO reqVO);

    List<NotificationCenterDTO> pageDetail(@Param("reqVO") NotificationPageQueryReqVO reqVO,
                                           @Param("start") Integer start, @Param("size") Integer size);

    Long appPageCount(@Param("reqVO") AppNotificationQueryReqVO reqVO);

    List<AppNotificationDTO> appPage(@Param("reqVO") AppNotificationQueryReqVO reqVO,
                                     @Param("start") Integer start, @Param("size") Integer size,
                                     @Param("field") String field, @Param("type") String type);
}
