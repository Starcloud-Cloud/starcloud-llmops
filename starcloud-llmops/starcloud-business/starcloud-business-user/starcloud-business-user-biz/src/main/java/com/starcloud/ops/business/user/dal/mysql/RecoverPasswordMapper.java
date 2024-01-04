package com.starcloud.ops.business.user.dal.mysql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.dal.dataobject.RecoverPasswordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecoverPasswordMapper extends BaseMapper<RecoverPasswordDO> {

    default RecoverPasswordDO selectByEmail(String email) {
        LambdaQueryWrapper<RecoverPasswordDO> queryWrapper = Wrappers.lambdaQuery(RecoverPasswordDO.class)
                .eq(RecoverPasswordDO::getEmail, email)
                .orderByDesc(RecoverPasswordDO::getRecoverDate)
                .last("limit 1");
        return this.selectOne(queryWrapper);
    }

    default RecoverPasswordDO selectByCode(String recoverCode) {
        LambdaQueryWrapper<RecoverPasswordDO> queryWrapper = Wrappers.lambdaQuery(RecoverPasswordDO.class)
                .eq(RecoverPasswordDO::getRecoverCode, recoverCode)
                .eq(RecoverPasswordDO::getStatus, 0)
                .orderByDesc(RecoverPasswordDO::getRecoverDate)
                .last("limit 1");
        return this.selectOne(queryWrapper);
    }


}
