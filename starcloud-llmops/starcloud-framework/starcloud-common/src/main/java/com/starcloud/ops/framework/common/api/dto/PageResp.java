package com.starcloud.ops.framework.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResp<T> {

    private List<T> list;

    private PageDetail page;

    /**
     * 构造分页响应
     *
     * @param list    列表
     * @param total   总数
     * @param current 当前页
     * @param limit   每页大小
     * @param <T>     列表类型
     * @return 分页响应
     */
    public static <T> PageResp<T> of(List<T> list, Long total, Long current, Long limit) {
        return PageResp.<T>builder()
                .list(list)
                .page(PageDetail.of(total, current, limit))
                .build();
    }
}
