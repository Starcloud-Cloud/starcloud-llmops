package com.starcloud.ops.business.app.api.favorite.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场对象收藏响应实体 VO")
public class AppFavoriteRespVO extends AppMarketRespVO {

    private static final long serialVersionUID = 4430780734779852216L;

    /**
     * 收藏 UID
     */
    @Schema(description = "收藏 UID")
    private String favoriteUid;

    /**
     * 收藏者 ID
     */
    @Schema(description = "收藏者 ID")
    private String favoriteCreator;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    private String favoriteType;

    private String styleUid;
}
