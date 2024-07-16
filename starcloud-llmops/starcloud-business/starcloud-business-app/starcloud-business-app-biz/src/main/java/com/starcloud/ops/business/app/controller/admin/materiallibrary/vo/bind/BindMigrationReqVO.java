package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class BindMigrationReqVO extends MaterialLibraryAppReqVO {

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private String libraryUid;
}