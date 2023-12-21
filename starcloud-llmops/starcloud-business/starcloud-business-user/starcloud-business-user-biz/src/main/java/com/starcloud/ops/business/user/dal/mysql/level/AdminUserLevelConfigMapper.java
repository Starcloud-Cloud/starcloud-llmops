package com.starcloud.ops.business.user.dal.mysql.level;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigListReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员等级 Mapper
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelConfigMapper extends BaseMapperX<AdminUserLevelConfigDO> {

    default List<AdminUserLevelConfigDO> selectList(AdminUserLevelConfigListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<AdminUserLevelConfigDO>()
                .likeIfPresent(AdminUserLevelConfigDO::getName, reqVO.getName())
                .eqIfPresent(AdminUserLevelConfigDO::getStatus, reqVO.getStatus())
                .orderByAsc(AdminUserLevelConfigDO::getLevel));
    }


    default List<AdminUserLevelConfigDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<AdminUserLevelConfigDO>()
                .eq(AdminUserLevelConfigDO::getStatus, status)
                .orderByAsc(AdminUserLevelConfigDO::getLevel));
    }

}
