package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.common.pojo.SortingField;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceAppReqVO extends MaterialLibraryAppReqVO {


    public static final String SORT_FIELD_USED_COUNT = "used_count";
    public static final String SORT_FIELD_CREATE_TIME = "create_time";
    public static final String SORT_FIELD_UPDATE_TIME = "update_time";
    public static final String SORT_FIELD_ID = "id";


    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private String libraryUid;

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<Long> sliceIdList;

    @Schema(description = "移除的素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<Long> removesliceIdList;

    @Schema(description = "排序字段")
    private SortingField sortingField;

}