package cn.iocoder.yudao.module.system.api.user;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Admin 用户 API 接口
 *
 * @author 芋道源码
 */
public interface AdminUserApi {

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserRespDTO getUser(Long id);

    /**
     * 查询租户id
     *
     * @param id
     * @return
     */
    Long getTenantId(Long id);

    /**
     * 通过用户 ID 查询用户们
     *
     * @param ids 用户 ID 们
     * @return 用户对象信息
     */
    List<AdminUserRespDTO> getUserList(Collection<Long> ids);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserRespDTO> getUserMap(Collection<Long> ids) {
        List<AdminUserRespDTO> users = getUserList(ids);
        return CollectionUtils.convertMap(users, AdminUserRespDTO::getId);
    }

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);

    /**
     * 根据用户名获取用户信息
     *
     * @param name 用户名称
     * @return 用户对象信息
     */
    AdminUserRespDTO getUserByUsername(String name);

    /**
     * 判断用户角色是否是super_admin 并且是部门创建者
     * @param userId
     * @return
     */
    boolean checkDeptRole(Long userId);

}
