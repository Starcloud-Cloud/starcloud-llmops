package com.starcloud.ops.business.core.config.image;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 图片生成
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-06
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Component
@ConfigurationProperties(prefix = "starcloud-llm.business.app.image")
public class ImageAiProperties {

    /**
     * stability 配置
     */
    private StabilityImageProperties stability;

    /**
     * clipDrop 配置
     */
    private ClipDropImageProperties clipDrop;


    @Data
    @ToString
    @NoArgsConstructor
    @EqualsAndHashCode
    @Component
    @ConfigurationProperties(prefix = "starcloud-llm.business.app.image.stability")
    public static class StabilityImageProperties {

        /**
         * Stability API Key
         */
        private String apiKey;

    }

    @Data
    @ToString
    @NoArgsConstructor
    @EqualsAndHashCode
    @Component
    @ConfigurationProperties(prefix = "starcloud-llm.business.app.image.clip-drop")
    public static class ClipDropImageProperties {

        /**
         * ClipDrop API Key
         */
        private String apiKey;

    }
}
