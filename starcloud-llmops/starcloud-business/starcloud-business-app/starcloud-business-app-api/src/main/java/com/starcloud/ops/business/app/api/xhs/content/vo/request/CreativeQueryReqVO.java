package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
public class CreativeQueryReqVO {

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 执行任务类型
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

    /**
     * 最大重试次数
     */
    private Integer maxRetry;


    public Boolean valid() {
        return !StringUtil.isBlank(type) && CreativeContentTypeEnum.contain(type);
    }
}
