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
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
        LambdaQueryWrapper<AppDO> wrapper = queryWrapper(Boolean.TRUE);
        wrapper.likeRight(StringUtils.isNotBlank(query.getName()), AppDO::getName, query.getName());
        wrapper.ne(AppDO::getSource, AppSourceEnum.WX_WP.name());
        if (StringUtils.isNotBlank(query.getModel()) && AppModelEnum.CHAT.name().equals(query.getModel())) {
            wrapper.eq(AppDO::getModel, AppModelEnum.CHAT.name());
        } else {
            wrapper.ne(AppDO::getModel, AppModelEnum.CHAT.name());
        }
        wrapper.orderByDesc(AppDO::getCreateTime);
        return this.selectPage(PageUtil.page(query), wrapper);
    }

    /**
     * 根据应用 uid 获取应用详情
     *
     * @param uid 应用 uid
     * @return 应用详情
     */
    default AppDO get(String uid, boolean isSimple) {
        LambdaQueryWrapper<AppDO> wrapper = queryWrapper(isSimple);
        wrapper.eq(AppDO::getUid, uid);
        wrapper.eq(AppDO::getDeleted, Boolean.FALSE);
        return this.selectOne(wrapper);
    }

    /**
     * 创建应用市场应用
     *
     * @param appDO 应用市场
     * @return 应用市场
     */
    default AppDO create(AppDO appDO) {
        // 校验应用名称是否重复
        AppValidate.isFalse(duplicateName(appDO.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, appDO.getName());
        appDO.setDeleted(Boolean.FALSE);
        appDO.setPublishUid(null);
        appDO.setLastPublish(null);
        this.insert(appDO);
        return appDO;
    }

    /**
     * 修改应用市场应用
     *
     * @param app 应用市场
     * @return 应用市场
     */
    default AppDO modify(AppDO app) {
        // 判断应用是否存在, 不存在无法修改
        AppDO appDO = this.get(app.getUid(), Boolean.TRUE);
        AppValidate.notNull(appDO, ErrorCodeConstants.APP_NON_EXISTENT, app.getUid());
        // 名称修改了，需要校验名称是否重复
        if (!appDO.getName().equals(app.getName())) {
            AppValidate.isFalse(duplicateName(app.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, app.getName());
        }
        app.setDeleted(Boolean.FALSE);
        app.setId(appDO.getId());
        this.updateById(app);
        return app;
    }

    /**
     * 删除应用
     *
     * @param uid 应用唯一标识
     */
    default void delete(String uid) {
        AppDO app = this.get(uid, Boolean.TRUE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, uid);
        this.deleteById(app.getId());
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
     * 查询应用是否已经安装
     *
     * @param marketUid 应用市场应用 Uid
     * @param userId    用户 id
     * @return 安装状态
     */
    default InstalledRespVO verifyHasInstalled(String marketUid, String userId) {
        /*
            1. 当前用户，
            2. 未删除，
            3. 已经发布的应用
            4. 已经安装的应用

         */
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class);
        wrapper.select(AppDO::getUid, AppDO::getInstallUid, AppDO::getPublishUid);
        wrapper.eq(AppDO::getCreator, userId);
        wrapper.eq(AppDO::getDeleted, Boolean.FALSE);
        wrapper.and(and -> and
                .likeRight(AppDO::getPublishUid, marketUid)
                .or()
                .likeRight(AppDO::getInstallUid, marketUid).eq(AppDO::getType, AppTypeEnum.INSTALLED.name())
        );

        AppDO appDO = this.selectOne(wrapper);
        if (Objects.isNull(appDO)) {
            return InstalledRespVO.of(AppInstallStatusEnum.UNINSTALLED.name(), null, null);
        }
        String uidVersion = Optional.ofNullable(appDO.getInstallUid()).orElse(appDO.getPublishUid());

        return InstalledRespVO.of(AppInstallStatusEnum.INSTALLED.name(), AppUtils.obtainVersion(uidVersion), null);
    }

    /**
     * 获取查询条件
     *
     * @param isSimple 是否简单查询
     * @return 查询条件
     */
    default LambdaQueryWrapper<AppDO> queryWrapper(boolean isSimple) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class);
        wrapper.eq(AppDO::getDeleted, Boolean.FALSE);
        if (!isSimple) {
            return wrapper;
        }
        wrapper.select(
                AppDO::getId,
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
                AppDO::getImages,
                AppDO::getCreator,
                AppDO::getUpdater,
                AppDO::getCreateTime,
                AppDO::getUpdateTime,
                AppDO::getTenantId
        );
        return wrapper;
    }

}
