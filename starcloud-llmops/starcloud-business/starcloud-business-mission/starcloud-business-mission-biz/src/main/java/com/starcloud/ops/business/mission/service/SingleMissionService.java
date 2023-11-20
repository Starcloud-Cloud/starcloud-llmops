package com.starcloud.ops.business.mission.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;

import java.util.List;

public interface SingleMissionService {

    /**
     * 增加单条任务
     */
    void addSingleMission(String notificationUid, List<String> creativeUids);

    /**
     * 分页查询
     */
    PageResult<SingleMissionRespVO> page(SinglePageQueryReqVO reqVO);

    /**
     * 修改任务
     */
    SingleMissionRespVO modifySelective(SingleMissionModifyReqVO reqVO);

    /**
     * 删除
     */
    void delete(String uid);

    /**
     * 领取任务
     */
    void pick(String uid);

    /**
     * 发布任务
     */
    void publish(String notificationUid, Boolean publish);
}
