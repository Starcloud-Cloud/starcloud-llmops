package com.starcloud.ops.business.app.api.xhs.content.dto;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容执行参数")
public class CreativeContentExecuteParam implements java.io.Serializable {

    private static final long serialVersionUID = -5322081881706423889L;

    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private AppMarketRespVO appInformation;

    /**
     * 执行扩展参数
     */
    @Schema(description = "执行扩展参数")
    private CreativeContentExecuteExtend extend;

}
