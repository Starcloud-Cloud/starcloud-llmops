package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonTagDTO {
    private Integer id;
    private String tag;
    private List<PersonTagDTO> tag2;
}