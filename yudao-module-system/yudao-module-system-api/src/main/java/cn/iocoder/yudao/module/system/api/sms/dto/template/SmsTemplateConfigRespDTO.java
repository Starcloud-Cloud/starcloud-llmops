package cn.iocoder.yudao.module.system.api.sms.dto.template;

import lombok.Data;

import java.util.List;

/**
 * 短信模板
 *
 * @author Cusack Alan
 */
@Data
public class SmsTemplateConfigRespDTO {

    /**
     * 模板编码
     */
    private String code;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板内容
     */
    private String content;
    /**
     * 短信渠道编码
     */
    private String channelCode;
    /**
     * 参数数组
     */
    private List<String> params;

}
