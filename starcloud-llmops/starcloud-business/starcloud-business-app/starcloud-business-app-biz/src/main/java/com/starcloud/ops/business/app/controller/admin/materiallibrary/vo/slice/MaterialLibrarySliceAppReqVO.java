package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.common.pojo.SortingField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceAppReqVO {


    public static final String SORT_FIELD_USER_COUNT = "used_count";
    public static final String SORT_FIELD_ORDER_COUNT = "create_time";
    public static final String SORT_FIELD_PRICE = "update_time";


    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "素材库ID不能为空")
    private String libraryUid;

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<Long> sliceIdList;

    @Schema(description = "移除的素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<Long> removesliceIdList;

    @Schema(description = "排序字段")
    private SortingField sortingField;

}