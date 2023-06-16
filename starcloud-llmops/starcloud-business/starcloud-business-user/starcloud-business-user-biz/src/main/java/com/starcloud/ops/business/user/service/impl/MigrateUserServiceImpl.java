package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.UserRoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.mq.producer.permission.PermissionProducer;
import com.starcloud.ops.business.user.pojo.dto.WpUserDTO;
import com.starcloud.ops.business.user.pojo.dto.MigrateResultDTO;
import com.starcloud.ops.business.user.service.MigrateUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class MigrateUserServiceImpl implements MigrateUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private PermissionProducer permissionProducer;

    @Override
    public List<MigrateResultDTO> migrateUsers(List<WpUserDTO> wpUserDTOS) {
       List<MigrateResultDTO> migrateResults = new ArrayList<>(wpUserDTOS.size());
        for (WpUserDTO wpUserDTO : wpUserDTOS) {
            MigrateResultDTO resultDTO = validateUser(wpUserDTO);
            if (!resultDTO.isSuccess()) {
                migrateResults.add(resultDTO);
                continue;
            }
            try {
                DeptDO deptDO = new DeptDO();
                deptDO.setParentId(3L);
                deptDO.setName(wpUserDTO.getUsername() + "_dept_wp");
                deptDO.setStatus(0);
                deptDO.setTenantId(2L);
                deptDO.setEmail(wpUserDTO.getEmail());
                deptMapper.insert(deptDO);

                AdminUserDO userDO = new AdminUserDO();
                userDO.setDeptId(deptDO.getId());
                userDO.setUsername(wpUserDTO.getUsername());
                userDO.setEmail(wpUserDTO.getEmail());
                userDO.setStatus(0);
                userDO.setNickname(wpUserDTO.getNickname());
                userDO.setTenantId(2L);
                userDO.setPassword(passwordEncoder.encode("abc123456"));
                adminUserMapper.insert(userDO);

                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(userDO.getId());
                userRoleDO.setRoleId(2L);
                userRoleDO.setCreator(userDO.getUsername());
                userRoleDO.setUpdater(userDO.getUpdater());
                userRoleDO.setTenantId(userDO.getTenantId());
                userRoleMapper.insert(userRoleDO);
                migrateResults.add(resultDTO);
            } catch (Exception e) {
                MigrateResultDTO migrateResultDTO = new MigrateResultDTO();
                migrateResultDTO.setUsername(wpUserDTO.getUsername());
                migrateResultDTO.setEmail(wpUserDTO.getEmail());
                migrateResultDTO.setErrorMsg(e.getMessage());
                migrateResults.add(resultDTO);
            }
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
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
