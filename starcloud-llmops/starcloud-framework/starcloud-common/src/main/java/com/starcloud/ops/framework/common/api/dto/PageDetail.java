package com.starcloud.ops.framework.common.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDetail {

    public Long total;

    public Long current;

    private Long limit;

    /**
     * 创建分页详情
     *
     * @param total   总数
     * @param current 当前页
     * @param limit   每页大小
     * @return 分页详情
     */
    public static PageDetail of(Long total, Long current, Long limit) {
        return new PageDetail(total, current, limit);
    }
}
