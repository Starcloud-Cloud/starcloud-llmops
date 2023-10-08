package com.starcloud.ops.business.app.domain.repository.publish;


import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.channel.AppPublishChannelMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

@Repository
@SuppressWarnings("all")
public class AppPublishRepository {

    @Resource
    private AppPublishMapper appPublishMapper;

    @Resource
    private AppPublishChannelMapper appPublishChannelMapper;

    /**
     * 根据渠道媒介编号，获得应用信息
     *
     * @param mediumUid 渠道媒介编号
     * @return 应用信息
     */
    public AppEntity getAppEntityByMediumUid(String mediumUid) {
        AppPublishChannelDO appPublishChannelDO = appPublishChannelMapper.getByMediumUid(mediumUid);
        AppValidate.notNull(appPublishChannelDO, ErrorCodeConstants.CHANNEL_NON_EXISTENT, mediumUid);
        AppPublishDO appPublishDO = appPublishMapper.get(appPublishChannelDO.getPublishUid(), Boolean.FALSE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, appPublishChannelDO.getPublishUid());
        if (StringUtils.isBlank(appPublishDO.getAppInfo())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NON_EXISTENT);
        }

        AppDO appDO = JSONUtil.toBean(appPublishDO.getAppInfo(), AppDO.class);
        if (Objects.isNull(appDO)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NON_EXISTENT, appDO.getUid());
        }

        return (AppEntity) AppConvert.INSTANCE.convert(appDO, Boolean.FALSE);
    }

    /**
     * 根据发布编号，获得发布信息
     *
     * @param publishUid 发布编号
     * @return 发布信息
     */
    public AppPublishDO getByPublishUid(String publishUid) {
        AppPublishDO appPublishDO = appPublishMapper.get(publishUid, Boolean.FALSE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, publishUid);
        return appPublishDO;
    }

    public ChatAppEntity getChatEntityByMediumUid(String mediumUid) {
        AppPublishChannelDO appPublishChannelDO = appPublishChannelMapper.getByMediumUid(mediumUid);
        AppValidate.notNull(appPublishChannelDO, ErrorCodeConstants.CHANNEL_NON_EXISTENT, mediumUid);
        AppPublishDO appPublishDO = appPublishMapper.get(appPublishChannelDO.getPublishUid(), Boolean.FALSE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, appPublishChannelDO.getPublishUid());
        if (StringUtils.isBlank(appPublishDO.getAppInfo())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NON_EXISTENT);
        }
        AppDO appDO = JSONUtil.toBean(appPublishDO.getAppInfo(), AppDO.class);
        BaseAppEntity entity = AppConvert.INSTANCE.convert(appDO, false);
        return (ChatAppEntity) entity;
    }

}
