package com.starcloud.ops.business.app.api.xhs.material.dto;

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractBaseCreativeMaterialDTO {

    /**
     * 摘要内容 用于筛选
     * @return
     */
    abstract String generateContent();

}
