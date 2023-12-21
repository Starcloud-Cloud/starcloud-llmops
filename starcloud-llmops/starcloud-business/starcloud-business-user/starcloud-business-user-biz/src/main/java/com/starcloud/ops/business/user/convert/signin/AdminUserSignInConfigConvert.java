package com.starcloud.ops.business.user.convert.signin;


import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigRespVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AppAdminUserSignInConfigRespVO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 签到规则 Convert
 *
 * @author QingX
 */
@Mapper
public interface AdminUserSignInConfigConvert {

    AdminUserSignInConfigConvert INSTANCE = Mappers.getMapper(AdminUserSignInConfigConvert.class);

    AdminUserSignInConfigDO convert(AdminUserSignInConfigCreateReqVO bean);

    AdminUserSignInConfigDO convert(AdminUserSignInConfigUpdateReqVO bean);

    AdminUserSignInConfigRespVO convert(AdminUserSignInConfigDO bean);

    List<AdminUserSignInConfigRespVO> convertList(List<AdminUserSignInConfigDO> list);

    List<AppAdminUserSignInConfigRespVO> convertList02(List<AdminUserSignInConfigDO> list);

}
