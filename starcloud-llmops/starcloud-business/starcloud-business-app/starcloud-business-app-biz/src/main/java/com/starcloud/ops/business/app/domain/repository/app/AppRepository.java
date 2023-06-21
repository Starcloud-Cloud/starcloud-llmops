package com.starcloud.ops.business.app.domain.repository.app;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * App Repository
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Repository
public class AppRepository {

    @Resource
    private AppMapper appMapper;

    /**
     * 根据 uid 查询应用
     *
     * @param uid 应用唯一标识
     * @return 应用实体
     */
    public AppEntity getByUid(String uid) {
        AppDO app = appMapper.selectOne(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getUid, uid));
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return AppConvert.INSTANCE.convert(app);
    }

    /**
     * 新增应用
     *
     * @param appEntity 应用实体
     */
    public void insert(AppEntity appEntity) {
        // 校验应用名称是否重复
        AppValidate.isTrue(!duplicateName(appEntity.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
        AppDO app = AppConvert.INSTANCE.convert(appEntity);
        app.setUid(AppUtils.generateUid(AppConstants.APP_PREFIX));
        app.setUploadUid(null);
        app.setLastUpload(null);
        app.setDeleted(Boolean.FALSE);
        appMapper.insert(app);
    }

    /**
     * 更新应用
     *
     * @param appEntity 应用实体
     */
    public void update(AppEntity appEntity) {
        AppValidate.isTrue(!duplicateName(appEntity.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
        AppDO app = AppConvert.INSTANCE.convert(appEntity);
        // deleted 不允许修改
        app.setDeleted(Boolean.FALSE);
        LambdaUpdateWrapper<AppDO> wrapper = Wrappers.lambdaUpdate(AppDO.class).eq(AppDO::getUid, app.getUid());
        appMapper.update(app, wrapper);
    }

    /**
     * 删除应用
     *
     * @param uid 应用唯一标识
     */
    public void deleteByUid(String uid) {
        // 校验应用是否存在
        AppValidate.isTrue(isExists(uid), ErrorCodeConstants.APP_NO_EXISTS_UID, uid);

        // 删除应用
        LambdaUpdateWrapper<AppDO> wrapper = Wrappers.lambdaUpdate(AppDO.class)
                .set(AppDO::getDeleted, Boolean.TRUE)
                .eq(AppDO::getUid, uid);
        appMapper.delete(wrapper);
    }

    /**
     * 判断应用是否存在
     *
     * @param uid 应用 UID 标识
     * @return 是否存在: true 存在，false 不存在
     */
    public Boolean isExists(String uid) {
        return Objects.nonNull(appMapper.selectOne(Wrappers.lambdaQuery(AppDO.class)
                .select(AppDO::getId).eq(AppDO::getUid, uid)));
    }

    /**
     * 判断应用名称是否重复
     *
     * @param name 应用名称
     */
    public Boolean duplicateName(String name) {
        return appMapper.selectCount(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getName, name)) > 0;
    }

}
