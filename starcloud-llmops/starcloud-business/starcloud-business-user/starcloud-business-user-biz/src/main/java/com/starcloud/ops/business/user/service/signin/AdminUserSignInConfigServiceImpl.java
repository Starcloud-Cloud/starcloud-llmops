package com.starcloud.ops.business.user.service.signin;


import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigUpdateReqVO;
import com.starcloud.ops.business.user.convert.signin.AdminUserSignInConfigConvert;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import com.starcloud.ops.business.user.dal.mysql.signin.AdminUserSignInConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.SIGN_IN_CONFIG_EXISTS;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.SIGN_IN_CONFIG_NOT_EXISTS;


/**
 * 签到规则 Service 实现类
 *
 * @author QingX
 */
@Service
@Validated
public class AdminUserSignInConfigServiceImpl implements AdminUserSignInConfigService {

    @Resource
    private AdminUserSignInConfigMapper adminUserSignInConfigMapper;

    @Override
    public Long createSignInConfig(AdminUserSignInConfigCreateReqVO createReqVO) {
        // 判断是否重复插入签到天数
        validateSignInConfigDayDuplicate(createReqVO.getDay(), null);

        // 插入
        AdminUserSignInConfigDO signInConfig = AdminUserSignInConfigConvert.INSTANCE.convert(createReqVO);
        adminUserSignInConfigMapper.insert(signInConfig);
        // 返回
        return signInConfig.getId();
    }

    @Override
    public void updateSignInConfig(AdminUserSignInConfigUpdateReqVO updateReqVO) {
        // 校验存在
        validateSignInConfigExists(updateReqVO.getId());
        // 判断是否重复插入签到天数
        validateSignInConfigDayDuplicate(updateReqVO.getDay(), updateReqVO.getId());

        // 判断更新
        AdminUserSignInConfigDO updateObj = AdminUserSignInConfigConvert.INSTANCE.convert(updateReqVO);
        adminUserSignInConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteSignInConfig(Long id) {
        // 校验存在
        validateSignInConfigExists(id);
        // 删除
        adminUserSignInConfigMapper.deleteById(id);
    }

    private void validateSignInConfigExists(Long id) {
        if (adminUserSignInConfigMapper.selectById(id) == null) {
            throw exception(SIGN_IN_CONFIG_NOT_EXISTS);
        }
    }

    /**
     * 校验 day 是否重复
     *
     * @param day 天
     * @param id  编号，只有更新的时候会传递
     */
    private void validateSignInConfigDayDuplicate(Integer day, Long id) {
        AdminUserSignInConfigDO config = adminUserSignInConfigMapper.selectByDay(day);
        // 1. 新增时，config 非空，则说明重复
        if (id == null && config != null) {
            throw exception(SIGN_IN_CONFIG_EXISTS);
        }
        // 2. 更新时，如果 config 非空，且 id 不相等，则说明重复
        if (id != null && config != null && !config.getId().equals(id)) {
            throw exception(SIGN_IN_CONFIG_EXISTS);
        }
    }

    @Override
    public AdminUserSignInConfigDO getSignInConfig(Long id) {
        return adminUserSignInConfigMapper.selectById(id);
    }

    @Override
    public List<AdminUserSignInConfigDO> getSignInConfigList() {
        List<AdminUserSignInConfigDO> list = adminUserSignInConfigMapper.selectList();
        list.sort(Comparator.comparing(AdminUserSignInConfigDO::getDay));
        return list;
    }

    @Override
    public List<AdminUserSignInConfigDO> getSignInConfigList(Integer status) {
        List<AdminUserSignInConfigDO> list = adminUserSignInConfigMapper.selectListByStatus(status);
        list.sort(Comparator.comparing(AdminUserSignInConfigDO::getDay));
        return list;
    }

}
