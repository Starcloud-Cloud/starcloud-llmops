package com.starcloud.ops.business.app.domain.repository.publish;


import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.channel.AppPublishChannelMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
@SuppressWarnings("all")
public class AppPublishRepository {

    @Resource
    private AppPublishMapper appPublishMapper;



    public AppPublishDO getByPublishUid(String publishUid) {
        AppPublishDO appPublishDO = appPublishMapper.get(publishUid, Boolean.FALSE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.APP_PUBLISH_APP_INFO_NOT_FOUND, publishUid);
        return appPublishDO;
    }
}
