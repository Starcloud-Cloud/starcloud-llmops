package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class XhsCreativeQueryReq {

    private String planUid;

    /**
     * 执行任务类型 picture/copy_writing
     */
    private String type;

    /**
     * 一次查询的数据量
     */
    private Integer bathCount;

    /**
     * 是否只执行重试任务
     */
    private Boolean retryProcess;




    public Boolean valid() {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        return true;
    }
}
