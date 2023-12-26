package com.starcloud.ops.business.user.convert.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelRespVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员等级记录 Convert
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelConvert {

    AdminUserLevelConvert INSTANCE = Mappers.getMapper(AdminUserLevelConvert.class);

    AdminUserLevelRespVO convert(AdminUserLevelDO bean);

    @Mapping(source = "levelName", target = "levelName")
    @Mapping(source = "startTime", target = "validStartTime")
    @Mapping(source = "endTime", target = "validEndTime")
    AdminUserLevelDO convert01(AdminUserLevelCreateReqVO bean, String levelName, LocalDateTime startTime, LocalDateTime endTime);


    List<AdminUserLevelRespVO> convertList(List<AdminUserLevelDO> list);

    PageResult<AdminUserLevelRespVO> convertPage(PageResult<AdminUserLevelDO> page);

    default AdminUserLevelDO copyTo(AdminUserLevelConfigDO from, AdminUserLevelDO to) {
        if (from != null) {
            to.setLevelId(from.getId());
//            to.setCurrentLevel(from.getLevelConfig());
        }
        return to;
    }
}
