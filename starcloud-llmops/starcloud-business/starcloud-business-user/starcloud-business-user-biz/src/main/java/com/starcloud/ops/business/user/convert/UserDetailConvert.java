package com.starcloud.ops.business.user.convert;


import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsCollectRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserInfoRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserDetailConvert {

    UserDetailConvert INSTANCE = Mappers.getMapper(UserDetailConvert.class);


    UserDetailVO useToDetail(AdminUserDO userDO);

    @Mapping(source = "adminUserLevelDOS", target = "levels")
    @Mapping(source = "adminUserRightsCollectRespVOS", target = "rights")
    @Mapping(source = "adminUserTeamRightsCollectRespVOS", target = "teamRights")
    AdminUserInfoRespVO useToDetail02(AdminUserDO userDO, List<AdminUserLevelDetailRespVO> adminUserLevelDOS, List<AdminUserRightsCollectRespVO> adminUserRightsCollectRespVOS, List<AdminUserRightsCollectRespVO> adminUserTeamRightsCollectRespVOS);

}
