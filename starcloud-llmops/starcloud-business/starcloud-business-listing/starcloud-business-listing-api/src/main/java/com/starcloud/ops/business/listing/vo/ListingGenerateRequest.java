package com.starcloud.ops.business.listing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Listing 生成请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "ListingGenerateRequest", description = "Listing 生成请求")
public class ListingGenerateRequest implements java.io.Serializable {

    private static final long serialVersionUID = 5409988681591500383L;

    /**
     * 应用标签
     */
    @Schema(description = "sse")
    private SseEmitter sseEmitter;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * AI 模型
     */
    @Schema(description = "AI模型")
    private String aiModel;
}
