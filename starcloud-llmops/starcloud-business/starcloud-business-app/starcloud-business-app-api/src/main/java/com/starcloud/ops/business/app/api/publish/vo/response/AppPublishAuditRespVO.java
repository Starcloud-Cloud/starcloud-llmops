package com.starcloud.ops.business.app.api.publish.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-31
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishAuditRespVO", description = "应用发布审核状态响应")
public class AppPublishAuditRespVO implements Serializable {

    private static final long serialVersionUID = -6374734395079342363L;

    /**
     * 发布UID
     */
    private String uid;

    /**
     * 应用 UID
     */
    private String appUid;

    /**
     * 发布状态
     */
    private Integer audit;

    /**
     * 发布状态时间
     */
    private LocalDateTime updateTime;
}
