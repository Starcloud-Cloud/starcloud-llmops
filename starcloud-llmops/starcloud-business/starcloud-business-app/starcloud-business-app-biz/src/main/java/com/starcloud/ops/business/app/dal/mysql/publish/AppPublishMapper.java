package com.starcloud.ops.business.app.dal.mysql.publish;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import org.apache.commons.lang3.StringUtils;

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
public interface AppPublishMapper extends BaseMapper<AppPublishDO> {

    /**
     * 分页查询应用发布记录
     *
     * @param query 查询条件
     * @return 应用发布列表
     */
    default Page<AppPublishDO> page(AppPublishPageReqVO query) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.eq(StringUtils.isNotBlank(query.getAppUid()), AppPublishDO::getAppUid, query.getAppUid());
        wrapper.likeLeft(StringUtils.isNotBlank(query.getName()), AppPublishDO::getName, query.getName());
        wrapper.eq(StringUtils.isNotBlank(query.getModel()), AppPublishDO::getModel, query.getModel());
        wrapper.in(CollectionUtil.isNotEmpty(query.getAudits()), AppPublishDO::getAudit, query.getAudits());
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
    default AppPublishDO getByUid(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppPublishDO> wrapper = queryWrapper(isSimple);
        wrapper.eq(AppPublishDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    /**
     * 修改应用发布记录的审核状态
     *
     * @param uid   发布 UID
     * @param audit 审核状态
     */
    default void audit(String uid, Integer audit) {
        // 只允许待审核、审核通过、审核拒绝、取消发布
        if (!Objects.equals(AppMarketAuditEnum.PENDING.getCode(), audit) ||
                !Objects.equals(AppMarketAuditEnum.APPROVED.getCode(), audit) ||
                !Objects.equals(AppMarketAuditEnum.REJECTED.getCode(), audit) ||
                !Objects.equals(AppMarketAuditEnum.CANCELED.getCode(), audit)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED);
        }
        LambdaUpdateWrapper<AppPublishDO> wrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        wrapper.eq(AppPublishDO::getUid, uid);
        wrapper.eq(AppPublishDO::getDeleted, Boolean.FALSE);
        wrapper.set(AppPublishDO::getAudit, audit);
        this.update(null, wrapper);
    }

    /**
     * 启用或者禁用分享
     *
     * @param uid         发布 UID
     * @param enableShare 是否启用分享
     */
    default void changeShareStatus(String uid, Boolean enableShare) {
        LambdaUpdateWrapper<AppPublishDO> wrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        wrapper.eq(AppPublishDO::getUid, uid);
        wrapper.eq(AppPublishDO::getDeleted, Boolean.FALSE);
        wrapper.set(AppPublishDO::getEnableShare, enableShare);
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
        wrapper.select(AppPublishDO::getUid);
        wrapper.select(AppPublishDO::getAppUid);
        wrapper.select(AppPublishDO::getMarketUid);
        wrapper.select(AppPublishDO::getName);
        wrapper.select(AppPublishDO::getModel);
        wrapper.select(AppPublishDO::getVersion);
        wrapper.select(AppPublishDO::getLanguage);
        wrapper.select(AppPublishDO::getAudit);
        wrapper.select(AppPublishDO::getShareLink);
        wrapper.select(AppPublishDO::getEnableShare);
        wrapper.select(AppPublishDO::getCreateTime);
        wrapper.select(AppPublishDO::getUpdateTime);
        return wrapper;
    }
}
