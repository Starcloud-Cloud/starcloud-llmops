package com.starcloud.ops.business.user.pojo;

import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * 通用返回
 */
@Data
public class PermissionResult<T> extends CommonResult<T> {

    /**
     * 权限集合
     */
    private Map<String, Boolean> permissionMap;

    public static <T> PermissionResult<T> success(T data, Map<String, Boolean> permissionMap) {
        PermissionResult<T> result = new PermissionResult<>();
        result.setCode(GlobalErrorCodeConstants.SUCCESS.getCode());
        result.setData(data);
        result.setMsg(StringUtils.EMPTY);
        result.setPermissionMap(permissionMap);
        return result;
    }
}
