package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentTypeEnums;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.Data;

@Data
public class CreativeQueryReqVO {

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
        if (StringUtil.isBlank(type) || !XhsCreativeContentTypeEnums.contain(type)) {
            return false;
        }
        return true;
    }
}