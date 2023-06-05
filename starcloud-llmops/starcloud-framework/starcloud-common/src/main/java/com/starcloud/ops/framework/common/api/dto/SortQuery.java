package com.starcloud.ops.framework.common.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 排序查询参数对象，封装了排序查询的基本参数
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-02
 */
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@Schema(description = "排序查询参数对象，封装了排序查询的参数")
public class SortQuery implements Serializable {

    private static final long serialVersionUID = 7802980663319219757L;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String field;

    /**
     * 排序方式
     */
    @Schema(description = "排序方式")
    private String type;

    /**
     * 构造排序查询参数对象
     *
     * @param field 排序字段
     * @param type  排序方式
     * @return 排序查询参数对象
     */
    public static SortQuery of(String field, String type) {
        SortQuery sortQuery = new SortQuery();
        sortQuery.setField(field);
        sortQuery.setType(type);
        return sortQuery;
    }

}
