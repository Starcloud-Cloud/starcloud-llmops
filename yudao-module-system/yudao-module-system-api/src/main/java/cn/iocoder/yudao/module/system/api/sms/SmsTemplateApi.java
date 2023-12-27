package cn.iocoder.yudao.module.system.api.sms;

import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.template.SmsTemplateConfigRespDTO;

import javax.validation.Valid;

/**
 * 短信模板 API 接口
 *
 * @author Alan Cusack
 */
public interface SmsTemplateApi {


    /**
     * 获得短信模板，从缓存中
     *
     * @param code 模板编码
     * @return 短信模板
     */
    SmsTemplateConfigRespDTO getSmsTemplateByApiTemplateId(String code);


}
