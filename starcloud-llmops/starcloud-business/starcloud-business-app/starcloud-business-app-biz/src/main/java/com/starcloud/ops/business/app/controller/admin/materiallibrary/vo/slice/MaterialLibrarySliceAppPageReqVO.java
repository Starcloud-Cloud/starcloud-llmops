package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 素材知识库数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibrarySliceAppPageReqVO extends SortablePageParam {


    public static final String SORT_FIELD_USED_COUNT = "used_count";
    public static final String SORT_FIELD_CREATE_TIME = "create_time";
    public static final String SORT_FIELD_UPDATE_TIME = "update_time";

    @Schema(description = "应用 UID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "应用 UID不能为空")
    private String appUid;

    @Schema(description = "排序字段")
    private SortingField sortingField;

}