package com.starcloud.ops.business.user.service;

import com.starcloud.ops.business.user.pojo.dto.WpUserDTO;
import com.starcloud.ops.business.user.pojo.dto.MigrateResultDTO;

import java.util.List;

public interface MigrateUserService {

    /**
     * 迁移wp用户
     * @param file
     * @return
     */
    List<MigrateResultDTO> migrateUsers(List<WpUserDTO> wpUserDTOS);
}
