package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class RegionsDTO {
    private String province;
    private List<String> cityList;
}