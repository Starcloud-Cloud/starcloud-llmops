package com.starcloud.ops.business.app.dal.mysql.limit;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.api.AppValidate;
import org.apache.commons.lang3.StringUtils;
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
        wrapper.last("LIMIT 1");
        return selectOne(wrapper);
    }

    /**
     * 根据查询条件查询限流信息, 如果不存在则返回默认值
     *
     * @param query 查询条件
     * @return 应用发布限流信息
     */
    default AppPublishLimitDO get(AppPublishLimitQuery query) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getAppUid, query.getAppUid());
        wrapper.eq(StringUtils.isNotBlank(query.getPublishUid()), AppPublishLimitDO::getPublishUid, query.getPublishUid());
        wrapper.eq(StringUtils.isNotBlank(query.getChannelUid()), AppPublishLimitDO::getChannelUid, query.getChannelUid());
        wrapper.last("LIMIT 1");
        return this.selectOne(wrapper);
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
        return this.updateById(appPublishLimit);
    }

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    default int delete(String uid) {
        AppPublishLimitDO limit = get(uid);
        AppValidate.notNull(limit, ErrorCodeConstants.LIMIT_NON_EXISTENT);
        return this.deleteById(limit.getId());
    }

    /**
     * 根据 appUid 删除应用发布限流信息
     *
     * @param appUid 应用 uid
     */
    default void deleteByAppUid(String appUid) {
        List<AppPublishLimitDO> list = this.listByAppUid(appUid);
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
        List<AppPublishLimitDO> list = this.listByPublishUid(publishUid);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Long> collect = list.stream().map(AppPublishLimitDO::getId).collect(Collectors.toList());
        this.deleteBatchIds(collect);
    }

    /**
     * 根据 应用UID 查询应用发布限流数量
     *
     * @param appUid 应用UID
     * @return 限流信息数量
     */
    default long countByAppUid(String appUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getAppUid, appUid);
        return selectCount(wrapper);
    }

    /**
     * 根据 应用UID 查询应用发布限流数量
     *
     * @param publishUid 应用UID
     * @return 限流信息数量
     */
    default long countByPublishUid(String publishUid) {
        LambdaQueryWrapper<AppPublishLimitDO> wrapper = Wrappers.lambdaQuery(AppPublishLimitDO.class);
        wrapper.eq(AppPublishLimitDO::getPublishUid, publishUid);
        return selectCount(wrapper);
    }

    /**
     * 根据 idList 更新发布 uid
     *
     * @param idList     idList
     * @param publishUid 发布 uid
     */
    void updatePublishUidByIdList(@Param("idList") List<Long> idList, @Param("publishUid") String publishUid);

}
