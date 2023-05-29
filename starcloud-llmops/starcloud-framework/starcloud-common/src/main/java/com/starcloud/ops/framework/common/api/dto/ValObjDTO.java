package com.starcloud.ops.framework.common.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author huyi
 * @date 2022/8/11
 * @time 16:14
 * @description
 */
@Data
@Accessors(chain = true)
public class ValObjDTO {

    private String key;

    private Object val;

    public static ValObjDTO of(String key, Object val) {
        return new ValObjDTO().setKey(key).setVal(val);
    }
}
