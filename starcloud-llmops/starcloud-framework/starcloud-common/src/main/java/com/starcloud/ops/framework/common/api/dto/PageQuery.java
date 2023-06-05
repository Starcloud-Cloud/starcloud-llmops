package com.starcloud.ops.framework.common.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-30
 */
@Data
@Schema(title = "分页查询对象", description = "分页查询对象")
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 157666534536774534L;

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_NO = 1;

    /**
     * 默认每页条数
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 页码，从 1 开始
     */
    @Schema(description = "页码，从 1 开始，默认为 1", example = "1")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = DEFAULT_PAGE_NO;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数，最大值为 10000, 默认为 10", example = "10")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 10000, message = "每页条数最大值为 10000")
    private Integer pageSize = DEFAULT_PAGE_SIZE;


}
