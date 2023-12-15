package com.starcloud.ops.business.user.convert.rights;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.AdminUserRightsRecordRespVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
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
public interface AdminUserRightsRecordConvert {

    AdminUserRightsRecordConvert INSTANCE = Mappers.getMapper(AdminUserRightsRecordConvert.class);

    default PageResult<AdminUserRightsRecordRespVO> convertPage(PageResult<AdminUserRightsRecordDO> pageResult, List<AdminUserDO> users) {
        PageResult<AdminUserRightsRecordRespVO> voPageResult = convertPage(pageResult);
        // user 拼接
        Map<Long, AdminUserDO> userMap = convertMap(users, AdminUserDO::getId);
        voPageResult.getList().forEach(record -> MapUtils.findAndThen(userMap, record.getUserId(),
                memberUserRespDTO -> record.setNickname(memberUserRespDTO.getNickname())));
        return voPageResult;
    }
    PageResult<AdminUserRightsRecordRespVO> convertPage(PageResult<AdminUserRightsRecordDO> pageResult);

    PageResult<AdminUserRightsRecordRespVO> convertPage02(PageResult<AdminUserRightsRecordDO> pageResult);

}
