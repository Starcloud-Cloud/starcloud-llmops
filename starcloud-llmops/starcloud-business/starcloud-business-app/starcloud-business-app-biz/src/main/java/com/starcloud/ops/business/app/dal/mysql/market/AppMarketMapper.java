package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.LanguageEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
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
     * @param query   查询条件
     * @param isAdmin 是否是管理员
     * @return 应用市场列表
     */
    default Page<AppMarketDO> page(AppMarketPageQuery query, boolean isAdmin) {
        // 构建查询条件
        LambdaQueryWrapper<AppMarketDO> wrapper = pageQueryMapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName());
        if (!isAdmin) {
            wrapper.eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.getCode());
        }
        String local = LocaleContextHolder.getLocale().toString();
        String language = LanguageEnum.ZH_CN.getCode().equals(local) ? LanguageEnum.ZH_CN.getCode() : LanguageEnum.EN_US.getCode();
        wrapper.last("ORDER BY CASE WHEN language = '" + language + "' THEN 0 ELSE 1 END, create_time DESC");
        // 分页查询
        return this.selectPage(PageUtil.page(query), wrapper);
    }

    /**
     * 根据应用 uid 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    default AppMarketDO get(String uid, Integer version, boolean isSimple) {
        // 获取最新版本的应用
        LambdaQueryWrapper<AppMarketDO> wrapper = isSimple ? simpleQueryMapper() : Wrappers.lambdaQuery(AppMarketDO.class);
        if (version == null) {
            wrapper.eq(AppMarketDO::getUid, uid);
            wrapper.eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.getCode());
            wrapper.orderByDesc(AppMarketDO::getVersion);

            List<AppMarketDO> appMarketList = this.selectList(wrapper);
            AppValidate.notEmpty(appMarketList, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, uid);
            return appMarketList.get(0);
        }
        // 版本号不为空，获取指定版本的应用
        wrapper.eq(AppMarketDO::getUid, uid);
        wrapper.eq(AppMarketDO::getVersion, version);
        AppMarketDO appMarket = this.selectOne(wrapper);
        AppValidate.notNull(appMarket, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, uid, version);
        return appMarket;
    }

    /**
     * 审核数据，审核通过：该版本的审核状态改为已通过，其余的版本改为未通过
     *
     * @param uid     应用uid
     * @param version 版本号
     */
    @SuppressWarnings("all")
    @Update("UPDATE llm_app_market SET audit = CASE WHEN version = #{version} THEN 1 WHEN version != #{version} THEN 2 ELSE audit END WHERE uid = #{uid} AND deleted = 0")
    void approvedAuditByUidAndVersion(@Param("uid") String uid, @Param("version") Integer version);

    /**
     * 查询部分字段查询包装器
     *
     * @return 查询包装器
     */
    static LambdaQueryWrapper<AppMarketDO> simpleQueryMapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class).select(
                AppMarketDO::getId,
                AppMarketDO::getUid,
                AppMarketDO::getName,
                AppMarketDO::getModel,
                AppMarketDO::getVersion,
                AppMarketDO::getCategories,
                AppMarketDO::getScenes,
                AppMarketDO::getImages,
                AppMarketDO::getFree,
                AppMarketDO::getCost,
                AppMarketDO::getViewCount,
                AppMarketDO::getLikeCount,
                AppMarketDO::getInstallCount
        );
    }

    /**
     * 查询部分字段查询包装器
     *
     * @return 查询包装器
     */
    static LambdaQueryWrapper<AppMarketDO> pageQueryMapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class).select(
                AppMarketDO::getUid,
                AppMarketDO::getName,
                AppMarketDO::getModel,
                AppMarketDO::getVersion,
                AppMarketDO::getLanguage,
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
                AppMarketDO::getInstallCount,
                AppMarketDO::getCreateTime
        );
    }

}
