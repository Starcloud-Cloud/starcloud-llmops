package com.starcloud.ops.business.user.convert.level;


import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelConfigRespDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.*;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员等级 Convert
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelConfigConvert {

    AdminUserLevelConfigConvert INSTANCE = Mappers.getMapper(AdminUserLevelConfigConvert.class);

    AdminUserLevelConfigDO convert(AdminUserLevelConfigCreateReqVO bean);

    AdminUserLevelConfigDO convert(AdminUserLevelConfigUpdateReqVO bean);

    AdminUserLevelConfigRespVO convert(AdminUserLevelConfigDO bean);

    List<AdminUserLevelConfigRespVO> convertList(List<AdminUserLevelConfigDO> list);

    List<AdminUserLevelConfigSimpleRespVO> convertSimpleList(List<AdminUserLevelConfigDO> list);

    List<AppAdminUserLevelConfigRespVO> convertList02(List<AdminUserLevelConfigDO> list);

    AdminUserLevelConfigRespDTO convert02(AdminUserLevelConfigDO bean);

}
