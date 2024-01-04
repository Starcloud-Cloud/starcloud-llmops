package com.starcloud.ops.business.app.api.xhs.plan.dto;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
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
@Schema(name = "CreativePlanExecuteDTO", description = "小红书请求")
public class CreativePlanExecuteDTO implements java.io.Serializable {

    private static final long serialVersionUID = -5322081881706423889L;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    /**
     * 创作方案模式
     */
    @Schema(description = "创作方案模式")
    private String schemeMode;

    /**
     * 文案段落数量
     */
    @Schema(description = "文案段落数量")
    private Integer paragraphCount;

    /**
     * 小红书文案请求数据
     */
    @Schema(description = "小红书文案请求数据")
    private CreativePlanAppExecuteDTO appExecuteRequest;

    /**
     * 小红书图片请求数据
     */
    @Schema(description = "小红书图片请求数据")
    private CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest;

    /**
     * 小红书应用响应数据
     */
    @Schema(description = "小红书应用响应数据")
    private AppRespVO appResponse;

    /**
     * 小红书应用请求数据
     *
     * @param appExecuteRequest 小红书应用请求数据
     * @return 小红书应用请求数据
     */
    public static CreativePlanExecuteDTO ofApp(CreativePlanAppExecuteDTO appExecuteRequest) {
        CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
        planExecute.setAppExecuteRequest(appExecuteRequest);
        return planExecute;
    }

    /**
     * 小红书图片请求数据
     *
     * @param imageStyleExecuteRequest 小红书图片请求数据
     * @return 小红书图片请求数据
     */
    public static CreativePlanExecuteDTO ofImageStyle(CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest) {
        CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
        planExecute.setImageStyleExecuteRequest(imageStyleExecuteRequest);
        return planExecute;
    }
}
