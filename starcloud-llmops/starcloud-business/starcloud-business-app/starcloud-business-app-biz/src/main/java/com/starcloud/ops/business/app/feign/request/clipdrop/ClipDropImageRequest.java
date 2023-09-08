package com.starcloud.ops.business.app.feign.request.clipdrop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * ClipDrop AI 图片生成请求基本请求类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Schema(name = "ClipDropImageRequest", description = "ClipDrop AI 图片生成请求基本请求类")
public class ClipDropImageRequest implements Serializable {

    private static final long serialVersionUID = 1775069552577744436L;

}
