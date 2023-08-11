package com.starcloud.ops.business.chat.worktool.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


/**
 * worktool 接口参数包装类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseReq<T> {

    /**
     * 消息详情
     */
    private List<T> list;
    /**
     * 通讯类型 固定值=2
     */
    private long socketType = 2;


}
