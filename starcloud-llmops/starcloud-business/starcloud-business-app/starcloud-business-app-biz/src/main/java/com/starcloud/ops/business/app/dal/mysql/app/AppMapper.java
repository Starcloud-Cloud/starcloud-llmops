package com.starcloud.ops.business.app.dal.mysql.app;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.response.InstalledRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppInstallStatusEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;
import java.util.Optional;

/**
 * 应用表 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Mapper
public interface AppMapper extends BaseMapperX<AppDO> {

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    default Page<AppDO> page(AppPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AppDO> wrapper = pageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppDO::getName, query.getName())
                .ne(AppDO::getSource, AppSourceEnum.WX_WP.name())
                .orderByDesc(AppDO::getCreateTime);
        return this.selectPage(PageUtil.page(query), wrapper);
    }

    /**
     * 根据应用 uid 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    default AppDO getByUid(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppDO> wrapper = isSimple ? simpleQueryWrapper() : Wrappers.lambdaQuery(AppDO.class);
        wrapper.eq(AppDO::getUid, uid);
        AppDO app = this.selectOne(wrapper);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return app;
    }

    /**
     * 判断应用名称是否重复
     *
     * @param name 应用名称
     */
    default Boolean duplicateName(String name) {
        return this.selectCount(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getName, name)) > 0;
    }

    /**
     * 查询应用是否已经安装
     *
     * @param marketUid 应用市场应用 Uid
     * @param userId    用户 id
     * @return 安装状态
     */
    default InstalledRespVO verifyHasInstalled(String marketUid, String userId) {
        // 查询应用是否已经安装; INSTALLED 的时候，比较 installUid，PUBLISHED 的时候，比较 publishUid
        // 因为发布过的应用也不应该可以再次安装，如果两个都有值，说明用户先是安装了应用，然后发布了应用，这个时候，应该是已经安装的
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .select(AppDO::getUid, AppDO::getInstallUid, AppDO::getPublishUid)
                .eq(AppDO::getCreator, userId)
                .and(and -> and
                        .likeLeft(AppDO::getInstallUid, marketUid).eq(AppDO::getType, AppTypeEnum.INSTALLED.name())
                        .or()
                        .likeLeft(AppDO::getPublishUid, marketUid).eq(AppDO::getType, AppTypeEnum.PUBLISHED.name())
                );

        AppDO appDO = this.selectOne(wrapper);
        if (Objects.isNull(appDO)) {
            return InstalledRespVO.of(AppInstallStatusEnum.UNINSTALLED.name(), null, null);
        }
        String uidVersion = Optional.ofNullable(appDO.getInstallUid()).orElse(appDO.getPublishUid());

        return InstalledRespVO.of(AppInstallStatusEnum.INSTALLED.name(), AppUtils.obtainVersion(uidVersion), null);
    }

    /**
     * 简单查询条件
     *
     * @return 查询条件
     */
    static LambdaQueryWrapper<AppDO> simpleQueryWrapper() {
        return Wrappers.lambdaQuery(AppDO.class).select(
                AppDO::getId,
                AppDO::getUid,
                AppDO::getName,
                AppDO::getType,
                AppDO::getModel,
                AppDO::getSource,
                AppDO::getCategories,
                AppDO::getScenes,
                AppDO::getIcon,
                AppDO::getPublishUid,
                AppDO::getInstallUid
        );
    }

    /**
     * 简单查询条件
     *
     * @return 查询条件
     */
    static LambdaQueryWrapper<AppDO> pageQueryWrapper() {
        return Wrappers.lambdaQuery(AppDO.class).select(
                AppDO::getUid,
                AppDO::getName,
                AppDO::getType,
                AppDO::getModel,
                AppDO::getSource,
                AppDO::getTags,
                AppDO::getCategories,
                AppDO::getScenes,
                AppDO::getIcon,
                AppDO::getDescription,
                AppDO::getPublishUid,
                AppDO::getInstallUid,
                AppDO::getCreator,
                AppDO::getUpdater,
                AppDO::getCreateTime,
                AppDO::getUpdateTime
        );
    }
}
