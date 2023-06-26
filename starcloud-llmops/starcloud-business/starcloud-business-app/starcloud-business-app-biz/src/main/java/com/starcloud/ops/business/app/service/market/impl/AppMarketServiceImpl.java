package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.vo.request.AppInstallReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketAuditReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.operate.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
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
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
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
    public PageResp<AppMarketRespVO> page(AppMarketPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AppMarketDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName())
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .orderByDesc(AppMarketDO::getCreateTime);

        // 分页查询
        Page<AppMarketDO> page = appMarketMapper.selectPage(PageUtil.page(query), wrapper);

        // 转换并且返回数据
        List<AppMarketRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppMarketConvert.INSTANCE::convertResp)
                .peek(item -> {
                    item.setWorkflowConfig(null);
                    item.setChatConfig(null);
                }).collect(Collectors.toList());
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
    public AppMarketRespVO getByUidAndVersion(String uid, Integer version) {
        // 查询应用市场应用并且校验
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getVersion, version);
        AppMarketDO appMarketDO = appMarketMapper.selectOne(wrapper);
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, uid, version);

        // 查看详情时候，会增加应用的查看量
        Integer viewCount = appMarketDO.getViewCount() + 1;
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getViewCount, appMarketDO.getViewCount() + 1)
                .eq(AppMarketDO::getId, appMarketDO.getId());
        appMarketMapper.update(null, updateWrapper);

        // 转换并且返回应用数据
        appMarketDO.setViewCount(viewCount);
        return AppMarketConvert.INSTANCE.convertResp(appMarketDO);
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
     * 下载安装应用
     *
     * @param request 安装请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void install(AppInstallReqVO request) {
        // 1. 基础校验
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_UID_IS_REQUIRED);
        AppValidate.notNull(request.getVersion(), ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED);
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 2. 查询应用市场应用并且校验
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getUid())
                .eq(AppMarketDO::getVersion, request.getVersion())
                .eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.getCode());
        AppMarketDO appMarket = appMarketMapper.selectOne(wrapper);
        AppValidate.notNull(appMarket, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, request.getUid(), request.getVersion());

        // 3. 校验当前用户是否已经下载过该用户, 如果下载过，抛出异常
        LambdaQueryWrapper<AppDO> appWrapper = Wrappers.lambdaQuery(AppDO.class)
                .select(AppDO::getUid, AppDO::getDownloadUid, AppDO::getCreator)
                .likeLeft(AppDO::getDownloadUid, request.getUid())
                .eq(AppDO::getType, AppTypeEnum.DOWNLOAD.getCode())
                .eq(AppDO::getCreator, loginUserId);
        AppDO app = appMapper.selectOne(appWrapper);
        AppValidate.isNull(app, ErrorCodeConstants.APP_HAS_BEEN_INSTALLED, request.getUid(), request.getVersion());

        // 4.说明没有下载过，需要下载
        AppEntity appEntity = AppConvert.INSTANCE.convert(appMarket);
        appEntity.insert();

        // 操作表中插入一条数据
        AppOperateDO appOperateDO = new AppOperateDO();
        appOperateDO.setAppUid(appMarket.getUid());
        appOperateDO.setVersion(appMarket.getVersion());
        appOperateDO.setOperate(AppOperateTypeEnum.DOWNLOAD.name());
        appOperateDO.setUser(Long.toString(loginUserId));
        appOperateMapper.insert(appOperateDO);

        // 6. 更新应用的下载量
        AppMarketDO updateAppMarketDO = new AppMarketDO();
        updateAppMarketDO.setId(appMarket.getId());
        updateAppMarketDO.setDownloadCount(appMarket.getDownloadCount() + 1);
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
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_UID_IS_REQUIRED);
        AppValidate.notNull(request.getVersion(), ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED);
        AppValidate.notNull(request.getAudit(), ErrorCodeConstants.APP_MARKET_AUDIT_IS_REQUIRED);
        // 2. 审核状态是否支持
        AppValidate.isTrue(IEnumable.containsOfCode(request.getAudit(), AppMarketAuditEnum.class),
                ErrorCodeConstants.APP_MARKET_AUDIT_IS_NOT_SUPPORT, request.getAudit());
        // 3. 查询应用是否存在
        AppValidate.isTrue(appMarketRepository.isExists(request.getUid(), request.getVersion()),
                ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, request.getUid(), request.getVersion());
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
        AppValidate.notNull(request.getOperate(), ErrorCodeConstants.APP_MARKET_VERSION_REQUIRED);
        AppValidate.notNull(request.getOperate(), ErrorCodeConstants.APP_OPERATE_IS_REQUIRED);

        // 2. 查询应用市场的应用并且校验
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getAppUid())
                .eq(AppMarketDO::getVersion, request.getVersion());
        AppMarketDO appMarketDO = appMarketMapper.selectOne(wrapper);
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, request.getAppUid(), request.getVersion());

        AppOperateDO operateDO = AppOperateConvert.INSTANCE.convert(request);
        // 插入操作记录
        appOperateMapper.insert(operateDO);
        // 更新应用市场的操作的数量
        String operate = request.getOperate().toUpperCase();
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .eq(AppMarketDO::getUid, request.getAppUid())
                .eq(AppMarketDO::getVersion, request.getVersion());
        // 此处不支持下载操作
        if (AppOperateTypeEnum.LIKE.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getLikeCount, appMarketDO.getLikeCount() + 1);
        } else if (AppOperateTypeEnum.DOWNLOAD.name().equals(operate)) {
            updateWrapper.set(AppMarketDO::getDownloadCount, appMarketDO.getDownloadCount() + 1);
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
    public List<AppMarketRespVO> listFavorite(String userId) {
        return null;
    }

    /**
     * 获取用户收藏的应用的详情
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     * @return 收藏应用
     */
    @Override
    public AppMarketRespVO getFavoriteApp(String userId, String uid) {
        return null;
    }

    /**
     * 将应用加入到收藏夹
     *
     * @param userId 用户 id
     * @param uid    应用 uid
     */
    @Override
    public void favorite(String userId, String uid) {

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
                AppMarketDO::getCreateTime
        );
    }
}
