package com.starcloud.ops.business.other;

import cn.iocoder.yudao.framework.common.core.KeyValue;
import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.sms.core.client.SmsClient;
import cn.iocoder.yudao.framework.sms.core.client.SmsClientFactory;
import cn.iocoder.yudao.framework.sms.core.client.SmsCommonResult;
import cn.iocoder.yudao.framework.sms.core.client.dto.SmsSendRespDTO;
import cn.iocoder.yudao.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.iocoder.yudao.framework.sms.core.enums.SmsChannelEnum;
import cn.iocoder.yudao.framework.sms.core.property.SmsChannelProperties;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.dal.mysql.sms.SmsTemplateMapper;
import cn.iocoder.yudao.module.system.service.sms.SmsChannelService;
import cn.iocoder.yudao.module.system.service.sms.SmsTemplateServiceImpl;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@ComponentScan(basePackages = {"cn.iocoder.yudao.module.system", "cn.iocoder.yudao.framework.sms"})
@Import({YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class SmsTest extends BaseDbUnitTest {


    @Resource
    private SmsClientFactory smsClientFactory;

    @Lazy
    @Resource
    private SmsChannelService smsChannelService;

    @MockBean
    private RedisMQTemplate redisMQTemplate;

    @Test
    public void smsTest() {

        Long channelId = 9l;
        String code = "SMS_463625873";

        //smsChannelService.initLocalCache();
        // 获得短信模板

        SmsChannelProperties smsChannelProperties = new SmsChannelProperties();
        smsChannelProperties.setId(channelId);
        smsChannelProperties.setCode(SmsChannelEnum.ALIYUN.getCode());
        smsChannelProperties.setApiKey("");
        smsChannelProperties.setApiSecret("");
        smsChannelProperties.setSignature("魔法AI");

        smsClientFactory.createOrUpdateSmsClient(smsChannelProperties);

        SmsClient smsClient = smsClientFactory.getSmsClient(channelId);

        Assert.notNull(smsClient, String.format("短信客户端(%d) 不存在", channelId));
//        SmsCommonResult<SmsTemplateRespDTO> templateResult = smsClient.getSmsTemplate(code);
//        templateResult.checkError();

        List<KeyValue<String, Object>> templateParams = new ArrayList<>();

        KeyValue keyValue = new KeyValue();
        keyValue.setValue(44631);
        keyValue.setKey("code");
        templateParams.add(keyValue);

        SmsCommonResult<SmsSendRespDTO> result = smsClient.sendSms(991l, "", code, templateParams);
        // 校验短信模板是否正确

        log.info("result: {}", result);


    }
}
