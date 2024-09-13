package com.starcloud.ops.business.mission.api;

import com.starcloud.ops.business.mission.api.vo.request.WechatUserBindReqVO;

public interface WechatUserBindService {

    /**
     * 绑定用户分组
     *
     * @param reqVO
     */
    void bindGroup(WechatUserBindReqVO reqVO);

    /**
     * 查询绑定的admin用户
     *
     * @return
     */
    String getBindUser();
}
