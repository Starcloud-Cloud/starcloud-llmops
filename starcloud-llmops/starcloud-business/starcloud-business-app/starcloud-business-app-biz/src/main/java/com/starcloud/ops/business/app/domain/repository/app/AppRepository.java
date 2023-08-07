package com.starcloud.ops.business.app.domain.repository.app;

import cn.hutool.core.util.IdUtil;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * App Repository
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Repository
@SuppressWarnings("all")
public class AppRepository {

    @Resource
    private AppMapper appMapper;

    /**
     * 根据 uid 查询应用
     *
     * @param uid 应用唯一标识
     * @return 应用实体
     */
    public BaseAppEntity getByUid(String uid) {
        AppDO app = appMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return AppConvert.INSTANCE.convert(app, Boolean.FALSE);
    }

    /**
     * 新增应用
     *
     * @param appEntity 应用实体
     */
    public void insert(BaseAppEntity appEntity) {
        AppDO app = AppConvert.INSTANCE.convert(appEntity);
        appMapper.create(app);
    }

    /**
     * 更新应用
     *
     * @param appEntity 应用实体
     */
    public void update(BaseAppEntity appEntity) {
        AppDO app = AppConvert.INSTANCE.convert(appEntity);
        appMapper.modify(app);
    }

}
