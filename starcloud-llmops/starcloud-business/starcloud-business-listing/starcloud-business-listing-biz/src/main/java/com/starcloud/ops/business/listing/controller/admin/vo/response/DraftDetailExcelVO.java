package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class DraftDetailExcelVO {

    @ExcelProperty("站点")
    private String endpoint;

    @ExcelProperty("ASIN")
    private String asin;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("五点描述")
    private String fiveDesc;

    @ExcelProperty("产品描述")
    private String productDesc;

    @ExcelProperty("搜索词")
    private String searchTerm;

}
