package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 灰豚搜索集合 DTO
 */
@NoArgsConstructor
@Data
public class SearchConditionsDTO {

    private List<TagLableDTO> tagList;
    private List<TagLableDTO> careers;
    private List<RegionsDTO> regions;
}
