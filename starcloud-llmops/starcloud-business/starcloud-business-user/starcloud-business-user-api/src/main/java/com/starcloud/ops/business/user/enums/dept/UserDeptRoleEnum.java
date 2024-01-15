package com.starcloud.ops.business.user.enums.dept;

import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.DEPT_ROLE_NOT_EXIST;

@Getter
public enum UserDeptRoleEnum {
    NORMAL(100,"普通用户"),

    ADMIN(500,"管理员"),

    SUPER_ADMIN(1000,"超级管理员"),
    ;


    private Integer roleCode;

    private String desc;

    UserDeptRoleEnum(Integer roleCode, String desc) {
        this.roleCode = roleCode;
        this.desc = desc;
    }

    public static UserDeptRoleEnum getByRoleCode(Integer roleCode) {
        for (UserDeptRoleEnum value : UserDeptRoleEnum.values()) {
            if (value.getRoleCode().equals(roleCode)) {
                return value;
            }
        }
        throw exception(DEPT_ROLE_NOT_EXIST);
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(UserDeptRoleEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getRoleCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
