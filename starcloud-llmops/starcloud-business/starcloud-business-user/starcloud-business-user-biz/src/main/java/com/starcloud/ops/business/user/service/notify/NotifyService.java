package com.starcloud.ops.business.user.service.notify;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.CreateNotifyReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.FilterUserReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.framework.common.api.dto.Option;

import java.util.List;
import java.util.Map;

public interface NotifyService {

    Map<String, List<Option>> metaData();

    /**
     * 发送通知
     *
     * @param
     */
    void sendNotify(Long logId);

    /**
     * 创建发送消息任务
     * @param reqDTO
     */
    void createMsgTask(CreateNotifyReqVO reqDTO);

    void triggerNotify(CreateNotifyReqVO reqDTO);

    /**
     * 过滤用户通知内容
     * @param reqVO
     * @return
     */
    PageResult<NotifyContentRespVO> pageFilterUser(FilterUserReqVO reqVO);

    /**
     * 创建站内信模板
     * @param createReqVO
     * @return
     */
    Long createNotifyTemplate(NotifyTemplateCreateReqVO createReqVO);

    /**
     * 更新站内信模版
     * @param updateReqVO
     */
    void updateNotifyTemplate(NotifyTemplateUpdateReqVO updateReqVO);
}
