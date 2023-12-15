package com.starcloud.ops.business.user.service.rights;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.rights.vo.AdminUserRightsRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import com.starcloud.ops.business.user.dal.mysql.rights.AdminUserRightsRecordMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;


/**
 * 积分记录 Service 实现类
 *
 * @author QingX
 */
@Slf4j
@Service
@Validated
public class AdminUserRightsRecordServiceImpl implements AdminUserRightsRecordService {

    @Resource
    private AdminUserRightsRecordMapper adminUserRightsRecordMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public PageResult<AdminUserRightsRecordDO> getPointRecordPage(AdminUserRightsRecordPageReqVO pageReqVO) {
        // 根据用户昵称查询出用户 ids
        Set<Long> userIds = null;
        if (StringUtils.isNotBlank(pageReqVO.getNickname())) {
            List<AdminUserDO> users = adminUserService.getUserListByNickname(pageReqVO.getNickname());
            // 如果查询用户结果为空直接返回无需继续查询
            if (CollectionUtils.isEmpty(users)) {
                return PageResult.empty();
            }
            userIds = convertSet(users, AdminUserDO::getId);
        }
        // 执行查询
        return adminUserRightsRecordMapper.selectPage(pageReqVO, userIds);
    }

    @Override
    public PageResult<AdminUserRightsRecordDO> getPointRecordPage(Long userId, PageParam pageVO) {
        return adminUserRightsRecordMapper.selectPage(userId, pageVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPointRecord(Long userId, Integer point, AdminUserRightsBizTypeEnum bizType, String bizId) {
        if (point == 0) {
            return;
        }
//        // 1. 校验用户积分余额
//        AdminUserDO user = adminUserService.getUser(userId);
//        Integer userPoint = ObjectUtil.defaultIfNull(user.getPoint(), 0);
//        int totalPoint = userPoint + point; // 用户变动后的积分
//        if (totalPoint < 0) {
//            throw exception(USER_POINT_NOT_ENOUGH);
//        }
//
//        // 2. 更新用户积分
//        boolean success = adminUserService.updateUserPoint(userId, point);
//        if (!success) {
//            throw exception(USER_POINT_NOT_ENOUGH);
//        }

        // 3. 增加积分记录
        AdminUserRightsRecordDO record = new AdminUserRightsRecordDO()
                .setUserId(userId).setBizId(bizId).setBizType(bizType.getType())
                .setTitle(bizType.getName()).setDescription(StrUtil.format(bizType.getDescription(), point));
//                .setPoint(point).setTotalPoint(totalPoint);
        adminUserRightsRecordMapper.insert(record);
    }

}
