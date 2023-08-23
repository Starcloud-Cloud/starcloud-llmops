package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.i18n.LocaleContextHolder;

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
     * 分页查询应用市场列表
     *
     * @param query 查询条件
     * @return 应用市场列表
     */
    default Page<AppMarketDO> page(AppMarketPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AppMarketDO> queryMapper = queryMapper(Boolean.TRUE);
        queryMapper.likeRight(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName());
        queryMapper.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        if (StringUtils.isNotBlank(query.getModel()) && AppModelEnum.CHAT.name().equals(query.getModel())) {
            queryMapper.eq(AppMarketDO::getModel, AppModelEnum.CHAT.name());
        }

        String local = LocaleContextHolder.getLocale().toString();
        String language = LanguageEnum.ZH_CN.getCode().equals(local) ? LanguageEnum.ZH_CN.getCode() : LanguageEnum.EN_US.getCode();
        // 先按照语言排序，再按照使用量排序
        queryMapper.last("ORDER BY CASE WHEN language = '" + language + "' THEN 0 ELSE 1 END, usage_count DESC, view_count Desc, create_time DESC");
        // 分页查询
        return this.selectPage(PageUtil.page(query), queryMapper);
    }

    /**
     * 获取应用市场列表选项
     *
     * @param query 查询条件
     * @return 应用列表
     */
    List<AppMarketDO> listMarketApp(@Param("query") AppMarketListQuery query);

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
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, appMarket.getUid());
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
        AppValidate.notNull(appMarketDO, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, uid);
        this.deleteById(appMarketDO.getId());
    }

    /**
     * 判断应用名称是否重复, 只判断 model 为 COMPLETION 的应用，其余的名称可以重复
     *
     * @param name 应用名称
     */
    default Boolean duplicateName(String name) {
        return this.selectCount(Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getModel, AppModelEnum.COMPLETION.name()).eq(AppMarketDO::getName, name)) > 0;
    }

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
                AppMarketDO::getModel,
                AppMarketDO::getVersion,
                AppMarketDO::getLanguage,
                AppMarketDO::getTags,
                AppMarketDO::getCategories,
                AppMarketDO::getScenes,
                AppMarketDO::getImages,
                AppMarketDO::getFree,
                AppMarketDO::getCost,
                AppMarketDO::getUsageCount,
                AppMarketDO::getViewCount,
                AppMarketDO::getLikeCount,
                AppMarketDO::getInstallCount,
                AppMarketDO::getDescription,
                AppMarketDO::getCreator,
                AppMarketDO::getUpdater,
                AppMarketDO::getCreateTime,
                AppMarketDO::getUpdateTime
        );
        return wrapper;
    }


}
