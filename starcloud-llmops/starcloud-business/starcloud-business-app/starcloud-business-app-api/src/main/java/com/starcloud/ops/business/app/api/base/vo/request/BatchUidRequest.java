package com.starcloud.ops.business.app.api.base.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "UID请求实体")
public class BatchUidRequest implements Serializable {

    private static final long serialVersionUID = -8083745861564277538L;

    /**
     * 批次 uid
     */
    @Schema(description = "批次UID")
    @NotBlank(message = "批次UID不能为空")
    private String batchUid;

}
