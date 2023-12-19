package com.starcloud.ops.business.user.convert.rights;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 用户积分记录 Convert
 *
 * @author QingX
 */
@Mapper
public interface AdminUserRightsConvert {

    AdminUserRightsConvert INSTANCE = Mappers.getMapper(AdminUserRightsConvert.class);

    default PageResult<AdminUserRightsRespVO> convertPage(PageResult<AdminUserRightsDO> pageResult, List<AdminUserDO> users) {
        PageResult<AdminUserRightsRespVO> voPageResult = convertPage(pageResult);
        // user 拼接
        Map<Long, AdminUserDO> userMap = convertMap(users, AdminUserDO::getId);
        voPageResult.getList().forEach(record -> MapUtils.findAndThen(userMap, record.getUserId(),
                memberUserRespDTO -> record.setNickname(memberUserRespDTO.getNickname())));
        return voPageResult;
    }
    PageResult<AdminUserRightsRespVO> convertPage(PageResult<AdminUserRightsDO> pageResult);

    PageResult<AdminUserRightsRespVO> convertPage02(PageResult<AdminUserRightsDO> pageResult);

}
