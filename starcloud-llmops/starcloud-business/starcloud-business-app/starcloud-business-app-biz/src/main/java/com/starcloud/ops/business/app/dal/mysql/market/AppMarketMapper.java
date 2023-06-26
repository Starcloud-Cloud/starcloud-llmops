package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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
     * 根据应用 uid 获取应用详情, 所有字段
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    default AppMarketDO getByUid(String uid) {
        // 获取最新版本的应用
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.name())
                .orderByDesc(AppMarketDO::getVersion);

        List<AppMarketDO> appMarketList = this.selectList(wrapper);
        AppMarketDO appMarket = this.selectOne(wrapper);
        AppValidate.notNull(appMarket, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, uid);
        return appMarket;
    }

    /**
     * 根据应用 uid 获取应用详情, 部分字段
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    default AppMarketDO getByUidSimple(String uid) {
        LambdaQueryWrapper<AppMarketDO> wrapper = simpleQueryMapper()
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.name());
        AppMarketDO appMarket = this.selectOne(wrapper);
        AppValidate.notNull(appMarket, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID, uid);
        return appMarket;
    }

    /**
     * 根据应用 uid 和 版本号 获取应用详情，所有字段
     *
     * @param uid     应用 uid
     * @param version 应用版本号
     * @return 应用详情
     */
    default AppMarketDO getByUidAndVersion(String uid, Integer version) {
        LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getVersion, version)
                .eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.name());
        AppMarketDO appMarket = this.selectOne(wrapper);
        AppValidate.notNull(appMarket, ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID_VERSION, uid, version);
        return appMarket;
    }

    /**
     * 根据应用 uid 和 版本号 获取应用详情，部分字段
     *
     * @param uid     应用 uid
     * @param version 应用版本号
     * @return 应用详情
     */
    default AppMarketDO getByUidAndVersionSimple(String uid, Integer version) {
        LambdaQueryWrapper<AppMarketDO> wrapper = simpleQueryMapper()
                .eq(AppMarketDO::getUid, uid)
                .eq(AppMarketDO::getVersion, version)
                .eq(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.name());
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
    @Update("UPDATE llm_app_market SET audit = CASE WHEN version = #{version} THEN 1 WHEN version != #{version} THEN 2 ELSE audit END WHERE uid = #{uid} AND deleted = 0")
    void approvedAuditByUidAndVersion(@Param("uid") String uid, @Param("version") Integer version);

    /**
     * 查询部分字段查询包装器
     *
     * @return 查询包装器
     */
    static LambdaQueryWrapper<AppMarketDO> simpleQueryMapper() {
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
