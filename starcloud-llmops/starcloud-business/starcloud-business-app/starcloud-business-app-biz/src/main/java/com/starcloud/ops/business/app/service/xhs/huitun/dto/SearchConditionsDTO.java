package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.service.xhs.huitun.dto
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/03/13  17:44
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/03/13   AlanCusack    1.0         1.0 Version
 */
@NoArgsConstructor
@Data
public class SearchConditionsDTO {

    private List<TagLableDTO> tagList;
    private List<TagLableDTO> careers;
    private List<RegionsDTO> regions;
}
