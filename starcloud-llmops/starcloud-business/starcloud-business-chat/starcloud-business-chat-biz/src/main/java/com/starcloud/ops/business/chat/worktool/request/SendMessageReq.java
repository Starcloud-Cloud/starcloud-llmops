package com.starcloud.ops.business.chat.worktool.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 消息明细
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessageReq {

    /**
     * at的人(at所有人用"@所有人") 选填
     */
    private List<String> atList;
    /**
     * 发送文本内容 (\n换行)
     */
    private String receivedContent;
    /**
     * 昵称或群名
     */
    private List<String> titleList;
    /**
     * 消息类型 固定值=203
     */
    private long type = 203;
}
