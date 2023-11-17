package com.starcloud.ops.business.mission.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import org.apache.ibatis.annotations.Mapper;

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
}
