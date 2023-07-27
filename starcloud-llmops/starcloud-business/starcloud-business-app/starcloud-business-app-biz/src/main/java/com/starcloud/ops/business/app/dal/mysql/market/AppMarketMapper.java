package com.starcloud.ops.business.app.dal.mysql.market;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.i18n.LocaleContextHolder;

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
        queryMapper.likeLeft(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName());
        queryMapper.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        String local = LocaleContextHolder.getLocale().toString();
        String language = LanguageEnum.ZH_CN.getCode().equals(local) ? LanguageEnum.ZH_CN.getCode() : LanguageEnum.EN_US.getCode();
        // 先按照语言排序，再按照使用量排序
        queryMapper.last("ORDER BY CASE WHEN language = '" + language + "' THEN 0 ELSE 1 END, usage_count DESC, view_count Desc, create_time DESC");
        // 分页查询
        return this.selectPage(PageUtil.page(query), queryMapper);
    }

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
        AppValidate.isFalse(duplicateName(appMarket.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
        appMarket.setUid(IdUtil.fastSimpleUUID());
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
        // 校验应用名称是否重复
        AppValidate.isFalse(duplicateName(appMarket.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
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
     * 判断应用名称是否重复
     *
     * @param name 应用名称
     */
    default Boolean duplicateName(String name) {
        return this.selectCount(Wrappers.lambdaQuery(AppMarketDO.class).eq(AppMarketDO::getName, name)) > 0;
    }

    /**
     * 查询应用市场应用 Wrapper
     *
     * @param isSimple 是否查询部分字段
     * @return 查询包装器
     */
    static LambdaQueryWrapper<AppMarketDO> queryMapper(boolean isSimple) {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class);
        wrapper.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        if (!isSimple) {
            return wrapper;
        }
        wrapper.select(AppMarketDO::getId);
        wrapper.select(AppMarketDO::getUid);
        wrapper.select(AppMarketDO::getName);
        wrapper.select(AppMarketDO::getModel);
        wrapper.select(AppMarketDO::getVersion);
        wrapper.select(AppMarketDO::getLanguage);
        wrapper.select(AppMarketDO::getCategories);
        wrapper.select(AppMarketDO::getScenes);
        wrapper.select(AppMarketDO::getImages);
        wrapper.select(AppMarketDO::getFree);
        wrapper.select(AppMarketDO::getCost);
        wrapper.select(AppMarketDO::getUsageCount);
        wrapper.select(AppMarketDO::getViewCount);
        wrapper.select(AppMarketDO::getLikeCount);
        wrapper.select(AppMarketDO::getInstallCount);
        wrapper.select(AppMarketDO::getCreateTime);
        return wrapper;
    }
}
