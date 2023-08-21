package com.starcloud.ops.business.open.service;

import com.starcloud.ops.business.open.controller.admin.vo.request.AddFriendReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.QaCallbackReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WecomCreateGroupReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WecomGroupRespVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WecomGroupService {
    /**
     * 新建群聊
     * @param reqVO
     * @return
     */
    void initGroup(WecomCreateGroupReqVO reqVO);

    /**
     * 查询所有群聊
     * @param appUid
     */
    List<WecomGroupRespVO> listGroupDetail(String appUid);


    /**
     * 绑定发布渠道到群聊
     * @param qaCallbackReqVO
     */
    void bindPublishChannel(QaCallbackReqVO qaCallbackReqVO);

    /**
     * 添加好友
     * @param reqVO
     */
    void addFriend(AddFriendReqVO reqVO);
}
