package com.starcloud.ops.business.listing.vo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

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

    /**
     * 产品特性
     */
    @Schema(description = "产品特性")
    @NotBlank(message = "请输入产品特性，这是必填项！")
    private String productFeature;

    /**
     * 客户特性
     */
    @Schema(description = "客户特性")
    private String customerFeature;

    /**
     * 品牌名称
     */
    @Schema(description = "品牌名称")
    private String brandName;

    /**
     * 品牌名称位置
     */
    @Schema(description = "品牌名称位置")
    private String brandNameLocation;

    /**
     * 语言
     */
    @Schema(description = "语言")
    private String targetLanguage;

    /**
     * 风格
     */
    @Schema(description = "风格")
    private String writingStyle;

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private List<String> keywords;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 五点描述
     */
    @Schema(description = "五点描述")
    private List<String> bulletPoints;

    /**
     * 对象转为 Map，key 为字段的名称，转为大写下划线，value 为属性值。
     *
     * @return Map
     */
    public Map<String, String> toMap() {
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isBlank(this.productFeature)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(310500401, "请输入产品特性，这是必填项！"));
        }
        map.put("PRODUCT_FEATURE", this.productFeature);
        if (StringUtils.isNotBlank(this.customerFeature)) {
            map.put("CUSTOMER_FEATURE", this.customerFeature);
        }
        if (StringUtils.isNotBlank(this.brandName)) {
            map.put("BRAND_NAME", this.brandName);
        }
        if (StringUtils.isNotBlank(this.brandNameLocation)) {
            map.put("BRAND_NAME_LOCATION", this.brandNameLocation);
        }
        if (StringUtils.isNotBlank(this.targetLanguage)) {
            map.put("TARGET_LANGUAGE", this.targetLanguage);
        }
        if (StringUtils.isNotBlank(this.writingStyle)) {
            map.put("WRITING_STYLE", this.writingStyle);
        }
        if (CollectionUtil.isNotEmpty(this.keywords)) {
            map.put("KEYWORDS", StrUtil.join(",", this.keywords));
        }
        if (StringUtils.isNotBlank(this.title)) {
            map.put("TITLE", this.title);
        }
        if (CollectionUtil.isNotEmpty(this.bulletPoints)) {
            map.put("BULLET_POINTS", StrUtil.join(",", bulletPoints));
        }

        return map;
    }

}
