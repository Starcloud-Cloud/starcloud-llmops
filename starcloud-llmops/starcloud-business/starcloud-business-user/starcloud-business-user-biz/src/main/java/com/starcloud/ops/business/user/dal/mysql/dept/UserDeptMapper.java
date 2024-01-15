package com.starcloud.ops.business.user.dal.mysql.dept;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptUserRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
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

    List<UserDeptRespVO> deptList(@Param("userId") Long userId);

    List<DeptUserRespVO> userList(@Param("deptId") Long deptId);
}
