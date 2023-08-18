package com.starcloud.ops.business.app.dal.mysql.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-07-27
 */
@Mapper
public interface AppPublishChannelMapper extends BaseMapper<AppPublishChannelDO> {

    /**
     * 根据发布 UID 查询该发布下的媒介列表
     *
     * @param publishUid 发布uid
     * @return 媒介列表
     */
    default List<AppPublishChannelDO> listByPublishUid(String publishUid) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = Wrappers.lambdaQuery(AppPublishChannelDO.class);
        wrapper.eq(AppPublishChannelDO::getPublishUid, publishUid);
        wrapper.orderByDesc(AppPublishChannelDO::getCreateTime);
        return this.selectList(wrapper);
    }

    /**
     * 根据应用 UID 查询应用发布记录, 根据更新时间倒序
     *
     * @param appUid 应用 UID
     * @return 应用发布记录
     */
    default List<AppPublishChannelDO> listByAppUid(String appUid) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.eq(AppPublishChannelDO::getAppUid, appUid);
        wrapper.orderByDesc(AppPublishChannelDO::getCreateTime);
        return this.selectList(wrapper);
    }

    /**
     * 根据 mediumUid 查询应用发布记录
     *
     * @param mediumUid 媒介 uid
     * @return 应用发布渠道记录
     */
    default AppPublishChannelDO getByMediumUid(String mediumUid) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.eq(AppPublishChannelDO::getMediumUid, mediumUid);
        wrapper.eq(AppPublishChannelDO::getStatus, StateEnum.ENABLE.getCode());
        wrapper.orderByDesc(AppPublishChannelDO::getCreateTime).last("limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * 根据uid查询媒介
     *
     * @param uid 媒介uid
     * @return 媒介
     */
    default AppPublishChannelDO get(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = queryWrapper(isSimple);
        wrapper.eq(AppPublishChannelDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    /**
     * 根据 媒介 UID 查询 数量
     *
     * @param mediumUid 媒介 UID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM app_publish_channel WHERE medium_uid = #{mediumUid}")
    long countByMediumUid(String mediumUid);

    /**
     * 查询条件
     *
     * @param isSimple 是否简单查询
     * @return 查询条件
     */
    default LambdaQueryWrapper<AppPublishChannelDO> queryWrapper(boolean isSimple) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = Wrappers.lambdaQuery(AppPublishChannelDO.class);
        wrapper.eq(AppPublishChannelDO::getDeleted, Boolean.FALSE);
        if (!isSimple) {
            return wrapper;
        }
        wrapper.select(
                AppPublishChannelDO::getId,
                AppPublishChannelDO::getUid,
                AppPublishChannelDO::getAppUid,
                AppPublishChannelDO::getPublishUid,
                AppPublishChannelDO::getName,
                AppPublishChannelDO::getType,
                AppPublishChannelDO::getMediumUid,
                AppPublishChannelDO::getConfig,
                AppPublishChannelDO::getStatus,
                AppPublishChannelDO::getDescription,
                AppPublishChannelDO::getCreator,
                AppPublishChannelDO::getUpdater,
                AppPublishChannelDO::getCreateTime,
                AppPublishChannelDO::getUpdateTime
        );
        return wrapper;
    }


}
