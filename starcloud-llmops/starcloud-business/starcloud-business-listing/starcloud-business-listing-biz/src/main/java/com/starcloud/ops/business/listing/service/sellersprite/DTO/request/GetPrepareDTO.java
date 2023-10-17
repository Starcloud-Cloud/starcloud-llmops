package com.starcloud.ops.business.listing.service.sellersprite.DTO.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 流量词拓展 获取拓词方式
 */
@NoArgsConstructor
@Data
public class GetPrepareDTO {

    private String month;
    private Integer market;
    private List<String> asinList;
}
