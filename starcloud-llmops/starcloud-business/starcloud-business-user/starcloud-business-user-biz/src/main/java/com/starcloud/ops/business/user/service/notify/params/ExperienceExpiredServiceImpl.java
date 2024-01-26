package com.starcloud.ops.business.user.service.notify.params;

import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 体验包过期提醒
 */
@Slf4j
@Service
public class ExperienceExpiredServiceImpl extends NotifyParamsAbstractService {

    @Override
    public String support() {
        return NotifyTemplateEnum.NOTIFY_EXPERIENCE_EXPIRED.getCode();
    }

    @Override
    public List<NotifyContentRespVO> prepareParams() {
        return null;
    }
}
