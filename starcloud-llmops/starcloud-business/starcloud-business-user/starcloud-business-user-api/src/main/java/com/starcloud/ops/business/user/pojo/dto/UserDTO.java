package com.starcloud.ops.business.user.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private String username;

    private String email;

    private String password;

    private Long parentDeptId;

    private Integer userStatus;

    private String mobile;

    private Long inviteUserId;

    private Long tenantId;
}
