package com.starcloud.ops.business.user.dal.mysql.level;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelListReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员等级 Mapper
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelMapper extends BaseMapperX<AdminUserLevelDO> {

    default List<AdminUserLevelDO> selectList(AdminUserLevelListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<AdminUserLevelDO>()
                .likeIfPresent(AdminUserLevelDO::getName, reqVO.getName())
                .eqIfPresent(AdminUserLevelDO::getStatus, reqVO.getStatus())
                .orderByAsc(AdminUserLevelDO::getLevel));
    }


    default List<AdminUserLevelDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<AdminUserLevelDO>()
                .eq(AdminUserLevelDO::getStatus, status)
                .orderByAsc(AdminUserLevelDO::getLevel));
    }

}
