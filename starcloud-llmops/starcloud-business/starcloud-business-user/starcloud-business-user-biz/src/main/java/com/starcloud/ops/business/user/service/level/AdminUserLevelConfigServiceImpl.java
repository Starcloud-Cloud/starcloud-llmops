package com.starcloud.ops.business.user.service.level;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.system.api.permission.RoleApi;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.annotations.VisibleForTesting;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigListReqVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConfigConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;


/**
 * 会员等级 Service 实现类
 *
 * @author owen
 */
@Slf4j
@Service
@Validated
public class AdminUserLevelConfigServiceImpl implements AdminUserLevelConfigService {

    @Resource
    private AdminUserLevelConfigMapper adminUserLevelConfigMapper;

    @Resource
    private RoleApi roleApi;


    @Override
    public Long createLevel(AdminUserLevelConfigCreateReqVO createReqVO) {
        // 校验配置是否有效
        validateConfigValid(null, createReqVO.getName(), createReqVO.getLevel(), createReqVO.getRoleId());

        // 插入
        AdminUserLevelConfigDO level = AdminUserLevelConfigConvert.INSTANCE.convert(createReqVO);
        adminUserLevelConfigMapper.insert(level);
        // 返回
        return level.getId();
    }

    @Override
    public void updateLevel(AdminUserLevelConfigUpdateReqVO updateReqVO) {
        // 校验存在
        validateLevelExists(updateReqVO.getId());
        // 校验配置是否有效
        validateConfigValid(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getLevel(), updateReqVO.getRoleId());

        // 更新
        AdminUserLevelConfigDO updateObj = AdminUserLevelConfigConvert.INSTANCE.convert(updateReqVO);
        adminUserLevelConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteLevel(Long id) {
        // 校验存在
        validateLevelExists(id);
        // 校验分组下是否有用户
        validateLevelHasUser(id);
        // 删除
        adminUserLevelConfigMapper.deleteById(id);
    }

    @VisibleForTesting
    AdminUserLevelConfigDO validateLevelExists(Long id) {
        AdminUserLevelConfigDO levelDO = adminUserLevelConfigMapper.selectById(id);
        if (levelDO == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        return levelDO;
    }

    @VisibleForTesting
    void validateNameUnique(List<AdminUserLevelConfigDO> list, Long id, String name) {
        for (AdminUserLevelConfigDO levelDO : list) {
            if (ObjUtil.notEqual(levelDO.getName(), name)) {
                continue;
            }
            if (id == null || !id.equals(levelDO.getId())) {
                throw exception(LEVEL_NAME_EXISTS, levelDO.getName());
            }
        }
    }

    @VisibleForTesting
    void validateLevelUnique(List<AdminUserLevelConfigDO> list, Long id, Integer level) {
        for (AdminUserLevelConfigDO levelDO : list) {
            if (ObjUtil.notEqual(levelDO.getLevel(), level)) {
                continue;
            }

            if (id == null || !id.equals(levelDO.getId())) {
                throw exception(LEVEL_VALUE_EXISTS, levelDO.getLevel(), levelDO.getName());
            }
        }
    }

    @VisibleForTesting
    void validateRoleIdUnique(List<AdminUserLevelConfigDO> list, Long id, Long roleId) {
        // 校验角色是否存在
        roleApi.validRoleList(CollUtil.list(true, roleId));
        // 校验角色是否唯一
        for (AdminUserLevelConfigDO levelDO : list) {
            if (ObjUtil.notEqual(levelDO.getRoleId(), roleId)) {
                continue;
            }
            if (id == null || !id.equals(levelDO.getId())) {
                throw exception(LEVEL_ROLE_EXISTS, levelDO. getRoleId());
            }
        }
    }

    @VisibleForTesting
    void validateConfigValid(Long id, String name, Integer level, Long roleId) {
        List<AdminUserLevelConfigDO> list = adminUserLevelConfigMapper.selectList();
        // 校验名称唯一
        validateNameUnique(list, id, name);
        // 校验等级唯一
        validateLevelUnique(list, id, level);
        // 校验角色存在且唯一
        validateRoleIdUnique(list, id, roleId);
    }

    @VisibleForTesting
    void validateLevelHasUser(Long id) {
//        Long count = memberUserService.getUserCountByLevelId(id);
//        if (count > 0) {
//            throw exception(LEVEL_HAS_USER);
//        }
    }

    @Override
    public AdminUserLevelConfigDO getLevelConfig(Long id) {
        return id != null && id > 0 ? adminUserLevelConfigMapper.selectById(id) : null;
    }

    @Override
    public List<AdminUserLevelConfigDO> getLevelList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return adminUserLevelConfigMapper.selectBatchIds(ids);
    }

    @Override
    public List<AdminUserLevelConfigDO> getLevelList(AdminUserLevelConfigListReqVO listReqVO) {
        return adminUserLevelConfigMapper.selectList(listReqVO);
    }

    @Override
    public List<AdminUserLevelConfigDO> getLevelListByStatus(Integer status) {
        return adminUserLevelConfigMapper.selectListByStatus(status);
    }

    /**
     * 获得指定状态的会员等级列表
     *
     * @param roleId 角色 ID
     * @return 会员等级配置信息
     */
    @Override
    public AdminUserLevelConfigDO getLevelByRoleId(Long roleId){

        return adminUserLevelConfigMapper.selectOne( Wrappers.lambdaQuery(AdminUserLevelConfigDO.class).eq(AdminUserLevelConfigDO::getRoleId,roleId).eq(AdminUserLevelConfigDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
    }

    /**
     * 获得指定状态的会员等级列表
     *
     * @param roleId 状态
     * @return 会员等级列表
     */
    @Override
    public List<AdminUserLevelConfigDO> getListByRoleId(Long roleId) {
        return adminUserLevelConfigMapper.selectList(
                Wrappers.lambdaQuery(AdminUserLevelConfigDO.class)
                        .eq(AdminUserLevelConfigDO::getRoleId,roleId)
                        .eq(AdminUserLevelConfigDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
        );
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateUserLevel(MemberUserUpdateLevelReqVO updateReqVO) {
//        MemberUserDO user = memberUserService.getUser(updateReqVO.getId());
//        if (user == null) {
//            throw exception(USER_NOT_EXISTS);
//        }
//        // 等级未发生变化
//        if (ObjUtil.equal(user.getLevelId(), updateReqVO.getLevelId())) {
//            return;
//        }
//
//        // 1. 记录等级变动
//        MemberLevelRecordDO levelRecord = new MemberLevelRecordDO()
//                .setUserId(user.getId()).setRemark(updateReqVO.getReason());
//        AdminUserLevelConfigDO memberLevel = null;
//        if (updateReqVO.getLevelId() == null) {
//            // 取消用户等级时，需要扣减经验
//            levelRecord.setMagicImage(-user.getMagicImage());
//            levelRecord.setUserExperience(0);
//            levelRecord.setDescription("管理员取消了等级");
//        } else {
//            // 复制等级配置
//            memberLevel = validateLevelExists(updateReqVO.getLevelId());
//            MemberLevelRecordConvert.INSTANCE.copyTo(memberLevel, levelRecord);
//            // 变动经验值 = 等级的升级经验 - 会员当前的经验；正数为增加经验，负数为扣减经验
//            levelRecord.setMagicImage(memberLevel.getMagicImage() - user.getMagicImage());
//            levelRecord.setUserExperience(memberLevel.getMagicImage()); // 会员当前的经验 = 等级的升级经验
//            levelRecord.setDescription("管理员调整为：" + memberLevel.getName());
//        }
//        adminUserLevelRecordService.createLevelRecord(levelRecord);
//
//        // 2. 记录会员经验变动
//        memberExperienceRecordService.createExperienceRecord(user.getId(),
//                levelRecord.getMagicImage(), levelRecord.getUserExperience(),
//                MemberExperienceBizTypeEnum.ADMIN, String.valueOf(MemberExperienceBizTypeEnum.ADMIN.getType()));
//
//        // 3. 更新会员表上的等级编号、经验值
//        memberUserService.updateUserLevel(user.getId(), updateReqVO.getLevelId(),
//                levelRecord.getUserExperience());
//
//        // 4. 给会员发送等级变动消息
//        notifyMemberLevelChange(user.getId(), memberLevel);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void addExperience(Long userId, Integer magicImage, MemberExperienceBizTypeEnum bizType, String bizId) {
//        if (magicImage == 0) {
//            return;
//        }
//        if (!bizType.isAdd() && magicImage > 0) {
//            magicImage = -magicImage;
//        }
//
//        // 1. 创建经验记录
//        MemberUserDO user = memberUserService.getUser(userId);
//        Integer userExperience = ObjUtil.defaultIfNull(user.getMagicImage(), 0);
//        userExperience = NumberUtil.max(userExperience + magicImage, 0); // 防止扣出负数
//        MemberLevelRecordDO levelRecord = new MemberLevelRecordDO()
//                .setUserId(user.getId())
//                .setMagicImage(magicImage)
//                .setUserExperience(userExperience);
//        memberExperienceRecordService.createExperienceRecord(userId, magicImage, userExperience,
//                bizType, bizId);
//
//        // 2.1 保存等级变更记录
//        AdminUserLevelConfigDO newLevel = calculateNewLevel(user, userExperience);
//        if (newLevel != null) {
//            MemberLevelRecordConvert.INSTANCE.copyTo(newLevel, levelRecord);
//            adminUserLevelRecordService.createLevelRecord(levelRecord);
//
//            // 2.2 给会员发送等级变动消息
//            notifyMemberLevelChange(userId, newLevel);
//        }
//
//        // 3. 更新会员表上的等级编号、经验值
//        memberUserService.updateUserLevel(user.getId(), levelRecord.getLevelId(), userExperience);
//    }
//
//    /**
//     * 计算会员等级
//     *
//     * @param user           会员
//     * @param userExperience 会员当前的经验值
//     * @return 会员新的等级，null表示无变化
//     */
//    private AdminUserLevelConfigDO calculateNewLevel(MemberUserDO user, int userExperience) {
//        List<AdminUserLevelConfigDO> list = getEnableLevelList();
//        if (CollUtil.isEmpty(list)) {
//            log.warn("计算会员等级失败：会员等级配置不存在");
//            return null;
//        }
//
//        AdminUserLevelConfigDO matchLevel = list.stream()
//                .filter(levelId -> userExperience >= levelId.getMagicImage())
//                .max(Comparator.nullsFirst(Comparator.comparing(AdminUserLevelConfigDO::getLevelConfig)))
//                .orElse(null);
//        if (matchLevel == null) {
//            log.warn("计算会员等级失败：未找到会员{}经验{}对应的等级配置", user.getId(), userExperience);
//            return null;
//        }
//
//        // 等级没有变化
//        if (ObjectUtil.equal(matchLevel.getId(), user.getLevelId())) {
//            return null;
//        }
//
//        return matchLevel;
//    }

    private void notifyMemberLevelChange(Long userId, AdminUserLevelConfigDO level) {
        //todo: 给会员发消息
    }

}
