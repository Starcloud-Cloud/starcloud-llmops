package com.starcloud.ops.business.chat.worktool.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.chat.worktool.dto.WorktoolFriendDTO;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddFriendReq {

    private WorktoolFriendDTO friend;
    /**
     * 213 手机号添加  220 外部群添加
     */
    private long type = 213;

    /**
     * 从外部群添加好友
     */
    private String groupName;
}
