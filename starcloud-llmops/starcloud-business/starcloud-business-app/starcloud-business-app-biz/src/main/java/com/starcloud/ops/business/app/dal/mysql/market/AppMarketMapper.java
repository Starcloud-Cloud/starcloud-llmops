package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用市场表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-06-05
 */
@Mapper
public interface AppMarketMapper extends BaseMapper<AppMarketDO> {

    /**
     * 根据应用 uid 获取应用详情
     *
     * @param uid      应用 uid
     * @param isSimple 是否查询部分字段
     * @return 应用详情
     */
    default AppMarketDO get(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppMarketDO> wrapper = queryMapper(isSimple);
        wrapper.eq(AppMarketDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    default AppMarketDO getWithoutMaterial(String uid) {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class);
        wrapper.select(AppMarketDO::getId, AppMarketDO::getUid, AppMarketDO::getName,
                AppMarketDO::getType, AppMarketDO::getModel, AppMarketDO::getVersion,
                AppMarketDO::getLanguage, AppMarketDO::getSort, AppMarketDO::getTags,
                AppMarketDO::getCategory, AppMarketDO::getScenes, AppMarketDO::getImages,
                AppMarketDO::getIcon, AppMarketDO::getFree, AppMarketDO::getCost,
                AppMarketDO::getUsageCount, AppMarketDO::getLikeCount, AppMarketDO::getViewCount,
                AppMarketDO::getInstallCount, AppMarketDO::getConfig, AppMarketDO::getDescription,
                AppMarketDO::getExample, AppMarketDO::getDemo, AppMarketDO::getAudit,
                AppMarketDO::getUpdateTime,AppMarketDO::getCreateTime);
        wrapper.eq(AppMarketDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    default AppMarketDO getMaterial(String uid) {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class);
        wrapper.select(AppMarketDO::getId, AppMarketDO::getName, AppMarketDO::getUid, AppMarketDO::getMaterialList);
        wrapper.eq(AppMarketDO::getUid, uid);
        return this.selectOne(wrapper);
    }

    /**
     * 根据条件获取应用详情
     *
     * @param query 查询条件
     * @return 应用详情
     */
    AppMarketDO getOne(@Param("query") AppMarketQuery query);

    /**
     * 获取应用市场列表选项
     *
     * @param query 查询条件
     * @return 应用列表
     */
    List<AppMarketDO> list(@Param("query") AppMarketListQuery query);

    /**
     * 获取应用市场列表选项
     *
     * @param query 查询条件
     * @return 应用列表
     */
    List<AppMarketDO> listWithoutConfig(@Param("query") AppMarketListQuery query);

    /**
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    default Page<AppMarketDO> page(AppMarketPageQuery query) {
        LambdaQueryWrapper<AppMarketDO> queryMapper = queryMapper(Boolean.TRUE);
        queryMapper.likeRight(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName());
        queryMapper.eq(StringUtils.isNotBlank(query.getCategory()), AppMarketDO::getCategory, query.getCategory());
        if (UserUtils.isNotAdmin()) {
            queryMapper.eq(AppMarketDO::getType, AppTypeEnum.COMMON.name());
        }
        if (StringUtils.isNotBlank(query.getModel()) && AppModelEnum.CHAT.name().equals(query.getModel())) {
            queryMapper.eq(AppMarketDO::getModel, AppModelEnum.CHAT.name());
        } else {
            queryMapper.eq(AppMarketDO::getModel, AppModelEnum.COMPLETION.name());
        }
        queryMapper.last("ORDER BY sort IS NULL, sort ASC, update_time DESC");
        return this.selectPage(PageUtil.page(query), queryMapper);
    }

    /**
     * 创建应用市场应用
     *
     * @param appMarket 应用市场
     * @return 应用市场
     */
    default AppMarketDO create(AppMarketDO appMarket) {
        // 校验应用名称是否重复
        AppValidate.isFalse(duplicateName(appMarket.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, appMarket.getName());
        appMarket.setUsageCount(0);
        appMarket.setLikeCount(0);
        appMarket.setViewCount(0);
        appMarket.setInstallCount(0);
        appMarket.setDeleted(Boolean.FALSE);
        this.insert(appMarket);
        return appMarket;
    }

    /**
     * 修改应用市场应用
     *
     * @param appMarket 应用市场
     * @return 应用市场
     */
    default AppMarketDO modify(AppMarketDO appMarket) {
        // 判断应用是否存在, 不存在无法修改
        AppMarketDO appMarketDO = this.get(appMarket.getUid(), Boolean.TRUE);
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, appMarket.getUid());
        // 名称修改了, 则需要校验名称是否重复
        if (!appMarket.getName().equals(appMarketDO.getName())) {
            AppValidate.isFalse(duplicateName(appMarket.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, appMarket.getName());
        }
        appMarket.setDeleted(Boolean.FALSE);
        appMarket.setId(appMarketDO.getId());
        this.updateById(appMarket);
        return appMarket;
    }

    /**
     * 删除应用
     *
     * @param uid 应用唯一标识
     */
    default void delete(String uid) {
        AppMarketDO appMarketDO = this.get(uid, Boolean.TRUE);
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, uid);
        this.deleteById(appMarketDO.getId());
    }

    /**
     * 判断应用名称是否重复, 只判断 model 为 COMPLETION 的应用，其余的名称可以重复
     *
     * @param name 应用名称
     */
    default Boolean duplicateName(String name) {
        return countCompletionAppByName(name) > 0;
    }

    /**
     * 根据应用名称查询 COMPLETION 模式的应用数量
     *
     * @param name 应用名称
     * @return 应用数量
     */
    Long countCompletionAppByName(@Param("name") String name);

    /**
     * 查询应用市场应用 Wrapper
     *
     * @param isSimple 是否查询部分字段
     * @return 查询包装器
     */
    default LambdaQueryWrapper<AppMarketDO> queryMapper(boolean isSimple) {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class);
        wrapper.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        if (!isSimple) {
            return wrapper;
        }
        wrapper.select(
                AppMarketDO::getId,
                AppMarketDO::getUid,
                AppMarketDO::getName,
                AppMarketDO::getType,
                AppMarketDO::getModel,
                AppMarketDO::getVersion,
                AppMarketDO::getLanguage,
                AppMarketDO::getSort,
                AppMarketDO::getTags,
                AppMarketDO::getCategory,
                AppMarketDO::getScenes,
                AppMarketDO::getImages,
                AppMarketDO::getIcon,
                AppMarketDO::getFree,
                AppMarketDO::getCost,
                AppMarketDO::getUsageCount,
                AppMarketDO::getViewCount,
                AppMarketDO::getLikeCount,
                AppMarketDO::getInstallCount,
                AppMarketDO::getDescription,
                AppMarketDO::getExample,
                AppMarketDO::getDemo,
                AppMarketDO::getPluginList,
                AppMarketDO::getCreator,
                AppMarketDO::getUpdater,
                AppMarketDO::getCreateTime,
                AppMarketDO::getUpdateTime,
                AppMarketDO::getTenantId
        );
        return wrapper;
    }


}
