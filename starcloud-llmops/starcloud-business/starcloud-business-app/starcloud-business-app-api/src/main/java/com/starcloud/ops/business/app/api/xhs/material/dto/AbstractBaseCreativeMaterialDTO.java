package com.starcloud.ops.business.app.api.xhs.material.dto;

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractBaseCreativeMaterialDTO {

    abstract String generateContent();

}
