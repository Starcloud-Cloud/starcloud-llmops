package com.starcloud.ops.business.app.dal.mysql.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import org.apache.ibatis.annotations.Mapper;

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
     * 根据发布uid查询该发布下的媒介列表
     *
     * @param publishUid 发布uid
     * @return 媒介列表
     */
    default List<AppPublishChannelDO> listByPublishUid(String publishUid) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = Wrappers.lambdaQuery(AppPublishChannelDO.class);
        wrapper.eq(AppPublishChannelDO::getPublishUid, publishUid);
        wrapper.eq(AppPublishChannelDO::getDeleted, Boolean.FALSE);
        return this.selectList(wrapper);
    }

    /**
     * 根据uid查询媒介
     *
     * @param uid 媒介uid
     * @return 媒介
     */
    default AppPublishChannelDO get(String uid) {
        LambdaQueryWrapper<AppPublishChannelDO> wrapper = Wrappers.lambdaQuery(AppPublishChannelDO.class);
        wrapper.eq(AppPublishChannelDO::getUid, uid);
        wrapper.eq(AppPublishChannelDO::getDeleted, Boolean.FALSE);
        return this.selectOne(wrapper);
    }

    /**
     * 创建 一次发布的默认媒介
     *
     * @param publishUid 发布uid
     */
    default void defaultCreateChannel(String publishUid, AppPublishReqVO request) {


    }


}
