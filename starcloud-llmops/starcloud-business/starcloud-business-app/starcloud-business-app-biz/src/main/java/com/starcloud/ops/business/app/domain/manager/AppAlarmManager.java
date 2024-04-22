package com.starcloud.ops.business.app.domain.manager;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用报警管理
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class AppAlarmManager {

    @Resource
    private SmsSendApi smsSendApi;

    private void alarm(String errorMsg) {
//        try {
//            Map<String, Object> templateParams = new HashMap<>();
//            templateParams.put("errorMsg", errorMsg);
//            templateParams.put("date", LocalDateTime.now());
//            templateParams.put("environment", SpringUtil.getActiveProfile());
//            smsSendApi.sendSingleSmsToAdmin(
//                    new SmsSendSingleToUserReqDTO()
//                            .setUserId(1L).setMobile("17835411844")
//                            .setTemplateCode("NOTICE_XHS_LOGIN_WARN")
//                            .setTemplateParams(templateParams));
//        } catch (RuntimeException e) {
//            log.error("系统通知信息发送失败", e);
//        }
    }

}
