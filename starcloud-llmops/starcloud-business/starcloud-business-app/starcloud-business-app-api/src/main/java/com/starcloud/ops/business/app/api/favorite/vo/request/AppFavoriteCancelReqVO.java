package com.starcloud.ops.business.app.api.favorite.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 应用收藏创建请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AppFavoriteCancelReqVO implements Serializable {

    private static final long serialVersionUID = 3164853422308964487L;

    /**
     * 应用市场Uid
     */
    @Schema(description = "应用市场Uid")
    @NotBlank(message = "应用市场Uid不能为空")
    private String marketUid;

}
