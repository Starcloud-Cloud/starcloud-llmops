package com.starcloud.ops.business.user.service.notify.params;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyTemplateDO;
import cn.iocoder.yudao.module.system.service.notify.NotifyTemplateService;
import com.starcloud.ops.business.user.controller.admin.notify.vo.FilterUserReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.PARAMS_ERROR;

@Slf4j
public abstract class NotifyParamsAbstractService {

    @Resource
    NotifyTemplateService notifyTemplateService;

    public NotifyTemplateDO getTemplate() {
        NotifyTemplateDO template = notifyTemplateService.getNotifyTemplateByCodeFromCache(support());
        if (template == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        return template;
    }

    public List<NotifyContentRespVO> filterNotifyContent() {
        long start = System.currentTimeMillis();
        List<NotifyContentRespVO> notifyContentList = prepareParams();
        long end = System.currentTimeMillis();
        log.info("notify prepare params, {} ms", end - start);
        return notifyContentList;
    }

    String formatContent(String template, Map<String, Object> params) {
        return StrUtil.format(template, params);
    }

    /**
     * 支持的通知类型
     * {@link NotifyTemplateEnum#getCode()}
     *
     * @return 模板code
     */
    public abstract String support();

    /**
     * 准备消息参数
     *
     * @return
     */
    public abstract List<NotifyContentRespVO> prepareParams();

    /**
     * 分页查询
     * @param reqVO
     * @return
     */
    public abstract PageResult<NotifyContentRespVO> pageFilterNotifyContent(FilterUserReqVO reqVO);
}
