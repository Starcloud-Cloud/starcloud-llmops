package com.starcloud.ops.business.user.convert;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    AdminUserDO convert(UserProfileUpdateRequest bean);
}
