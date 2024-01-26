package com.starcloud.ops.business.user.service.notify.params;

import cn.hutool.core.map.MapUtil;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 提醒新用户购买体验包
 */
@Slf4j
@Service
public class PurchaseExperienceServiceImpl extends NotifyParamsAbstractService {


    @Override
    public String support() {
        return NotifyTemplateEnum.NOTIFY_PURCHASE_EXPERIENCE.getCode();
    }

    @Override
    public List<NotifyContentRespVO> prepareParams() {
        List<NotifyContentRespVO> result = new ArrayList<>();
//          alter table system_notify_message add `batch_code` varchar(255) DEFAULT NULL COMMENT '批次号';
//          alter table system_notify_message add `sms_success` bit(1) DEFAULT b'0' COMMENT '短信发送成功';
//          alter table system_notify_message add `mp_success` bit(1) DEFAULT b'0' COMMENT '公众号发送成功';
//          alter table system_notify_message add `sms_log` json DEFAULT NULL COMMENT '短信日志';
//          alter table system_notify_message add `mp_log` json DEFAULT NULL COMMENT '公众号日志';
//          alter table system_notify_message add `sent` bit(1) DEFAULT b'0' COMMENT '已发送';
//          alter table system_notify_message add `media_types` json DEFAULT NULL COMMENT '发送媒介';
//          alter table system_notify_template add `media_types` json DEFAULT NULL COMMENT '发送媒介';
//        NotifyContentRespVO content = new NotifyContentRespVO();
//        content.setReceiverId(206L);
//        HashMap<String, Object> params = MapUtil.newHashMap();
//        params.put("username","张三");
//        params.put("magicBean",1);
//        content.setTemplateParams(params);
//        content.setReceiverName("receiverName");
//        result.add(content);
        return result;
    }
}
