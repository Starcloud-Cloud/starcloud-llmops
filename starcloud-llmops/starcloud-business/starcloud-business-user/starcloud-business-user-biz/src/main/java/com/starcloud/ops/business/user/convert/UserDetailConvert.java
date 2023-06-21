package com.starcloud.ops.business.user.convert;


import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDetailConvert {

    UserDetailConvert INSTANCE = Mappers.getMapper(UserDetailConvert.class);


    UserDetailVO useToDetail(AdminUserDO userDO);

}
