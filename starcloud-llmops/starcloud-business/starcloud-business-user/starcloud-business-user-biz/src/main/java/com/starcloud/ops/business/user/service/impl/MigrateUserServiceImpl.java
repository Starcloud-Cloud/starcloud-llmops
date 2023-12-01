package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants;
import com.starcloud.ops.business.user.pojo.dto.MigrateResultDTO;
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.pojo.dto.WpUserDTO;
import com.starcloud.ops.business.user.service.MigrateUserService;
import com.starcloud.ops.business.user.service.StarUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MigrateUserServiceImpl implements MigrateUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

//    @Autowired
//    private PermissionProducer permissionProducer;

    @Autowired
    private StarUserService starUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public List<MigrateResultDTO> migrateUsers(List<WpUserDTO> wpUserDTOS) {
       List<MigrateResultDTO> migrateResults = new ArrayList<>(wpUserDTOS.size());
        for (WpUserDTO wpUserDTO : wpUserDTOS) {
            MigrateResultDTO resultDTO = validateUser(wpUserDTO);
            if (!resultDTO.isSuccess()) {
                migrateResults.add(resultDTO);
                continue;
            }
            try {
                UserDTO userDTO = UserDTO.builder().username(wpUserDTO.getUsername())
                        .email(wpUserDTO.getEmail())
                        .password(passwordEncoder.encode("abc123"))
                        .parentDeptId(3L)
                        .userStatus(CommonStatusEnum.ENABLE.getStatus()).build();

                starUserService.createNewUser(userDTO);
                migrateResults.add(resultDTO);
            } catch (Exception e) {
                MigrateResultDTO migrateResultDTO = new MigrateResultDTO();
                migrateResultDTO.setUsername(wpUserDTO.getUsername());
                migrateResultDTO.setEmail(wpUserDTO.getEmail());
                migrateResultDTO.setErrorMsg(e.getMessage());
                migrateResults.add(resultDTO);
            }
        }
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//
//            @Override
//            public void afterCommit() {
//                permissionProducer.sendUserRoleRefreshMessage();
//            }
//
//        });
        return migrateResults;
    }


    private MigrateResultDTO validateUser(WpUserDTO wpUserDTO) {
        MigrateResultDTO resultDTO = new MigrateResultDTO();
        String username = wpUserDTO.getUsername();
        String email = wpUserDTO.getEmail();

        if (StringUtils.isBlank(username)) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg("username is blank");
            return resultDTO;
        }

        if (StringUtils.isBlank(email)) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg("email is blank");
            return resultDTO;
        }

        resultDTO.setUsername(username);
        resultDTO.setEmail(email);
        AdminUserDO byUsername = adminUserMapper.selectByUsername(username);
        if (byUsername != null) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg("username is exists");
            return resultDTO;
        }
        AdminUserDO byEmail = adminUserMapper.selectByEmail(email);
        if (byEmail != null) {
            resultDTO.setSuccess(false);
            resultDTO.setErrorMsg("email is exists");
            return resultDTO;
        }
        resultDTO.setSuccess(true);
        return resultDTO;
    }
}
