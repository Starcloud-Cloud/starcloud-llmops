package com.starcloud.ops.business.user.service.level;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;

import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelListReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelUpdateReqVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
public class AdminUserLevelServiceImpl implements AdminUserLevelService {

    @Resource
    private AdminUserLevelMapper adminUserLevelMapper;

    @Resource
    private AdminUserLevelRecordService adminUserLevelRecordService;
    @Resource
    private AdminUserService adminUserService;

    @Override
    public Long createLevel(AdminUserLevelCreateReqVO createReqVO) {
        // 校验配置是否有效
        validateConfigValid(null, createReqVO.getName(), createReqVO.getLevel(), createReqVO.getExperience());

        // 插入
        AdminUserLevelDO level = AdminUserLevelConvert.INSTANCE.convert(createReqVO);
        adminUserLevelMapper.insert(level);
        // 返回
        return level.getId();
    }

    @Override
    public void updateLevel(AdminUserLevelUpdateReqVO updateReqVO) {
        // 校验存在
        validateLevelExists(updateReqVO.getId());
        // 校验配置是否有效
        validateConfigValid(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getLevel(), updateReqVO.getExperience());

        // 更新
        AdminUserLevelDO updateObj = AdminUserLevelConvert.INSTANCE.convert(updateReqVO);
        adminUserLevelMapper.updateById(updateObj);
    }

    @Override
    public void deleteLevel(Long id) {
        // 校验存在
        validateLevelExists(id);
        // 校验分组下是否有用户
        validateLevelHasUser(id);
        // 删除
        adminUserLevelMapper.deleteById(id);
    }

    @VisibleForTesting
    AdminUserLevelDO validateLevelExists(Long id) {
        AdminUserLevelDO levelDO = adminUserLevelMapper.selectById(id);
        if (levelDO == null) {
            throw exception(LEVEL_NOT_EXISTS);
        }
        return levelDO;
    }

    @VisibleForTesting
    void validateNameUnique(List<AdminUserLevelDO> list, Long id, String name) {
        for (AdminUserLevelDO levelDO : list) {
            if (ObjUtil.notEqual(levelDO.getName(), name)) {
                continue;
            }
            if (id == null || !id.equals(levelDO.getId())) {
                throw exception(LEVEL_NAME_EXISTS, levelDO.getName());
            }
        }
    }

    @VisibleForTesting
    void validateLevelUnique(List<AdminUserLevelDO> list, Long id, Integer level) {
        for (AdminUserLevelDO levelDO : list) {
            if (ObjUtil.notEqual(levelDO.getLevel(), level)) {
                continue;
            }

            if (id == null || !id.equals(levelDO.getId())) {
                throw exception(LEVEL_VALUE_EXISTS, levelDO.getLevel(), levelDO.getName());
            }
        }
    }

    @VisibleForTesting
    void validateExperienceOutRange(List<AdminUserLevelDO> list, Long id, Integer level, Integer experience) {
//        for (AdminUserLevelDO levelDO : list) {
//            if (levelDO.getId().equals(id)) {
//                continue;
//            }
//
//            if (levelDO.getLevel() < level) {
//                // 经验大于前一个等级
//                if (experience <= levelDO.getExperience()) {
//                    throw exception(LEVEL_EXPERIENCE_MIN, levelDO.getName(), levelDO.getExperience());
//                }
//            } else if (levelDO.getLevel() > level) {
//                //小于下一个级别
//                if (experience >= levelDO.getExperience()) {
//                    throw exception(LEVEL_EXPERIENCE_MAX, levelDO.getName(), levelDO.getExperience());
//                }
//            }
//        }
    }

    @VisibleForTesting
    void validateConfigValid(Long id, String name, Integer level, Integer experience) {
        List<AdminUserLevelDO> list = adminUserLevelMapper.selectList();
        // 校验名称唯一
        validateNameUnique(list, id, name);
        // 校验等级唯一
        validateLevelUnique(list, id, level);
        // 校验升级所需经验是否有效: 大于前一个等级，小于下一个级别
        validateExperienceOutRange(list, id, level, experience);
    }

    @VisibleForTesting
    void validateLevelHasUser(Long id) {
//        Long count = memberUserService.getUserCountByLevelId(id);
//        if (count > 0) {
//            throw exception(LEVEL_HAS_USER);
//        }
    }

    @Override
    public AdminUserLevelDO getLevel(Long id) {
        return id != null && id > 0 ? adminUserLevelMapper.selectById(id) : null;
    }

    @Override
    public List<AdminUserLevelDO> getLevelList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return adminUserLevelMapper.selectBatchIds(ids);
    }

    @Override
    public List<AdminUserLevelDO> getLevelList(AdminUserLevelListReqVO listReqVO) {
        return adminUserLevelMapper.selectList(listReqVO);
    }

    @Override
    public List<AdminUserLevelDO> getLevelListByStatus(Integer status) {
        return adminUserLevelMapper.selectListByStatus(status);
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
//        AdminUserLevelDO memberLevel = null;
//        if (updateReqVO.getLevelId() == null) {
//            // 取消用户等级时，需要扣减经验
//            levelRecord.setExperience(-user.getExperience());
//            levelRecord.setUserExperience(0);
//            levelRecord.setDescription("管理员取消了等级");
//        } else {
//            // 复制等级配置
//            memberLevel = validateLevelExists(updateReqVO.getLevelId());
//            MemberLevelRecordConvert.INSTANCE.copyTo(memberLevel, levelRecord);
//            // 变动经验值 = 等级的升级经验 - 会员当前的经验；正数为增加经验，负数为扣减经验
//            levelRecord.setExperience(memberLevel.getExperience() - user.getExperience());
//            levelRecord.setUserExperience(memberLevel.getExperience()); // 会员当前的经验 = 等级的升级经验
//            levelRecord.setDescription("管理员调整为：" + memberLevel.getName());
//        }
//        adminUserLevelRecordService.createLevelRecord(levelRecord);
//
//        // 2. 记录会员经验变动
//        memberExperienceRecordService.createExperienceRecord(user.getId(),
//                levelRecord.getExperience(), levelRecord.getUserExperience(),
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
//    public void addExperience(Long userId, Integer experience, MemberExperienceBizTypeEnum bizType, String bizId) {
//        if (experience == 0) {
//            return;
//        }
//        if (!bizType.isAdd() && experience > 0) {
//            experience = -experience;
//        }
//
//        // 1. 创建经验记录
//        MemberUserDO user = memberUserService.getUser(userId);
//        Integer userExperience = ObjUtil.defaultIfNull(user.getExperience(), 0);
//        userExperience = NumberUtil.max(userExperience + experience, 0); // 防止扣出负数
//        MemberLevelRecordDO levelRecord = new MemberLevelRecordDO()
//                .setUserId(user.getId())
//                .setExperience(experience)
//                .setUserExperience(userExperience);
//        memberExperienceRecordService.createExperienceRecord(userId, experience, userExperience,
//                bizType, bizId);
//
//        // 2.1 保存等级变更记录
//        AdminUserLevelDO newLevel = calculateNewLevel(user, userExperience);
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
//    private AdminUserLevelDO calculateNewLevel(MemberUserDO user, int userExperience) {
//        List<AdminUserLevelDO> list = getEnableLevelList();
//        if (CollUtil.isEmpty(list)) {
//            log.warn("计算会员等级失败：会员等级配置不存在");
//            return null;
//        }
//
//        AdminUserLevelDO matchLevel = list.stream()
//                .filter(level -> userExperience >= level.getExperience())
//                .max(Comparator.nullsFirst(Comparator.comparing(AdminUserLevelDO::getLevel)))
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

    private void notifyMemberLevelChange(Long userId, AdminUserLevelDO level) {
        //todo: 给会员发消息
    }

}