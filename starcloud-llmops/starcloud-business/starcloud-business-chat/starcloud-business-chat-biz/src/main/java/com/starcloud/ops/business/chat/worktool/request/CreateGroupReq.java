package com.starcloud.ops.business.chat.worktool.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 新建外部群
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGroupReq {

    /**
     * 群的群公告(选填)
     */
    private String groupAnnouncement;
    /**
     * 要创建的群名
     */
    private String groupName;
    /**
     * 修改群备注(选填)
     */
    private String groupRemark;
    /**
     * 要拉入群的成员昵称
     */
    private List<String> selectList;
    /**
     * 固定值=206
     */
    private long type = 206;
}
