package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.response.InstalledRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppInstallReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListGroupByCategoryQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketGroupCategoryRespVO;
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
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private AppOperateMapper appOperateMapper;

    @Resource
    private AppFavoriteMapper appFavoritesMapper;

    @Resource
    private AppDictionaryService appDictionaryService;

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    @Override
    public PageResp<AppMarketRespVO> page(AppMarketPageQuery query) {
        // 分页查询
        Page<AppMarketDO> page = appMarketMapper.page(query);
        // 转换并且返回数据
        List<AppMarketRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppMarketConvert.INSTANCE::convertResponse).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据分类Code查询应用市场列表
     *
     * @param query 查询条件
     * @return 分组列表
     */
    @Override
    public List<AppMarketGroupCategoryRespVO> listGroupByCategory(AppMarketListGroupByCategoryQuery query) {
        List<AppMarketGroupCategoryRespVO> result = Lists.newArrayList();

        // 查询应用市场列表
        AppMarketListQuery appMarketListQuery = new AppMarketListQuery();
        appMarketListQuery.setModel(AppModelEnum.COMPLETION.name());
        List<AppMarketDO> appMarketList = appMarketMapper.defaultListMarketApp(appMarketListQuery);

        // 是否查询热门搜索的应用
        if (query.getIsHot()) {
            List<String> nameList = appDictionaryService.hotSearchMarketAppNameList();
            if (CollectionUtil.isNotEmpty(nameList)) {
                LambdaQueryWrapper<AppMarketDO> hotSearchListWrapper = appMarketMapper.queryMapper(Boolean.TRUE);
                hotSearchListWrapper.in(AppMarketDO::getName, nameList);
                List<AppMarketDO> hotSearchList = appMarketMapper.selectList(hotSearchListWrapper);
                if (CollectionUtil.isNotEmpty(hotSearchList)) {
                    List<AppMarketRespVO> collect = nameList.stream()
                            .map(name -> hotSearchList.stream().filter(item -> name.equals(item.getName())).findFirst().orElse(null))
                            .filter(Objects::nonNull)
                            .map(AppMarketConvert.INSTANCE::convertResponse)
                            .collect(Collectors.toList());
                    Collections.reverse(collect);
                    AppMarketGroupCategoryRespVO hotSearchResponse = new AppMarketGroupCategoryRespVO();
                    hotSearchResponse.setName("热门");
                    hotSearchResponse.setCode("HOT");
                    hotSearchResponse.setParentCode("ROOT");
                    hotSearchResponse.setIcon("hot");
                    hotSearchResponse.setAppList(collect);
                    result.add(hotSearchResponse);
                }
            }
        }

        // 如果为空，直接返回
        if (CollectionUtil.isEmpty(appMarketList)) {
            return result;
        }

        // 按照类别分组
        Map<String, List<AppMarketRespVO>> appMap = CollectionUtil.emptyIfNull(appMarketList).parallelStream()
                .filter(item -> StringUtils.isNotBlank(item.getCategory()))
                .map(AppMarketConvert.INSTANCE::convertResponse).collect(Collectors.groupingBy(AppMarketRespVO::getCategory));

        // 目前是两层树，二级分类。
        List<AppCategoryVO> categoryTreeList = appDictionaryService.categoryTree();

        // 转换数据
        for (AppCategoryVO category : categoryTreeList) {
            // 获取当前分类下的应用列表
            List<AppMarketRespVO> marketList = appMap.getOrDefault(category.getCode(), Lists.newArrayList());
            CollectionUtil.emptyIfNull(category.getChildren()).stream().map(item -> appMap.getOrDefault(item.getCode(), Lists.newArrayList())).forEach(marketList::addAll);
            // 如果为空，忽略
            if (marketList.isEmpty()) {
                continue;
            }
            // 转换数据
            AppMarketGroupCategoryRespVO categoryResponse = new AppMarketGroupCategoryRespVO();
            categoryResponse.setName(category.getName());
            categoryResponse.setCode(category.getCode());
            categoryResponse.setParentCode(category.getParentCode());
            categoryResponse.setIcon(category.getIcon());
            categoryResponse.setImage(category.getImage());
            categoryResponse.setAppList(marketList);
            result.add(categoryResponse);
        }

        return result;
    }

    /**
     * 获取应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    @Override
    public List<Option> listMarketAppOption(AppMarketListQuery query) {
        query.setModel(AppModelEnum.COMPLETION.name());
        // 查询应用市场列表
        List<AppMarketDO> list = appMarketMapper.listMarketApp(query);
        // 转换并且返回数据
        return CollectionUtil.emptyIfNull(list).stream().filter(Objects::nonNull).map(item -> {
            Option option = new Option();
            option.setLabel(item.getName());
            option.setValue(item.getUid());
            return option;
        }).collect(Collectors.toList());
    }

    /**
     * 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMarketRespVO get(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.MARKET_UID_REQUIRED);
        // 查询应用市场信息
        AppMarketDO appMarketDO = appMarketMapper.get(uid, Boolean.FALSE);

        // 获取当前用户是否安装了该应用的信息
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }

        // 获取当前用户是否安装了该应用的信息
        InstalledRespVO installedRespVO = appMapper.verifyHasInstalled(appMarketDO.getUid(), Long.toString(loginUserId));

        // 操作表中插入一条查看记录, 并且增加查看量
        appOperateMapper.create(appMarketDO.getUid(), appMarketDO.getVersion(), AppOperateTypeEnum.VIEW.name(), Long.toString(loginUserId));
        Integer viewCount = appMarketDO.getViewCount() + 1;
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class);
        updateWrapper.set(AppMarketDO::getViewCount, viewCount);
        updateWrapper.eq(AppMarketDO::getId, appMarketDO.getId());
        appMarketMapper.update(null, updateWrapper);

        // 转换并且返回应用数据
        appMarketDO.setViewCount(viewCount);
        AppMarketRespVO appMarketRespVO = AppMarketConvert.INSTANCE.convertResponse(appMarketDO);
        appMarketRespVO.setInstallInfo(installedRespVO);
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
     * @param uid 应用 uid
     */
    @Override
    public void delete(String uid) {
        appMarketMapper.delete(uid);
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
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_UID_REQUIRED);
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }

        // 2. 查询应用市场应用并且校验
        AppMarketDO appMarket = appMarketMapper.get(request.getUid(), Boolean.FALSE);
        AppValidate.notNull(appMarket, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, request.getUid());

        // 3. 校验当前用户是否已经安装过该用户, 如果安装过，抛出异常
        InstalledRespVO installed = appMapper.verifyHasInstalled(request.getUid(), Long.toString(loginUserId));
        AppValidate.isTrue(InstalledRespVO.isInstalled(installed), ErrorCodeConstants.MARKET_APP_INSTALLED);

        // 4. 说明没有安装过，需要安装
        AppEntity appEntity = AppConvert.INSTANCE.convert(appMarket);
        appEntity.insert();

        // 5. 操作表中插入一条数据
        appOperateMapper.create(appMarket.getUid(), appMarket.getVersion(), AppOperateTypeEnum.INSTALLED.name(), Long.toString(loginUserId));

        // 6. 更新应用的安装量
        AppMarketDO updateAppMarketDO = new AppMarketDO();
        updateAppMarketDO.setId(appMarket.getId());
        updateAppMarketDO.setInstallCount(appMarket.getInstallCount() + 1);
        appMarketMapper.updateById(updateAppMarketDO);
    }

    /**
     * 应用操作
     *
     * @param request 操作请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void operate(AppOperateReqVO request) {
        // 2. 查询应用市场的应用, 如果不存在，抛出异常
        AppMarketDO appMarketDO = appMarketMapper.get(request.getAppUid(), Boolean.TRUE);

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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.MARKET_OPERATE_NOT_SUPPORTED, request.getOperate());
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
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_UID_REQUIRED);
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
        AppValidate.isNull(favoriteDO, ErrorCodeConstants.FAVORITE_APP_BEAN, uid);
        // 查询应用市场的应用, 如果不存在，抛出异常
        AppMarketDO appMarketDO = appMarketMapper.get(uid, Boolean.FALSE);

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
        AppValidate.notNull(favoriteDO, ErrorCodeConstants.FAVORITE_APP_NON_EXISTENT, uid);

        // 删除收藏记录
        appFavoritesMapper.delete(Wrappers.lambdaQuery(AppFavoriteDO.class)
                .eq(AppFavoriteDO::getAppUid, uid).eq(AppFavoriteDO::getUser, userId));
    }
}
