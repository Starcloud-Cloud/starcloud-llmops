package com.starcloud.ops.business.user.convert.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordRespVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员等级记录 Convert
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelRecordConvert {

    AdminUserLevelRecordConvert INSTANCE = Mappers.getMapper(AdminUserLevelRecordConvert.class);

    AdminUserLevelRecordRespVO convert(AdminUserLevelRecordDO bean);


    List<AdminUserLevelRecordRespVO> convertList(List<AdminUserLevelRecordDO> list);

    PageResult<AdminUserLevelRecordRespVO> convertPage(PageResult<AdminUserLevelRecordDO> page);

    default AdminUserLevelRecordDO copyTo(AdminUserLevelDO from, AdminUserLevelRecordDO to) {
        if (from != null) {
            to.setLevelId(from.getId());
//            to.setCurrentLevel(from.getLevel());
        }
        return to;
    }
}
