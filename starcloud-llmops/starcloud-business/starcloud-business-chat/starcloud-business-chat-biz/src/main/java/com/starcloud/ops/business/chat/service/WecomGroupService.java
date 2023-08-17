package com.starcloud.ops.business.chat.service;

import com.starcloud.ops.business.chat.controller.admin.wecom.vo.request.WecomCreateGroupReqVO;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.response.WecomGroupRespVO;
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
}
