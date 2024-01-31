package com.starcloud.ops.business.user.service.notify.params;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.notify.vo.FilterUserReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        return Collections.emptyList();
    }

    @Override
    public PageResult<NotifyContentRespVO> pageFilterNotifyContent(FilterUserReqVO reqVO) {
        return new PageResult<>();
    }
}
