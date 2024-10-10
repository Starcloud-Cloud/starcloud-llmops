package com.starcloud.ops.business.user.service.dept;

import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateUserDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.UserDeptUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptUserRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;

import java.util.List;
import java.util.Set;

public interface UserDeptService {

    /**
     * 当前用户所在的部门列表
     *
     * @return
     */
    List<UserDeptRespVO> deptList();

    /**
     * 当前部门下的所有用户
     *
     * @return
     */
    List<DeptUserRespVO> userList(Long deptId);

    /**
     * 切换用户到指定部门下
     *
     * @param deptId
     */
    void checkout(Long deptId);

    /**
     * 部门详情
     *
     * @param deptId
     * @return
     */
    DeptRespVO deptDetail(Long deptId);

    /**
     * 修改部门
     */
    void updateDept(UserDeptUpdateReqVO reqVO);

    /**
     * 加入部门
     *
     * @param inviteCode
     */
    void joinDept(String inviteCode);

    /**
     * 绑定新用户
     *
     * @param reqVO
     */
    void create(CreateUserDeptReqVO reqVO);

    /**
     * 移除用户
     *
     * @param userDeptId
     */
    void removeUser(Long userDeptId);

    /**
     * 邀请码获取部门简要信息
     *
     * @param inviteCode
     * @return
     */
    UserDeptRespVO getSimpleDept(String inviteCode);

    /**
     * 修改角色
     *
     * @param userDeptId
     * @param role
     */
    void updateRole(Long userDeptId, Integer role);

    /**
     * 创建部门
     *
     * @param createDeptReqVO
     */
    void createDept(CreateDeptReqVO createDeptReqVO);

    /**
     * 查询当前用户所在部门的超级管理员
     *
     * @param currentUserId
     * @return
     */
    UserDeptDO selectSuperAdminId(Long currentUserId);

    /**
     * 查询此用户是超级管理员的部门
     *
     * @param userId
     * @return
     */
    UserDeptDO selectOwnerDept(Long userId);

    /**
     * 查询用户的部门信息
     */
    UserDeptDO selectByDeptAndUser(Long userDeptId, Long userId);

    /**
     * 记录消耗
     *
     * @param deptDO
     * @param rightsType
     * @param rightAmount
     */
    void recordRights(UserDeptDO deptDO, Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount);

    /**
     * yo
     */
    Set<String> getUserPermission();
}
