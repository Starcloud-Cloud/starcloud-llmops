package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BaseDTO {
    private Integer page;
    private Integer pageSize;
    private Integer index;
    private Integer position;
    private Object updatedTime;
}