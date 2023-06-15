package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketAuditRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.operate.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.operate.AppOperateMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Slf4j
@Service
public class AppMarketServiceImpl implements AppMarketService {

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppOperateMapper appOperateMapper;

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    @Override
    public PageResp<AppMarketDTO> page(AppMarketPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AppMarketDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName())
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());

        // 分页查询
        Page<AppMarketDO> page = appMarketMapper.selectPage(PageUtil.page(query), wrapper);
        List<AppMarketDTO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream().map(AppMarketConvert::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据应用 uid 获取应用详情
     *
     * @param uid     应用 uid
     * @param version 应用版本
     * @return 应用详情
     */
    @Override
    public AppMarketDTO getByUid(String uid, Integer version) {
        LambdaQueryWrapper<AppMarketDO> wrapper = buildBaseQueryWrapper().eq(AppMarketDO::getUid, uid).eq(AppMarketDO::getVersion, version);
        AppMarketDO templateMarketDO = appMarketMapper.selectOne(wrapper);
        Assert.notNull(templateMarketDO, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, uid, version));
        // 查看详情时候，会增加应用的查看量
        Integer viewCount = templateMarketDO.getViewCount() + 1;
        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getViewCount, viewCount)
                .eq(AppMarketDO::getId, templateMarketDO.getId())
        );

        templateMarketDO.setViewCount(viewCount);
        return AppMarketConvert.convert(templateMarketDO);
    }

    /**
     * 创建应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void create(AppMarketRequest request) {
        AppMarketDO templateMarketDO = AppMarketConvert.convertCreate(request);
        templateMarketDO.setUid(IdUtil.simpleUUID());
        appMarketMapper.insert(templateMarketDO);
    }

    /**
     * 更新应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void modify(AppMarketUpdateRequest request) {
        // 更新的时候，version 是必须的
        Assert.notNull(request.getVersion(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED));
        AppMarketDO templateMarketDO = AppMarketConvert.convertModify(request);
        LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getUid())
                .eq(AppMarketDO::getVersion, request.getVersion())
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
        appMarketMapper.update(templateMarketDO, wrapper);
    }

    /**
     * 删除应用市场的应用
     *
     * @param uid     应用 uid
     * @param version 应用版本
     */
    @Override
    public void deleteByUid(String uid, Integer version) {
        LambdaQueryWrapper<AppMarketDO> wrapper = buildBaseQueryWrapper()
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getVersion, version);
        AppMarketDO appMarketDO = appMarketMapper.selectOne(wrapper);
        // 您要删除的应用不存在
        Assert.notNull(appMarketDO, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, uid, version));
        appMarketMapper.deleteById(appMarketDO.getId());
    }

    /**
     * 审核应用
     *
     * @param request 审核请求
     */
    @Override
    public void audit(AppMarketAuditRequest request) {
        // 1. 基础校验
        Assert.notBlank(request.getUid(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_UID_IS_REQUIRED));
        Assert.notNull(request.getVersion(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED));
        Assert.notNull(request.getAudit(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_AUDIT_IS_REQUIRED));
        // 2. 审核状态只能是通过或者拒绝
        if (AppMarketAuditEnum.isSupported(request.getAudit()).equals(Boolean.FALSE)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_AUDIT_IS_NOT_SUPPORT, request.getAudit());
        }
        // 3. 查询应用是否存在
        AppMarketDO appMarketDO = appMarketMapper.selectOne(buildBaseQueryWrapper()
                .eq(AppMarketDO::getUid, request.getUid())
                .eq(AppMarketDO::getVersion, request.getVersion())
        );
        Assert.notNull(appMarketDO, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, request.getUid(), request.getVersion()));

        // 4. 更新应用的审核状态
        if (AppMarketAuditEnum.APPROVED.getCode().equals(request.getAudit())) {
            // 如果要将该版本更新为 审核通过，则需要该版本的 audit 状态设置为已通过, 其他版本的 audit 状态设置为已拒绝
            appMarketMapper.approvedAuditByUidAndVersion(request.getUid(), request.getVersion());
        } else if (AppMarketAuditEnum.REJECTED.getCode().equals(request.getAudit())) {
            // 如果要将该版本更新为 审核拒绝，只需要需要该版本的 audit 状态设置为已拒绝
            LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                    .set(AppMarketDO::getAudit, AppMarketAuditEnum.REJECTED.getCode())
                    .eq(AppMarketDO::getUid, request.getUid())
                    .eq(AppMarketDO::getVersion, request.getVersion())
                    .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
            appMarketMapper.update(null, wrapper);
        }
    }

    /**
     * 应用操作
     *
     * @param request 操作请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void operate(AppOperateRequest request) {
        AppMarketDTO appMarketDTO = this.getByUid(request.getAppUid(), request.getVersion());
        Assert.notNull(appMarketDTO, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, request.getAppUid(), request.getVersion()));
        AppOperateDO operateDO = AppOperateConvert.convert(request);
        // 插入操作记录
        appOperateMapper.insert(operateDO);
        // 更新应用市场的操作的数量
        String operate = request.getOperate().toUpperCase();
        AppMarketDO appMarketDO = new AppMarketDO();
        LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getAppUid())
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
        if (AppOperateTypeEnum.LIKE.name().equals(operate)) {
            appMarketDO.setLikeCount(appMarketDTO.getLikeCount() + 1);
        } else if (AppOperateTypeEnum.DOWNLOAD.name().equals(operate)) {
            appMarketDO.setDownloadCount(appMarketDTO.getDownloadCount() + 1);
        } else if (AppOperateTypeEnum.VIEW.name().equals(operate)) {
            appMarketDO.setViewCount(appMarketDTO.getViewCount() + 1);
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_OPERATE_NOT_SUPPORTED, request.getOperate());
        }
        appMarketMapper.update(appMarketDO, wrapper);
    }

    /**
     * 基础查询条件
     *
     * @return 基础查询条件
     */
    private static LambdaQueryWrapper<AppMarketDO> buildBaseQueryWrapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class).eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
    }

    /**
     * 构建分页查询条件, 只查询部分字段
     *
     * @return 分页查询条件
     */
    private static LambdaQueryWrapper<AppMarketDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class).select(
                        AppMarketDO::getUid,
                        AppMarketDO::getName,
                        AppMarketDO::getModel,
                        AppMarketDO::getVersion,
                        AppMarketDO::getTags,
                        AppMarketDO::getCategories,
                        AppMarketDO::getScenes,
                        AppMarketDO::getImages,
                        AppMarketDO::getIcon,
                        AppMarketDO::getFree,
                        AppMarketDO::getCost,
                        AppMarketDO::getDescription,
                        AppMarketDO::getViewCount,
                        AppMarketDO::getLikeCount,
                        AppMarketDO::getDownloadCount,
                        AppMarketDO::getCreator,
                        AppMarketDO::getCreateTime
                )
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
    }
}
