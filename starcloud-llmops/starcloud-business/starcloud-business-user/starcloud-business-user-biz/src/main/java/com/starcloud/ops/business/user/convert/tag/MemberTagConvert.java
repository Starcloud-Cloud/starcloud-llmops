package com.starcloud.ops.business.user.convert.tag;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigRespVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员标签 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberTagConvert {

    MemberTagConvert INSTANCE = Mappers.getMapper(MemberTagConvert.class);

    AdminUserTagConfigDO convert(AdminUserTagConfigCreateReqVO bean);

    AdminUserTagConfigDO convert(AdminUserTagConfigUpdateReqVO bean);

    AdminUserTagConfigRespVO convert(AdminUserTagConfigDO bean);

    List<AdminUserTagConfigRespVO> convertList(List<AdminUserTagConfigDO> list);

    PageResult<AdminUserTagConfigRespVO> convertPage(PageResult<AdminUserTagConfigDO> page);

}
