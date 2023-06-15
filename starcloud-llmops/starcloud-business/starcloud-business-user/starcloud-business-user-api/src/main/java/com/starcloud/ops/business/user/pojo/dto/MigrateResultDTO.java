package com.starcloud.ops.business.user.pojo.dto;

import lombok.Data;

@Data
public class MigrateResultDTO {

    private String username;

    private String email;

    private boolean success;

    private String errorMsg;

}
