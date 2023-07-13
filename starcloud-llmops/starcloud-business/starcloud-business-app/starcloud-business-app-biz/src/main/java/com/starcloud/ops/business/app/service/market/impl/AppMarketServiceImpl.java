package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.response.InstalledRespVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.*;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.favorite.AppFavoriteConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.operate.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.favorite.AppFavoriteMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.operate.AppOperateMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppInstallStatusEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.extern.slf4j.Slf4j;
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
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppMarketRepository appMarketRepository;

    @Resource
    private AppOperateMapper appOperateMapper;

    @Resource
    private AppFavoriteMapper appFavoritesMapper;

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    @Override
    public PageResp<AppMarketRespVO> page(AppMarketPageQuery query, boolean isAdmin) {
        // 分页查询
        Page<AppMarketDO> page = appMarketMapper.page(query, isAdmin);
        // 转换并且返回数据
        List<AppMarketRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppMarketConvert.INSTANCE::convertResp).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 获取应用详情
     *
     * @param uid     应用 uid
     * @param version 应用版本
     * @return 应用详情
     */
    @Override
    public AppMarketRespVO get(String uid, Integer version) {
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_MARKET_UID_REQUIRED);
        // 查询应用市场信息
        AppMarketDO appMarketDO = appMarketMapper.get(uid, version, Boolean.FALSE);

        // 获取当前用户是否安装了该应用的信息
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        InstalledRespVO installedRespVO = appMapper.verifyHasInstalled(appMarketDO.getUid(), Long.toString(loginUserId));

        // 查看详情时候，会增加应用的查看量
        Integer viewCount = appMarketDO.getViewCount() + 1;
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getViewCount, appMarketDO.getViewCount() + 1)
                .eq(AppMarketDO::getId, appMarketDO.getId());
        appMarketMapper.update(null, updateWrapper);

        // 转换并且返回应用数据
        appMarketDO.setViewCount(viewCount);
        AppMarketRespVO appMarketRespVO = AppMarketConvert.INSTANCE.convertResp(appMarketDO);
        appMarketRespVO.setInstallStatus(installedRespVO);
        return appMarketRespVO;
    }

    /**
     * 创建应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void create(AppMarketReqVO request) {
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(request);
        appMarketEntity.insert();
    }

    /**
     * 更新应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void modify(AppMarketUpdateReqVO request) {
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(request);
        appMarketEntity.update();
    }

    /**
     * 删除应用市场的应用
     *
     * @param uid     应用 uid
     * @param version 应用版本
     */
    @Override
    public void deleteByUidAndVersion(String uid, Integer version) {
        appMarketRepository.deleteByUidAndVersion(uid, version);
    }

    /**
     * 安装安装应用
     *
     * @param request 安装请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void install(AppInstallReqVO request) {
        // 1. 基础校验
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_UID_IS_REQUIRED);
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }

        // 2. 查询应用市场应用并且校验
        AppMarketDO appMarket = appMarketMapper.get(request.getUid(), null, Boolean.FALSE);

        // 3. 校验当前用户是否已经安装过该用户, 如果安装过，抛出异常
        InstalledRespVO installed = appMapper.verifyHasInstalled(request.getUid(), Long.toString(loginUserId));
        AppValidate.isTrue(!AppInstallStatusEnum.UNINSTALLED.name().equals(installed.getInstallStatus()),
                ErrorCodeConstants.APP_HAS_BEEN_INSTALLED);

        // 4. 说明没有安装过，需要安装
        AppEntity appEntity = AppConvert.INSTANCE.convert(appMarket);
        appEntity.insert();

        // 5. 操作表中插入一条数据
        AppOperateDO appOperateDO = AppOperateConvert.INSTANCE.convert(appMarket.getUid(), appMarket.getVersion(),
                AppOperateTypeEnum.INSTALLED.name());
        appOperateMapper.insert(appOperateDO);

        // 6. 更新应用的安装量
        AppMarketDO updateAppMarketDO = new AppMarketDO();
        updateAppMarketDO.setId(appMarket.getId());
        updateAppMarketDO.setInstallCount(appMarket.getInstallCount() + 1);
        appMarketMapper.updateById(updateAppMarketDO);
    }

    /**
     * 审核应用
     *
     * @param request 审核请求
     */
    @Override
    public void audit(AppMarketAuditReqVO request) {
        // 1. 基础校验
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_MARKET_UID_REQUIRED);
        AppValidate.notNull(request.getVersion(), ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED);
        AppValidate.notNull(request.getAudit(), ErrorCodeConstants.APP_MARKET_AUDIT_IS_REQUIRED);
        // 2. 审核状态是否支持
        AppValidate.isTrue(IEnumable.containsOfCode(request.getAudit(), AppMarketAuditEnum.class),
                ErrorCodeConstants.APP_MARKET_AUDIT_IS_NOT_SUPPORT, request.getAudit());
        // 3. 查询应用是否存在
        appMarketMapper.get(request.getUid(), request.getVersion(), Boolean.TRUE);
        // 4. 更新应用的审核状态
        if (AppMarketAuditEnum.APPROVED.getCode().equals(request.getAudit())) {
            // 如果要将该版本更新为 审核通过，则需要该版本的 audit 状态设置为已通过, 其他版本的 audit 状态设置为已拒绝
            appMarketMapper.approvedAuditByUidAndVersion(request.getUid(), request.getVersion());
        } else {
            // 如果要将该版本更新为 审核拒绝或者待审核，只需要需要该版本的 audit 状态设置为已拒绝或者待审核
            LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                    .set(AppMarketDO::getAudit, request.getAudit())
                    .eq(AppMarketDO::getUid, request.getUid())
                    .eq(AppMarketDO::getVersion, request.getVersion());
            appMarketMapper.update(null, updateWrapper);
        }
    }

    /**
     * 应用操作
     *
     * @param request 操作请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void operate(AppOperateReqVO request) {
        // 1. 基础校验
        AppValidate.notBlank(request.getAppUid(), ErrorCodeConstants.APP_UID_IS_REQUIRED);
        AppValidate.notBlank(request.getOperate(), ErrorCodeConstants.APP_OPERATE_IS_REQUIRED);

        // 2. 查询应用市场的应用, 如果不存在，抛出异常
        AppMarketDO appMarketDO = appMarketMapper.get(request.getAppUid(), null, Boolean.TRUE);

        request.setVersion(appMarketDO.getVersion());
        // 转换数据
        AppOperateDO operateDO = AppOperateConvert.INSTANCE.convert(request);
        // 插入操作记录
        appOperateMapper.insert(operateDO);
        // 更新应用市场的操作的数量
        String operate = request.getOperate().toUpperCase();
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getAppUid())
                .eq(AppMarketDO::getVersion, appMarketDO.getVersion());

        if (AppOperateTypeEnum.LIKE.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getLikeCount, appMarketDO.getLikeCount() + 1);
        } else if (AppOperateTypeEnum.INSTALLED.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getInstallCount, appMarketDO.getInstallCount() + 1);
        } else if (AppOperateTypeEnum.VIEW.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getViewCount, appMarketDO.getViewCount() + 1);
        } else if (AppOperateTypeEnum.USAGE.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getUsageCount, appMarketDO.getUsageCount() + 1);
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_OPERATE_NOT_SUPPORTED, request.getOperate());
        }
        appMarketMapper.update(appMarketDO, updateWrapper);
    }

    /**
     * 应用市场应用收藏列表
     *
     * @param userId 用户 uid
     * @return 收藏列表
     */
    @Override
    public List<AppFavoriteRespVO> listFavorite(String userId) {
        AppValidate.notBlank(userId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        return CollectionUtil.emptyIfNull(appFavoritesMapper.listFavorite(userId)).stream()
                .map(AppFavoriteConvert.INSTANCE::convert)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的应用的详情
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     * @return 收藏应用
     */
    @Override
    public AppFavoriteRespVO getFavoriteApp(String userId, String uid) {
        AppValidate.notBlank(userId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_UID_IS_REQUIRED);
        AppFavoritePO favoriteApp = appFavoritesMapper.getFavoriteApp(userId, uid);
        return AppFavoriteConvert.INSTANCE.convert(favoriteApp);
    }

    /**
     * 将应用加入到收藏夹
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favorite(String userId, String uid) {
        // 应用是否已经被收藏了
        AppFavoriteDO favoriteDO = appFavoritesMapper.selectOne(Wrappers.lambdaQuery(AppFavoriteDO.class)
                .eq(AppFavoriteDO::getAppUid, uid).eq(AppFavoriteDO::getUser, userId));
        AppValidate.isNull(favoriteDO, ErrorCodeConstants.APP_HAS_FAVORITE, uid);
        // 查询应用市场的应用, 如果不存在，抛出异常
        AppMarketDO appMarketDO = appMarketMapper.get(uid, null, Boolean.FALSE);

        // 插入收藏记录
        AppFavoriteDO appFavoriteDO = new AppFavoriteDO();
        appFavoriteDO.setAppUid(uid);
        appFavoriteDO.setUser(userId);
        appFavoriteDO.setDeleted(Boolean.FALSE);
        appFavoritesMapper.insert(appFavoriteDO);

        // 更新应用市场的收藏数量
        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getLikeCount, appMarketDO.getLikeCount() + 1)
                .eq(AppMarketDO::getVersion, appMarketDO.getVersion())
                .eq(AppMarketDO::getUid, uid));
    }

    /**
     * 取消收藏
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     */
    @Override
    public void cancelFavorite(String userId, String uid) {
        // 应用是否已经被收藏了
        AppFavoriteDO favoriteDO = appFavoritesMapper.selectOne(Wrappers.lambdaQuery(AppFavoriteDO.class)
                .eq(AppFavoriteDO::getAppUid, uid).eq(AppFavoriteDO::getUser, userId));
        AppValidate.notNull(favoriteDO, ErrorCodeConstants.APP_FAVORITE_NOT_EXISTS, uid);

        // 删除收藏记录
        appFavoritesMapper.delete(Wrappers.lambdaQuery(AppFavoriteDO.class)
                .eq(AppFavoriteDO::getAppUid, uid).eq(AppFavoriteDO::getUser, userId));
    }
}
