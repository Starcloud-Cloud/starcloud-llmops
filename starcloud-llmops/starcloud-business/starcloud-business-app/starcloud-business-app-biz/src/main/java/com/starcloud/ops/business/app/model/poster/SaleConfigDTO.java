package com.starcloud.ops.business.app.model.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Schema(description = "售卖配置")
public class SaleConfigDTO implements java.io.Serializable{

    private static final long serialVersionUID = 3693634313817134472L;

    @Schema(description = "开启售卖")
    private Boolean openSale;

    @Schema(description = "演示笔记 ID")
    private String demoId;

}
