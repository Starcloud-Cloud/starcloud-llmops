package com.starcloud.ops.business.app.api.limit.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-31
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class AppPublishLimitQuery implements Serializable {

    private static final long serialVersionUID = 7556391372371118156L;

    /**
     * 发布 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用 UID 不能为空")
    private String appUid;

    /**
     * 发布 UID
     */
    @Schema(description = " 应用发布 UID")
    private String publishUid;

    /**
     * 发布渠道UID
     */
    @Schema(description = "应用发布渠道 UID")
    private String channelUid;
}
