package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceShareReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1,2,3")
    @Size(min = 1,max = 100,message = "数据共享仅支持 1-100 条数据")
    private List<Long> id;

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1111")
    @NotNull(message = "素材库ID不能为空")
    private Long libraryId;

    @Schema(description = "是否开启数据共享", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据共状态不能为空")
    @InEnum(value = CommonStatusEnum.class,message = "请求状态必须在指定范围 {value}")
    private Boolean isShare;
}