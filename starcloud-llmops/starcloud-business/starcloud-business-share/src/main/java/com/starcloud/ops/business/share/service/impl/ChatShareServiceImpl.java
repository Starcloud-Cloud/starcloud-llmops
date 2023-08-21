package com.starcloud.ops.business.share.service.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.share.service.ChatShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ChatShareServiceImpl implements ChatShareService {

    @Override
    public AppRespVO chatShareDetail(String mediumUid) {
        ChatAppEntity appEntity = AppFactory.factory(mediumUid);
        return AppConvert.INSTANCE.convertResponse(appEntity);
    }

    @Override
    public void shareChat(ChatRequestVO chatRequestVO) {
        if (StringUtils.isBlank(chatRequestVO.getMediumUid())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(600001, "Please use the latest sharing link"));
        }
        ChatAppEntity chatAppEntity = AppFactory.factory(chatRequestVO.getMediumUid());
        TenantContextHolder.setTenantId(chatAppEntity.getTenantId());
        chatAppEntity.asyncExecute(chatRequestVO);
    }
}
