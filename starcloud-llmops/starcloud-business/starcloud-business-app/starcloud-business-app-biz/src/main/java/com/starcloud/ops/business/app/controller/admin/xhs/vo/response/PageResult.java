package com.starcloud.ops.business.app.controller.admin.xhs.vo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 3168336986372765789L;

    @Schema(description = "数据", required = true)
    private List<T> list;

    @Schema(description = "总量", required = true)
    private Long total;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer errorCount;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> cn.iocoder.yudao.framework.common.pojo.PageResult<T> empty() {
        return new cn.iocoder.yudao.framework.common.pojo.PageResult<>(0L);
    }

    public static <T> cn.iocoder.yudao.framework.common.pojo.PageResult<T> empty(Long total) {
        return new cn.iocoder.yudao.framework.common.pojo.PageResult<>(total);
    }

}