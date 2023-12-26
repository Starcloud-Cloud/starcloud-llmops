package com.starcloud.ops.business.user.service.signin;



import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 签到规则 Service 接口
 *
 * @author QingX
 */
public interface AdminUserSignInConfigService {

    /**
     * 创建签到规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSignInConfig(@Valid AdminUserSignInConfigCreateReqVO createReqVO);

    /**
     * 更新签到规则
     *
     * @param updateReqVO 更新信息
     */
    void updateSignInConfig(@Valid AdminUserSignInConfigUpdateReqVO updateReqVO);

    /**
     * 删除签到规则
     *
     * @param id 编号
     */
    void deleteSignInConfig(Long id);

    /**
     * 获得签到规则
     *
     * @param id 编号
     * @return 签到规则
     */
    AdminUserSignInConfigDO getSignInConfig(Long id);

    /**
     * 获得签到规则列表
     *
     * @return 签到规则分页
     */
    List<AdminUserSignInConfigDO> getSignInConfigList();

    /**
     * 获得签到规则列表
     *
     * @param status 状态
     * @return 签到规则分页
     */
    List<AdminUserSignInConfigDO> getSignInConfigList(Integer status);

}
