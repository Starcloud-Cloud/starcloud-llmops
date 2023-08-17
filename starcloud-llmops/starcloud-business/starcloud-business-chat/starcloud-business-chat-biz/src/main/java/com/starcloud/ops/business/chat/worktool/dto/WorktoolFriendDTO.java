package com.starcloud.ops.business.chat.worktool.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorktoolFriendDTO {

    /**
     * 加好友附言
     */
    private String leavingMsg;
    /**
     * 备注其他信息(不推荐) 选填
     */
    private String markExtra;
    /**
     * 备注昵称 选填
     */
    private String markName;
    /**
     * 按手机号搜索
     */
    private String phone;
    /**
     * 备注标签(推荐) 选填
     */
    private List<String> tagList;

}
