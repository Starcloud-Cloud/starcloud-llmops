package com.starcloud.ops.framework.common.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDetail {
    public long total;

    public long current;

    private long limit;
}
