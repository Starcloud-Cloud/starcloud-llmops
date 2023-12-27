package cn.iocoder.yudao.module.system.api.sms;

import cn.iocoder.yudao.module.system.api.sms.dto.template.SmsTemplateConfigRespDTO;
import cn.iocoder.yudao.module.system.convert.sms.SmsTemplateConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.sms.SmsTemplateDO;
import cn.iocoder.yudao.module.system.service.sms.SmsTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 短信模板
 * @author Cusack Alan
 *
 */
@Service
@Validated
public class SmsTemplateApiImpl implements SmsTemplateApi{
    @Resource
    private SmsTemplateService smsTemplateService;


    /**
     * 获得短信模板，从缓存中
     *
     * @param apiTemplateId 模板编码
     * @return 短信模板
     */
    @Override
    public SmsTemplateConfigRespDTO getSmsTemplateByApiTemplateId(String apiTemplateId) {
        SmsTemplateDO smsTemplateDO = smsTemplateService.getSmsTemplateByApiTemplateIdFromCache(apiTemplateId);
        return SmsTemplateConvert.INSTANCE.convert01(smsTemplateDO);
    }
}
