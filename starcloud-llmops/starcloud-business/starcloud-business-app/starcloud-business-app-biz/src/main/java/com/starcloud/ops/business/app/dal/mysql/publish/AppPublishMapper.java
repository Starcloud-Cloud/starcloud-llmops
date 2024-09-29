package com.starcloud.ops.business.app.dal.mysql.publish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 应用发布 Mapper 接口
 * </p>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Mapper
public interface AppPublishMapper extends BaseMapper<AppPublishDO> {

    /**
     * 分页查询应用发布记录
     *
     * @param query 查询条件
     * @return 应用发布列表
     */
    default Page<AppPublishDO> page(AppPublishPageReqVO query) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(Boolean.TRUE);
        // APP_UID
        wrapper.eq(StringUtils.isNotBlank(query.getAppUid()), AppPublishDO::getAppUid, query.getAppUid());
        // NAME 模糊查询
        wrapper.likeRight(StringUtils.isNotBlank(query.getName()), AppPublishDO::getName, query.getName());
        // MODEL
        wrapper.eq(StringUtils.isNotBlank(query.getModel()), AppPublishDO::getModel, query.getModel());
        if (query.getIsAdmin()) {
            if (query.getAudit() != null) {
                if (Objects.equals(query.getAudit(), AppPublishAuditEnum.APPROVED.getCode()) ||
                        Objects.equals(query.getAudit(), AppPublishAuditEnum.PENDING.getCode()) ||
                        Objects.equals(query.getAudit(), AppPublishAuditEnum.REJECTED.getCode())) {
                    wrapper.eq(AppPublishDO::getAudit, query.getAudit());
                }
            } else {
                wrapper.in(AppPublishDO::getAudit, AppPublishAuditEnum.APPROVED.getCode(), AppPublishAuditEnum.REJECTED.getCode(), AppPublishAuditEnum.PENDING.getCode());
            }
            wrapper.orderByAsc(AppPublishDO::getAudit);
        } else {
            wrapper.eq(Objects.nonNull(query.getAudit()), AppPublishDO::getAudit, query.getAudit());
        }
        // 排序
        wrapper.orderByDesc(AppPublishDO::getUpdateTime);
        wrapper.orderByDesc(AppPublishDO::getVersion);
        return this.selectPage(PageUtil.page(query), wrapper);
    }

    /**
     * 根据应用 UID 查询应用发布记录, 根据版本号倒序排序
     *
     * @param appUid 应用 UID
     * @return 应用发布记录
     */
    default List<AppPublishDO> listByAppUid(String appUid) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.eq(AppPublishDO::getAppUid, appUid);
        wrapper.orderByDesc(AppPublishDO::getVersion);
        return this.selectList(wrapper);
    }

    /**
     * 根据发布 UID 查询应用发布记录
     *
     * @param uid 发布 UID
     * @return 应用发布记录
     */
    default AppPublishDO get(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(isSimple);
        wrapper.eq(AppPublishDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    default AppPublishDO getMarket(String marketUid, boolean isSimple) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(isSimple);
        wrapper.eq(AppPublishDO::getMarketUid, marketUid)
                .orderByDesc(AppPublishDO::getId)
                .last(" limit 1");
        return this.selectOne(wrapper);
    }

    /**
     * 审核发布记录
     *
     * @param uid    发布 UID
     * @param audit  审核状态
     * @param userId 用户 ID
     */
    default void audit(String uid, Integer audit, Long userId) {
        LambdaUpdateWrapper<AppPublishDO> wrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        wrapper.eq(AppPublishDO::getUid, uid);
        wrapper.eq(AppPublishDO::getDeleted, Boolean.FALSE);
        wrapper.set(AppPublishDO::getAudit, audit);
        wrapper.set(Objects.nonNull(userId), AppPublishDO::getUserId, userId);
        this.update(null, wrapper);
    }

    /**
     * 根据应用 UID 查询应用发布记录
     *
     * @param marketUid 应用市场 UID
     * @return 应用发布记录
     */
    default String selectAppUidByMarketUid(String marketUid) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.eq(AppPublishDO::getMarketUid, marketUid);
        List<AppPublishDO> appPublishList = this.selectList(wrapper);
        if (CollectionUtils.isEmpty(appPublishList)) {
            return null;
        }
        return appPublishList.get(0).getAppUid();
    }

    /**
     * 删除应用市场记录后，更新应用发布记录
     *
     * @param marketUid 应用市场 UID
     */
    default void updateAfterDeleteMarket(String marketUid) {
        LambdaUpdateWrapper<AppPublishDO> wrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        // 模版市场删除后，将模版市场 UID 置空
        wrapper.set(AppPublishDO::getMarketUid, null);
        // 模版市场删除后，将审核状态置为未发布
        wrapper.set(AppPublishDO::getAudit, AppPublishAuditEnum.UN_PUBLISH.getCode());
        wrapper.eq(AppPublishDO::getMarketUid, marketUid);
        this.update(null, wrapper);
    }

    /**
     * 查询条件
     *
     * @param isSimple 是否简单查询
     * @return 查询条件
     */
    default LambdaQueryWrapper<AppPublishDO> queryWrapper(boolean isSimple) {
        LambdaQueryWrapper<AppPublishDO> wrapper = Wrappers.lambdaQuery(AppPublishDO.class);
        wrapper.eq(AppPublishDO::getDeleted, Boolean.FALSE);
        if (!isSimple) {
            return wrapper;
        }
        wrapper.select(
                AppPublishDO::getId,
                AppPublishDO::getUid,
                AppPublishDO::getAppUid,
                AppPublishDO::getMarketUid,
                AppPublishDO::getUserId,
                AppPublishDO::getName,
                AppPublishDO::getType,
                AppPublishDO::getModel,
                AppPublishDO::getVersion,
                AppPublishDO::getCategory,
                AppPublishDO::getLanguage,
                AppPublishDO::getAudit,
                AppPublishDO::getCreator,
                AppPublishDO::getUpdater,
                AppPublishDO::getCreateTime,
                AppPublishDO::getUpdateTime,
                AppPublishDO::getDescription
        );
        return wrapper;
    }

}
