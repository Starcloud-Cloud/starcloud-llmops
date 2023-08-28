package com.starcloud.ops.business.app.dal.mysql.limit;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-07-27
 */
@Mapper
public interface AppPublishLimitMapper extends BaseMapper<AppPublishLimitDO> {

    /**
     * 根据 appUid 查询应用发布限流信息
     *
     * @param appUid 应用 uid
     * @return 应用发布限流信息
     */
    default List<AppPublishLimitDO> listByAppUid(String appUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getAppUid, appUid);
        wrapper.orderByDesc(AppPublishLimitDO::getCreateTime);
        return this.selectList(wrapper);
    }

    /**
     * 根据 publishUid 查询应用发布限流信息
     *
     * @param publishUid 发布 UID
     * @return 应用发布限流信息
     */
    default List<AppPublishLimitDO> listByPublishUid(String publishUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getPublishUid, publishUid);
        wrapper.orderByDesc(AppPublishLimitDO::getCreateTime);
        return this.selectList(wrapper);
    }

    /**
     * 根据 uid 获取应用发布限流信息
     *
     * @param uid 应用 uid
     * @return 应用发布限流信息
     */
    default AppPublishLimitDO get(String uid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getUid, uid);
        return selectOne(wrapper);
    }

    /**
     * 创建应用发布限流信息
     *
     * @param appPublishLimit 应用发布限流信息
     */
    default int create(AppPublishLimitDO appPublishLimit) {
        appPublishLimit.setDeleted(Boolean.FALSE);
        return this.insert(appPublishLimit);
    }

    /**
     * 更新应用发布限流信息
     *
     * @param appPublishLimit 应用发布限流信息
     */
    default int modify(AppPublishLimitDO appPublishLimit) {
        LambdaUpdateWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaUpdate(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getUid, appPublishLimit.getUid());
        return this.update(appPublishLimit, wrapper);
    }

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    default int delete(String uid) {
        AppPublishLimitDO limit = get(uid);
        AppValidate.notNull(limit, ErrorCodeConstants.APP_PUBLISH_LIMIT_NOT_EXISTS_UID);
        return this.deleteById(limit.getId());
    }

    /**
     * 根据 appUid 删除应用发布限流信息
     *
     * @param appUid 应用 uid
     */
    default void deleteByAppUid(String appUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getAppUid, appUid);
        List<AppPublishLimitDO> list = this.selectList(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Long> collect = list.stream().map(AppPublishLimitDO::getId).collect(Collectors.toList());
        this.deleteBatchIds(collect);
    }

    /**
     * 根据 publishUid 删除应用发布限流信息
     *
     * @param publishUid 发布 uid
     */
    default void deleteByPublishUid(String publishUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getPublishUid, publishUid);
        List<AppPublishLimitDO> list = this.selectList(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Long> collect = list.stream().map(AppPublishLimitDO::getId).collect(Collectors.toList());
        this.deleteBatchIds(collect);
    }

    /**
     * 根据 idList 更新发布 uid
     *
     * @param idList     idList
     * @param publishUid 发布 uid
     */
    void updatePublishUidByIdList(@Param("idList") List<Long> idList, @Param("publishUid") String publishUid);

}
