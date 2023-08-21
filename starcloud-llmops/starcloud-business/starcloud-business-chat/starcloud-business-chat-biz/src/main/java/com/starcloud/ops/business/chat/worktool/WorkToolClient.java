package com.starcloud.ops.business.chat.worktool;

import com.starcloud.ops.business.chat.worktool.request.*;
import com.starcloud.ops.business.chat.worktool.response.BaseResponse;
import com.starcloud.ops.business.chat.worktool.response.ExecuteResultResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${feign.remote.workTool.name:worktool}", url = "${feign.remote.workTool.url:https://worktool.asrtts.cn}")
public interface WorkToolClient {

    /**
     * 由机器人发送指定内容消息到相应的好友或群聊中，支持@指定人
     *
     * @param robotId
     * @param sendMessageReqBaseReq
     * @return
     */
    @PostMapping(value = "/wework/sendRawMessage?robotId={robotId}")
    BaseResponse<String> sendMsg(@PathVariable("robotId") String robotId, @RequestBody BaseReq<SendMessageReq> sendMessageReqBaseReq);

    /**
     * 新建群
     *
     * @param robotId
     * @param sendMessageReqBaseReq
     * @return
     */
    @PostMapping(value = "/wework/sendRawMessage?robotId={robotId}")
    BaseResponse<String> createGroup(@PathVariable("robotId") String robotId, @RequestBody BaseReq<CreateGroupReq> createGroupReqBaseReq);

    /**
     * 修改群信息/拉人/踢人
     *
     * @param robotId
     * @param sendMessageReqBaseReq
     * @return
     */
    @PostMapping(value = "/wework/sendRawMessage?robotId={robotId}")
    BaseResponse<String> modifyGroup(@PathVariable("robotId") String robotId, @RequestBody BaseReq<ModifyGroupReq> modifyGroupReqBaseReq);


    /**
     * 手机号/外部群添加好友
     *
     * @param robotId
     * @param sendMessageReqBaseReq
     * @return
     */
    @PostMapping(value = "/wework/sendRawMessage?robotId={robotId}")
    BaseResponse<String> addFriend(@PathVariable("robotId") String robotId, @RequestBody BaseReq<AddFriendReq> addFriendReqBaseReq);


    /**
     * 查询指令执行结果
     *
     * @param robotId
     * @param messageId
     * @return
     */
    @GetMapping(value = "/robot/rawMsg/list?robotId={robotId}&messageId={messageId}")
    BaseResponse<ExecuteResultResp> executeResult(@PathVariable("robotId") String robotId,
                                                  @PathVariable("messageId") String messageId);
}
