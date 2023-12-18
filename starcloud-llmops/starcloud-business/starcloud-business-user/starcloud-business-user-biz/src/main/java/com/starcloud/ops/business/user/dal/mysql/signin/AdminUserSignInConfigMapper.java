package com.starcloud.ops.business.user.dal.mysql.signin;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 签到规则 Mapper
 *
 * @author QingX
 */
@Mapper
public interface AdminUserSignInConfigMapper extends BaseMapperX<AdminUserSignInConfigDO> {

    default AdminUserSignInConfigDO selectByDay(Integer day) {
        return selectOne(AdminUserSignInConfigDO::getDay, day);
    }

    default List <AdminUserSignInConfigDO> selectListByStatus(Integer status) {
        return selectList(AdminUserSignInConfigDO::getStatus, status);
    }
}
