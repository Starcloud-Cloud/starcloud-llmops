package com.starcloud.ops.business.mission.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    Long pageCount(@Param("reqVO") NotificationPageQueryReqVO reqVO);

    List<NotificationCenterDTO> pageDetail(@Param("reqVO") NotificationPageQueryReqVO reqVO,
                                           @Param("start") Integer start, @Param("size") Integer size);

    Long appPageCount(@Param("reqVO") AppNotificationQueryReqVO reqVO);

    List<AppNotificationDTO> appPage(@Param("reqVO") AppNotificationQueryReqVO reqVO,
                                     @Param("start") Integer start, @Param("size") Integer size,
                                     @Param("field") String field, @Param("type") String type);
}
