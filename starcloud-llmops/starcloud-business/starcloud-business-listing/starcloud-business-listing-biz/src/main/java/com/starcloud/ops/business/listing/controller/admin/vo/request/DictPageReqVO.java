package com.starcloud.ops.business.listing.controller.admin.vo.request;

import cn.hutool.core.util.BooleanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.listing.enums.DictSortFieldEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询词库")
public class DictPageReqVO extends PageParam {

    @Schema(description = "词库名称")
    private String name;

    @Schema(description = "站点")
    private String endpoint;

    @Schema(description = "包含关键词")
    private String keyword;

    @Schema(description = "启用/禁用")
    private Boolean enable;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "正序")
    private Boolean asc;


    public String orderSql() {
        String column = DictSortFieldEnum.getColumn(sortField);
        return "order by " + column + (BooleanUtil.isTrue(asc)? " ASC":" DESC");
    }

}
