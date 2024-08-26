package com.starcloud.ops.business.user.dal.mysql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.dal.dataObject.RegisterUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface RegisterUserMapper extends BaseMapper<RegisterUserDO> {

    default RegisterUserDO selectByUsername(String username) {
        LambdaQueryWrapper<RegisterUserDO> queryWrapper = Wrappers.lambdaQuery(RegisterUserDO.class)
                .eq(RegisterUserDO::getUsername, username)
                .last("limit 1");
        return selectOne(queryWrapper);
    }


    default RegisterUserDO selectByEmail(String email) {

        LambdaQueryWrapper<RegisterUserDO> queryWrapper = Wrappers.lambdaQuery(RegisterUserDO.class)
                .eq(RegisterUserDO::getEmail, email)
                .last("limit 1");
        return selectOne(queryWrapper);
    }

    default RegisterUserDO selectByActivationCode(String activationCode) {
        LambdaQueryWrapper<RegisterUserDO> queryWrapper = Wrappers.lambdaQuery(RegisterUserDO.class)
                .eq(RegisterUserDO::getActivationCode, activationCode)
                .eq(RegisterUserDO::getStatus, 0)
                .orderByDesc(RegisterUserDO::getRegisterDate)
                .last("limit 1");
        return this.selectOne(queryWrapper);
    }

}
