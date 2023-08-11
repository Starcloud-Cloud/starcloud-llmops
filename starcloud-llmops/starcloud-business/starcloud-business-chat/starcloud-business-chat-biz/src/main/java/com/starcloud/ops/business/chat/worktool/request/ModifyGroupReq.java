package com.starcloud.ops.business.chat.worktool.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 修改群
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModifyGroupReq {
    /**
     * 待修改的群名
     */
    private String groupName;
    /**
     * 修改群备注(选填)
     */
    private String groupRemark;
    /**
     * 修改群模板(选填)
     */
    private String groupTemplate;
    /**
     * 修改群公告 选填
     */
    private String newGroupAnnouncement;
    /**
     * 修改群名 选填
     */
    private String newGroupName;
    /**
     * 移除群成员名称列表/踢人 选填
     */
    private List<String> removeList;
    /**
     * 添加群成员名称列表/拉人 选填
     */
    private List<String> selectList;
    /**
     * 拉人是否附带历史记录 选填
     */
    private Boolean showMessageHistory;
    /**
     * 固定值=207
     */
    private long type = 207;
}
