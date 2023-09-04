package com.starcloud.ops.business.app.service.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-29
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppLimitRequest", description = "限流执行请求VO")
public class AppLimitRequest implements Serializable {

    private static final long serialVersionUID = 2345561400654818438L;

    /**
     * 默认排除限流规则
     */
    private static final List<String> DEFAULT_EXCLUDE = Arrays.asList("BASE_GENERATE_IMAGE_LIMIT", "GENERATE_TEXT", "GENERATE_ARTICLE_LIMIT", "CHAT_ROBOT");

    /**
     * 应用唯一标识
     */
    @Schema(description = "应用唯一标识")
    private String appUid;

    /**
     * 渠道媒介UID
     */
    @Schema(description = "渠道媒介UID")
    private String mediumUid;

    /**
     * 游客唯一表示
     */
    @Schema(description = "游客唯一表示")
    private String endUser;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 包含的限流规则，如果配置，则只会执行包含的限流规则
     */
    @Schema(description = "包含的限流规则")
    private List<String> include;

    /**
     * include 和 exclude 配置相同的 key 的话，exclude 生效 需要排除的限流规则
     */
    @Schema(description = "需要排除的限流规则")
    private List<String> exclude = DEFAULT_EXCLUDE;


    /**
     * 获取请求数据
     *
     * @param appUid    应用UID
     * @param fromScene 执行场景
     * @return 请求数据
     */
    public static AppLimitRequest of(String appUid, String fromScene) {
        AppLimitRequest request = new AppLimitRequest();
        request.setAppUid(appUid);
        request.setFromScene(fromScene);
        return request;
    }

    /**
     * 获取请求数据
     *
     * @param mediumUid 渠道媒介UID
     * @param fromScene 执行场景
     * @param endUser   游客ID
     * @return 请求数据
     */
    public static AppLimitRequest of(String mediumUid, String fromScene, String endUser) {
        AppLimitRequest request = new AppLimitRequest();
        request.setMediumUid(mediumUid);
        request.setFromScene(fromScene);
        request.setEndUser(endUser);
        return request;
    }

}
