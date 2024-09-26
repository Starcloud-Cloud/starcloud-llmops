package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListGroupByCategoryQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketOptionListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketGroupCategoryRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.operate.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.favorite.AppFavoriteMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.operate.AppOperateMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
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
    private AppPublishMapper appPublishMapper;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private CreativeMaterialManager creativeMaterialManager;

    /**
     * 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    @Override
    public AppMarketRespVO get(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.MARKET_UID_REQUIRED);
        // 查询应用市场信息
        AppMarketDO appMarket = appMarketMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(appMarket, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, uid);

        // 转换应用数据
        AppMarketRespVO response = AppMarketConvert.INSTANCE.convertResponse(appMarket);

//        // 获取当前登录用户并且校验
//        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
//        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);
//
//        // 查询是否收藏
//        AppFavoriteDO favorite = appFavoritesMapper.get(appMarket.getUid(), String.valueOf(loginUserId));
//        if (Objects.nonNull(favorite)) {
//            response.setIsFavorite(Boolean.TRUE);
//        } else {
//            response.setIsFavorite(Boolean.FALSE);
//        }

        return response;
    }

    /**
     * 获取应用详情
     *
     * @param query 查询条件
     * @return 应用详情
     */
    @Override
    public AppMarketRespVO getOne(AppMarketQuery query) {
        AppMarketDO one = appMarketMapper.getOne(query);
        AppValidate.notNull(one, ErrorCodeConstants.MARKET_APP_NON_EXISTENT);
        return AppMarketConvert.INSTANCE.convertResponse(one);
    }

    /**
     * 获取应用详情并且增加查看量增加
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppMarketRespVO getAndIncreaseView(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.MARKET_UID_REQUIRED);
        // 查询应用市场信息
        AppMarketDO appMarket = appMarketMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(appMarket, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, uid);

        // 转换应用数据
        AppMarketRespVO response = AppMarketConvert.INSTANCE.convertResponse(appMarket);

        // 迁移旧素材数据
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(appMarket.getType())) {
            WorkflowStepWrapperRespVO stepByHandler = response.getStepByHandler(MaterialActionHandler.class.getSimpleName());
            if (CollectionUtil.isNotEmpty(appMarket.getMaterialList()) && Objects.nonNull(stepByHandler)) {
                // 从数据库迁移
                creativeMaterialManager.migrateFromData(appMarket.getName(), appMarket.getUid(),
                        MaterialBindTypeEnum.APP_MARKET.getCode(), stepByHandler, appMarket.getMaterialList(), Long.valueOf(appMarket.getCreator()));
                appMarket.setMaterialList(Collections.emptyList());
                appMarketMapper.updateById(appMarket);

            } else if (CollectionUtil.isEmpty(appMarket.getMaterialList()) && Objects.nonNull(stepByHandler)) {
                String stepVariableValue = stepByHandler.getVariableToString(CreativeConstants.LIBRARY_QUERY);
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(stepVariableValue)) {
                    // 新建
                    creativeMaterialManager.createEmptyLibrary(appMarket.getName(), appMarket.getUid(),
                            MaterialBindTypeEnum.APP_MARKET.getCode(), Long.valueOf(appMarket.getCreator()));
                } else {
                    // 从变量迁移
                    creativeMaterialManager.migrateFromConfig(appMarket.getName(), appMarket.getUid(),
                            MaterialBindTypeEnum.APP_MARKET.getCode(), stepVariableValue, Long.valueOf(appMarket.getCreator()));
                    stepByHandler.putVariable(CreativeConstants.LIBRARY_QUERY, "");
                    appMarket.setConfig(JsonUtils.toJsonString(response.getWorkflowConfig()));
                    appMarketMapper.updateById(appMarket);
                }
            }
        }

        // 获取当前登录用户并且校验
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 查询是否收藏
        AppFavoriteDO favorite = appFavoritesMapper.get(appMarket.getUid(), String.valueOf(loginUserId));
        if (Objects.nonNull(favorite)) {
            response.setIsFavorite(Boolean.TRUE);
        } else {
            response.setIsFavorite(Boolean.FALSE);
        }

        // 操作表中插入一条查看记录, 并且增加查看量
        appOperateMapper.create(appMarket.getUid(), appMarket.getVersion(), AppOperateTypeEnum.VIEW.name(), String.valueOf(loginUserId));

        // 增加查看量
        Integer viewCount = appMarket.getViewCount() + 1;
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class);
        updateWrapper.set(AppMarketDO::getViewCount, viewCount);
        // 更新时间保持不变
        updateWrapper.set(AppMarketDO::getUpdateTime, appMarket.getUpdateTime());
        updateWrapper.eq(AppMarketDO::getId, appMarket.getId());
        appMarketMapper.update(null, updateWrapper);

        // 转换并且返回应用数据
        response.setViewCount(viewCount);
        return response;
    }

    /**
     * 根据条件查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    @Override
    public List<AppMarketRespVO> list(AppMarketListQuery query) {
        // 查询应用市场列表
        List<AppMarketDO> list = appMarketMapper.list(query);
        return CollectionUtil.emptyIfNull(list).stream().filter(Objects::nonNull).map(AppMarketConvert.INSTANCE::convertResponse).collect(Collectors.toList());
    }

    /**
     * 根据条件查询应用市场列表 Option
     *
     * @param query 查询条件
     * @return 应用市场列表 Option
     */
    @Override
    public List<Option> listOption(AppMarketOptionListQuery query) {
        query.setTypeList(Collections.singletonList(AppTypeEnum.SYSTEM.name()));
        query.setModel(AppModelEnum.COMPLETION.name());
        // 如果传入 tags 则使用 tags，未传入 tags 时候且 tagType 不为空的时候，进行处理
        if (CollectionUtil.isEmpty(query.getTags()) && StringUtils.isNotBlank(query.getTagType())) {
            AppMarketTagTypeEnum tagTypeEnum = AppMarketTagTypeEnum.of(query.getTagType());
            if (Objects.isNull(tagTypeEnum)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.MARKET_TAG_TYPE_NOT_SUPPORTED, query.getTagType());
            }
            query.setTags(tagTypeEnum.getTags());
        }
        List<AppMarketDO> list = appMarketMapper.list(query);
        return CollectionUtil.emptyIfNull(list).stream().filter(Objects::nonNull).map(item -> {
            Option option = new Option();
            option.setLabel(item.getName());
            option.setValue(item.getUid());
            return option;
        }).collect(Collectors.toList());
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

        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 查询用户收藏的应用Map, key为应用市场 UID
        Map<String, AppFavoriteDO> favoriteMap = appFavoritesMapper.mapByUserId(String.valueOf(loginUserId));

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
                            .peek(item -> {
                                if (CollectionUtil.isNotEmpty(favoriteMap)) {
                                    item.setIsFavorite(favoriteMap.containsKey(item.getUid()));
                                }
                            })
                            .collect(Collectors.toList());
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

        // 查询应用市场列表
        AppMarketListQuery appMarketListQuery = new AppMarketListQuery();
        // 非管理员，只能查询普通应用
        if (UserUtils.isNotAdmin()) {
            List<String> typeList = Lists.newArrayList();
            typeList.add(AppTypeEnum.COMMON.name());
            typeList.add(AppTypeEnum.MEDIA_MATRIX.name());
            appMarketListQuery.setTypeList(typeList);
        }
        // 只查询 COMPLETION 的应用
        appMarketListQuery.setModel(AppModelEnum.COMPLETION.name());
        List<AppMarketDO> appMarketList = appMarketMapper.listWithoutConfig(appMarketListQuery);

        // 如果为空，直接返回
        if (CollectionUtil.isEmpty(appMarketList)) {
            return result;
        }

        // 按照类别分组
        Map<String, List<AppMarketRespVO>> appMap = CollectionUtil.emptyIfNull(appMarketList).parallelStream()
                .filter(item -> StringUtils.isNotBlank(item.getCategory()))
                .map(AppMarketConvert.INSTANCE::convertResponseWithId)
                .peek(item -> {
                    if (CollectionUtil.isNotEmpty(favoriteMap)) {
                        item.setIsFavorite(favoriteMap.containsKey(item.getUid()));
                    }
                })
                .collect(Collectors.groupingBy(AppMarketRespVO::getCategory));

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

            marketList = marketList.stream()
                    .sorted(Comparator.comparing(AppMarketRespVO::getSort, Comparator.nullsLast(Long::compareTo))
                            .thenComparingLong(AppMarketRespVO::getId)
                    )
                    .peek(item -> item.setId(null))
                    .collect(Collectors.toList());

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
     * 创建应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void create(AppMarketReqVO request) {
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(request);
        appMarketEntity.setCreator(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appMarketEntity.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appMarketEntity.setCreateTime(LocalDateTime.now());
        appMarketEntity.setUpdateTime(LocalDateTime.now());
        appMarketEntity.insert();
    }

    /**
     * 创建应用市场的应用为我的应用
     *
     * @param appMarketUid 应用信息
     */
    @Override
    public String createSameApp(String appMarketUid) {
        AppMarketDO appMarketDO = appMarketMapper.get(appMarketUid, Boolean.FALSE);

        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(appMarketDO);
        AppEntity appEntity = AppConvert.INSTANCE.convert(appMarketEntity);

        appEntity.setUid(null);
        // 原来应用市场的名称 + 6位随机数(字母+数字)。
        String appName = appEntity.getName() + RandomStringUtils.randomAlphanumeric(6);
        appEntity.setName(randomName(appName, appEntity.getName()));
        appEntity.setSource(AppSourceEnum.MARKET.name());
        appEntity.setCreator(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appEntity.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appEntity.setCreateTime(LocalDateTime.now());
        appEntity.setUpdateTime(LocalDateTime.now());
        appEntity.insert();

        // 为新的应用创建一个新的素材库
        MaterialLibrarySliceAppReqVO source = new MaterialLibrarySliceAppReqVO();
        source.setAppUid(appMarketUid);

        MaterialLibraryAppReqVO target = new MaterialLibraryAppReqVO();
        target.setAppUid(appEntity.getUid());
        target.setAppName(appEntity.getName());
        target.setAppType(MaterialBindTypeEnum.APP_MAY.getCode());
        target.setUserId(WebFrameworkUtils.getLoginUserId());

        creativeMaterialManager.copyLibrary(source, target, PluginBindTypeEnum.owner);
        return appEntity.getUid();
    }

    /**
     * 更新应用市场的应用
     *
     * @param request 应用信息
     */
    @Override
    public void modify(AppMarketUpdateReqVO request) {
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(request);
        appMarketEntity.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appMarketEntity.setUpdateTime(LocalDateTime.now());
        appMarketEntity.update();
    }

    /**
     * 删除应用市场的应用
     *
     * @param uid 应用 uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        appMarketMapper.delete(uid);
        appFavoritesMapper.deleteByMarketUid(uid);
        String appUid = appPublishMapper.selectAppUidByMarketUid(uid);
        if (StringUtils.isNotBlank(appUid)) {
            appMapper.updatePublishUidAfterDeleteMarket(appUid);
        }
        appPublishMapper.updateAfterDeleteMarket(uid);
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
        if (StringUtils.isBlank(request.getUserId())) {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            if (loginUserId == null) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
            }
            request.setUserId(Long.toString(loginUserId));
        }
        if (Objects.nonNull(appMarketDO.getTenantId())) {
            request.setTenantId(appMarketDO.getTenantId());
        }
        // 转换数据
        AppOperateDO operateDO = AppOperateConvert.INSTANCE.convert(request);
        // 插入操作记录
        appOperateMapper.insert(operateDO);
        // 更新应用市场的操作的数量
        String operate = request.getOperate().toUpperCase();
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class);
        updateWrapper.eq(AppMarketDO::getUid, request.getAppUid());
        updateWrapper.eq(AppMarketDO::getVersion, appMarketDO.getVersion());

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
        // 更新时间保持不变
        updateWrapper.set(AppMarketDO::getUpdateTime, appMarketDO.getUpdateTime());
        appMarketMapper.update(appMarketDO, updateWrapper);
    }

    /**
     * 递归生成随机名称
     *
     * @param name 名称
     */
    public String randomName(String name, String baseName) {
        if (appMapper.duplicateName(name)) {
            String newName = baseName + RandomStringUtils.randomAlphanumeric(6);
            return randomName(newName, baseName);
        } else {
            return name;
        }
    }
}
