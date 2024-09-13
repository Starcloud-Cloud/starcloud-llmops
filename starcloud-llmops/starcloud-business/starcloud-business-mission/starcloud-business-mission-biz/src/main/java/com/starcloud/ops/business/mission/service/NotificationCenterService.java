package com.starcloud.ops.business.mission.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.api.vo.request.AppNotificationQueryReqVO;
import com.starcloud.ops.business.mission.api.vo.response.AppNotificationRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;

import java.util.Map;

public interface NotificationCenterService {

    /**
     * 新建任务
     */
    NotificationRespVO create(NotificationCreateReqVO reqVO);

    /**
     * 分页查询任务列表
     */
    PageResult<NotificationRespVO> page(NotificationPageQueryReqVO reqVO);

    /**
     * 发布任务
     */
    void publish(String uid, Boolean publish);

    /**
     * 删除任务
     */
    void delete(String uid);

    /**
     * 查询
     */
    NotificationCenterDO getByUid(String uid);


    NotificationRespVO selectByUid(String uid);

    /**
     * 编辑
     */
    NotificationRespVO modifySelective(NotificationModifyReqVO reqVO);

    /**
     * 枚举值
     * @return
     */
    Map<String, Object> metadata();

    /**
     * 查询分组邀请码
     * @return
     */
    String code();

}
