package com.starcloud.ops.business.user.convert.level;


import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelConfigRespDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.*;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员等级 Convert
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelConvert {

    AdminUserLevelConvert INSTANCE = Mappers.getMapper(AdminUserLevelConvert.class);

    AdminUserLevelDO convert(AdminUserLevelCreateReqVO bean);

    AdminUserLevelDO convert(AdminUserLevelUpdateReqVO bean);

    AdminUserLevelRespVO convert(AdminUserLevelDO bean);

    List<AdminUserLevelRespVO> convertList(List<AdminUserLevelDO> list);

    List<AdminUserLevelSimpleRespVO> convertSimpleList(List<AdminUserLevelDO> list);

    List<AppAdminUserLevelRespVO> convertList02(List<AdminUserLevelDO> list);

    AdminUserLevelConfigRespDTO convert02(AdminUserLevelDO bean);

}
