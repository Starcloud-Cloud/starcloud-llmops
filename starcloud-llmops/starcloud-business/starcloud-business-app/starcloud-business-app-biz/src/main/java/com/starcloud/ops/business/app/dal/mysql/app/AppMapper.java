package com.starcloud.ops.business.app.dal.mysql.app;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
        // 非管理员用户智能查看普通应用
        if (UserUtils.isNotAdmin()) {
            wrapper.ne(AppDO::getType, AppTypeEnum.SYSTEM.name());
        }
        wrapper.eq(StringUtils.isNotBlank(query.getCategory()), AppDO::getCategory, query.getCategory());
        wrapper.ne(AppDO::getSource, AppSourceEnum.WX_WP.name());
        if (StringUtils.isNotBlank(query.getModel()) && AppModelEnum.CHAT.name().equals(query.getModel())) {
            wrapper.eq(AppDO::getModel, AppModelEnum.CHAT.name());
        } else {
            wrapper.eq(AppDO::getModel, AppModelEnum.COMPLETION.name());
        }
        wrapper.last("ORDER BY sort IS NULL, sort ASC, update_time DESC");
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

    default AppDO getWithoutMaterial(String uid) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class);
        wrapper.select(AppDO::getId, AppDO::getUid, AppDO::getName, AppDO::getModel,
                AppDO::getType, AppDO::getSource, AppDO::getSort, AppDO::getTags,
                AppDO::getCategory, AppDO::getScenes, AppDO::getImages, AppDO::getIcon,
                AppDO::getConfig, AppDO::getDescription, AppDO::getInstallUid, AppDO::getLastPublish,
                AppDO::getExample, AppDO::getDemo);

        wrapper.eq(AppDO::getUid, uid);
        wrapper.eq(AppDO::getDeleted, Boolean.FALSE);
        return this.selectOne(wrapper);
    }

    default AppDO getMaterial(String uid) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class);
        wrapper.select(AppDO::getId, AppDO::getUid, AppDO::getName, AppDO::getMaterialList);
        wrapper.eq(AppDO::getUid, uid);
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
        // AppValidate.isFalse(duplicateName(appDO.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, appDO.getName());
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
//        if (!appDO.getName().equals(app.getName())) {
//            AppValidate.isFalse(duplicateName(app.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE, app.getName());
//        }
        app.setDeleted(Boolean.FALSE);
        app.setId(appDO.getId());
        this.updateById(app);
        return app;
    }

    /**
     * 更新应用信息在删除应用市场时候
     *
     * @param appUid 应用市场 uid
     */
    default void updatePublishUidAfterDeleteMarket(String appUid) {
        LambdaUpdateWrapper<AppDO> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(AppDO::getPublishUid, null);
        wrapper.set(AppDO::getLastPublish, null);
        wrapper.likeRight(AppDO::getUid, appUid);
        this.update(null, wrapper);
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
                AppDO::getSort,
                AppDO::getTags,
                AppDO::getCategory,
                AppDO::getScenes,
                AppDO::getIcon,
                AppDO::getDescription,
                AppDO::getExample,
                AppDO::getDemo,
                AppDO::getPluginList,
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
