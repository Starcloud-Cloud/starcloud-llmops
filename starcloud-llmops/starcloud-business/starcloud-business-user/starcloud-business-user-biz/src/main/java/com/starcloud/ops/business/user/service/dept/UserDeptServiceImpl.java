package com.starcloud.ops.business.user.service.dept;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateUserDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.UserDeptUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptUserRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.convert.dept.DeptConvert;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import com.starcloud.ops.business.user.dal.mysql.dept.UserDeptMapper;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.DEPT_NOT_FOUND;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class UserDeptServiceImpl implements UserDeptService {

    @Resource
    private AdminUserService userService;

    @Resource
    private DeptService deptService;

    @Resource
    private UserDeptMapper userDeptMapper;

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private final static String baseStr = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final static String prefix = "dept_invite_";

    @Override
    public List<UserDeptRespVO> deptList() {
        return userDeptMapper.deptList(WebFrameworkUtils.getLoginUserId());
    }

    @Override
    public List<DeptUserRespVO> userList(Long deptId) {
        validBindDept(deptId);
        return userDeptMapper.userList(deptId);
    }

    @Override
    public void checkout(Long deptId) {
        validBindDept(deptId);
        userService.updateUserDept(WebFrameworkUtils.getLoginUserId(), deptId);
    }

    @Override
    public DeptRespVO deptDetail(Long deptId) {
        validBindDept(deptId);
        DeptDO dept = deptService.getDept(deptId);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
        DeptRespVO respVO = DeptConvert.INSTANCE.convert(dept);
        respVO.setInviteCode(generateInviteCode(deptId));
        UserDeptDO userDeptDO = userDeptMapper.selectByDeptAndRole(deptId, UserDeptRoleEnum.SUPER_ADMIN);
        respVO.setAdminUserId(userDeptDO.getUserId());
        return respVO;
    }

    @Override
    public void updateDept(UserDeptUpdateReqVO reqVO) {
        UserDeptDO currentUserDept = userDeptMapper.selectByDeptAndUser(reqVO.getId(), WebFrameworkUtils.getLoginUserId());
        checkPermissions(currentUserDept, UserDeptRoleEnum.SUPER_ADMIN);
        deptService.updateDept(DeptConvert.INSTANCE.convert(reqVO));
    }

    @Override
    public void joinDept(String inviteCode) {
        String value = redisTemplate.boundValueOps(prefix + inviteCode).get();
        if (StringUtils.isBlank(value)) {
            throw exception(INVALID_CODE);
        }
        String[] split = value.split("_");
        try {
            Long deptId = Long.valueOf(split[0]);
            Long inviteUser = Long.valueOf(split[1]);

//            validDeptNum(WebFrameworkUtils.getLoginUserId());

            List<UserDeptDO> userDeptDOS = userDeptMapper.selectByDeptId(deptId);
            Optional<UserDeptDO> superUser = userDeptDOS.stream().filter(userDeptDO -> Objects.equals(UserDeptRoleEnum.SUPER_ADMIN.getRoleCode(), userDeptDO.getDeptRole())).findAny();
            if (!superUser.isPresent()) {
                throw exception(DEPT_IS_FULL,1);
            }

            AdminUserLevelDetailRespVO userLevelDetailRespVO = adminUserLevelService.getLevelList(superUser.get().getUserId()).get(0);
            Integer usableTeamUsers = Optional.ofNullable(userLevelDetailRespVO).map(AdminUserLevelDetailRespVO::getLevelConfig).map(AdminUserLevelDetailRespVO.LevelConfig::getUsableTeamUsers).orElse(1);
            if (usableTeamUsers <= userDeptDOS.size()) {
                throw exception(DEPT_IS_FULL, usableTeamUsers);
            }

            CreateUserDeptReqVO createUserDeptReqVO = CreateUserDeptReqVO.builder()
                    .deptId(deptId)
                    .userId(WebFrameworkUtils.getLoginUserId())
                    .inviteUser(inviteUser)
                    .deptRole(UserDeptRoleEnum.NORMAL.getRoleCode()).build();
            create(createUserDeptReqVO);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.warn("join dept error", e);
            throw exception(INVALID_CODE);
        }
    }

    public void create(CreateUserDeptReqVO reqVO) {
        String key = reqVO.getDeptId().toString() + "_" + reqVO.getUserId();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                UserDeptDO userDeptDO = userDeptMapper.selectByDeptAndUser(reqVO.getDeptId(), reqVO.getUserId());
                if (userDeptDO != null) {
                    return;
                }

                DeptDO dept = deptService.getDept(reqVO.getDeptId());
                if (dept == null) {
                    throw exception(DEPT_NOT_FOUND);
                }
                AdminUserDO user = userService.getUser(reqVO.getUserId());
                if (user == null) {
                    throw exception(USER_NOT_EXISTS);
                }
                UserDeptDO deptDO = DeptConvert.INSTANCE.convert(reqVO);
                userDeptMapper.insert(deptDO);
            }
        } catch (Exception e) {
            log.error("create user dept bind error {}", JSONUtil.toJsonPrettyStr(reqVO), e);
            throw exception(DEPT_BIND_ERROR);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(Long userDeptId) {
        UserDeptDO deleteUserDept = userDeptMapper.selectById(userDeptId);
        if (deleteUserDept == null) {
            return;
        }

        UserDeptDO currentUserDept = userDeptMapper.selectByDeptAndUser(deleteUserDept.getDeptId(), WebFrameworkUtils.getLoginUserId());
        checkPermissions(currentUserDept, UserDeptRoleEnum.SUPER_ADMIN);

        if (deleteUserDept.getDeptRole() >= UserDeptRoleEnum.SUPER_ADMIN.getRoleCode()) {
            throw exception(SUPER_ADMIN_DELETED);
        }

        AdminUserDO user = userService.getUser(deleteUserDept.getUserId());
        if (user != null && Objects.equals(deleteUserDept.getDeptId(), user.getDeptId())) {
            // 切换用户到其他空间
            List<UserDeptDO> userDeptDOS = userDeptMapper.selectByUserId(deleteUserDept.getUserId());
            if (CollectionUtils.isEmpty(userDeptDOS)) {
                throw exception(DELETE_ERROR);
            }
            userService.updateUserDept(user.getId(), userDeptDOS.get(0).getDeptId());
        }

        userDeptMapper.deleteById(deleteUserDept.getId());
    }

    @Override
    public UserDeptRespVO getSimpleDept(String inviteCode) {
        String value = redisTemplate.boundValueOps(prefix + inviteCode).get();
        if (StringUtils.isBlank(value)) {
            throw exception(INVALID_CODE);
        }
        String[] split = value.split("_");
        try {
            Long deptId = Long.valueOf(split[0]);
            DeptDO dept = deptService.getDept(deptId);
            return DeptConvert.INSTANCE.convert2(dept);
        } catch (Exception e) {
            log.warn("get simple error", e);
            throw exception(INVALID_CODE);
        }
    }

    @Override
    public void updateRole(Long userDeptId, Integer role) {
        UserDeptRoleEnum deptRoleEnum = UserDeptRoleEnum.getByRoleCode(role);

        UserDeptDO updateUserDept = userDeptMapper.selectById(userDeptId);
        UserDeptDO currentUserDept = userDeptMapper.selectByDeptAndUser(updateUserDept.getDeptId(), WebFrameworkUtils.getLoginUserId());

        if (Objects.equals(updateUserDept.getUserId(), WebFrameworkUtils.getLoginUserId())) {
            return;
        }

        checkPermissions(currentUserDept, UserDeptRoleEnum.SUPER_ADMIN);
        UserDeptDO userDeptDO = userDeptMapper.selectById(userDeptId);
        if (userDeptDO == null) {
            // 用户不在空间中
            return;
        }

        if (userDeptDO.getDeptRole() > currentUserDept.getDeptRole()) {
            // 当前用户权限小于被修改用户权限
            throw exception(INSUFFICIENT_PERMISSIONS);
        }

        if (deptRoleEnum.getRoleCode() >= currentUserDept.getDeptRole()) {
            // 目标权限高于当前用户权限
            throw exception(INSUFFICIENT_PERMISSIONS);
        }
        userDeptDO.setDeptRole(deptRoleEnum.getRoleCode());
        userDeptMapper.updateById(userDeptDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDept(CreateDeptReqVO createDeptReqVO) {
        Long deptId = deptService.createDept(DeptConvert.INSTANCE.convert(createDeptReqVO));
        validDeptNum(WebFrameworkUtils.getLoginUserId());

        CreateUserDeptReqVO createUserDeptReqVO = CreateUserDeptReqVO.builder()
                .deptId(deptId)
                .inviteUser(WebFrameworkUtils.getLoginUserId())
                .userId(WebFrameworkUtils.getLoginUserId())
                .deptRole(UserDeptRoleEnum.SUPER_ADMIN.getRoleCode()).build();
        create(createUserDeptReqVO);
    }

    @Override
    public UserDeptDO selectSuperAdminId(Long currentUserId) {
        AdminUserDO user = userService.getUser(currentUserId);
        if (user == null) {
            return null;
        }
        UserDeptDO userDeptDO = userDeptMapper.selectByDeptAndRole(user.getDeptId(), UserDeptRoleEnum.SUPER_ADMIN);
        return userDeptDO;
    }

    @Override
    public void recordRights(UserDeptDO deptDO, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        if (deptDO == null || deptDO.getId() == null) {
            return;
        }

        if (AdminUserRightsTypeEnum.MAGIC_BEAN.equals(rightsType)) {
            userDeptMapper.recordAppRights(rightAmount,deptDO.getId());
        } else if (AdminUserRightsTypeEnum.MAGIC_IMAGE.equals(rightsType)) {
            userDeptMapper.recordImageRights(rightAmount,deptDO.getId());
        }
    }

    private void validDeptNum(Long userId) {
        AdminUserLevelDetailRespVO userLevelDetailRespVO = adminUserLevelService.getLevelList(userId).get(0);
        Integer usableTeams = Optional.ofNullable(userLevelDetailRespVO).map(AdminUserLevelDetailRespVO::getLevelConfig).map(AdminUserLevelDetailRespVO.LevelConfig::getUsableTeams).orElse(1);
        List<UserDeptDO> userDepts = userDeptMapper.selectByUserId(WebFrameworkUtils.getLoginUserId());
        if (usableTeams <= userDepts.size()) {
            throw exception(TOO_MANY_DEPT_NUM, usableTeams);
        }
    }

    private void validBindDept(Long deptId) {
        UserDeptDO userDeptDO = userDeptMapper.selectByDeptAndUser(deptId, WebFrameworkUtils.getLoginUserId());
        if (userDeptDO == null) {
            throw exception(NOT_IN_THIS_DEPT);
        }
    }

    private void checkPermissions(UserDeptDO currentUserDept, UserDeptRoleEnum deptRoleEnum) {
        if (currentUserDept == null
                || currentUserDept.getDeptRole() < deptRoleEnum.getRoleCode()) {
            throw exception(INSUFFICIENT_PERMISSIONS);
        }
    }

    private String generateInviteCode(Long deptId) {
        String code = RandomUtil.randomString(baseStr, 8);
        String value = deptId.toString() + "_" + WebFrameworkUtils.getLoginUserId();
        if (redisTemplate.boundValueOps(prefix + code).setIfAbsent(value, 10, TimeUnit.MINUTES)) {
            return code;
        }
        return generateInviteCode(deptId);
    }


}
