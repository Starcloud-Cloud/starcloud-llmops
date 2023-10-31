package com.starcloud.ops.business.listing.vo;

import com.starcloud.ops.business.listing.enums.ListingGenerateTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * Listing 生成响应
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "ListingGenerateResponse", description = "Listing 生成响应")
public class ListingGenerateResponse implements java.io.Serializable {

    private static final long serialVersionUID = 8969470726269051882L;

    /**
     * 返回状态
     */
    @Schema(description = "返回状态")
    private Boolean success;

    /**
     * 会话UID
     */
    @Schema(description = "会话id")
    private String conversationUid;

    /**
     * 应用标签
     */
    @Schema(description = "生成类型")
    @NotBlank(message = "请输入Listing类型，这是必填项！")
    @InEnum(value = ListingGenerateTypeEnum.class, field = InEnum.EnumField.NAME, message = "请输入正确的Listing类型({value})，目前只支持：{values}")
    private String listingType;

    /**
     * 草稿UID
     */
    @Schema(description = "草稿UID")
    @NotBlank(message = "请输入草稿UID，这是必填项！")
    private String draftUid;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private String answer;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;


}
