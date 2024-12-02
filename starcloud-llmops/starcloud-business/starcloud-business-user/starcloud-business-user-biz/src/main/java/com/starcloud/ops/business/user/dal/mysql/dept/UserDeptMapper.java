package com.starcloud.ops.business.user.dal.mysql.dept;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptUserRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDeptMapper extends BaseMapperX<UserDeptDO> {

    default UserDeptDO selectByDeptAndUser(Long deptId, Long userId) {
        LambdaQueryWrapper<UserDeptDO> wrapper = Wrappers.lambdaQuery(UserDeptDO.class)
                .eq(UserDeptDO::getDeptId, deptId)
                .eq(UserDeptDO::getUserId, userId);
        return selectOne(wrapper);
    }

    default UserDeptDO selectByDeptAndRole(Long deptId, UserDeptRoleEnum role) {
        LambdaQueryWrapper<UserDeptDO> wrapper = Wrappers.lambdaQuery(UserDeptDO.class)
                .eq(UserDeptDO::getDeptId, deptId)
                .eq(UserDeptDO::getDeptRole, role.getRoleCode())
                .last("limit 1");
        return selectOne(wrapper);
    }

    default UserDeptDO selectByUserAndRole(Long userId, UserDeptRoleEnum role) {
        LambdaQueryWrapper<UserDeptDO> wrapper = Wrappers.lambdaQuery(UserDeptDO.class)
                .eq(UserDeptDO::getUserId, userId)
                .eq(UserDeptDO::getDeptRole, role.getRoleCode())
                .last("limit 1");
        return selectOne(wrapper);
    }


    default List<UserDeptDO> selectByUserId(Long userId) {
        LambdaQueryWrapper<UserDeptDO> wrapper = Wrappers.lambdaQuery(UserDeptDO.class)
                .eq(UserDeptDO::getUserId, userId);
        return selectList(wrapper);
    }

    default List<UserDeptDO> selectByDeptId(Long deptId) {
        LambdaQueryWrapper<UserDeptDO> wrapper = Wrappers.lambdaQuery(UserDeptDO.class)
                .eq(UserDeptDO::getDeptId, deptId);
        return selectList(wrapper);
    }

    void recordImageRights(@Param("rightAmount") Integer rightAmount, @Param("id") Long id);

    void recordAppRights(@Param("rightAmount") Integer rightAmount, @Param("id") Long id);

    List<UserDeptRespVO> deptList(@Param("userId") Long userId);

    List<DeptUserRespVO> userList(@Param("deptId") Long deptId);
}
