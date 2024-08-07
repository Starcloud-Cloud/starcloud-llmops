package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户特征 DTO
 */

@NoArgsConstructor
@Data
public class PersonFeatDTO {
    private List<PersonTagDTO> person;
    private List<PersonTagDTO> feat;
}
