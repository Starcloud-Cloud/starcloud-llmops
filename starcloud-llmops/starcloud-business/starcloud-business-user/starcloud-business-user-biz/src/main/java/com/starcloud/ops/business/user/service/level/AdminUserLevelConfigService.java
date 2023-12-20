package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;

import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigListReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 会员等级 Service 接口
 *
 * @author owen
 */
public interface AdminUserLevelConfigService {

    /**
     * 创建会员等级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLevel(@Valid AdminUserLevelConfigCreateReqVO createReqVO);

    /**
     * 更新会员等级
     *
     * @param updateReqVO 更新信息
     */
    void updateLevel(@Valid AdminUserLevelConfigUpdateReqVO updateReqVO);

    /**
     * 删除会员等级
     *
     * @param id 编号
     */
    void deleteLevel(Long id);

    /**
     * 获得会员等级
     *
     * @param id 编号
     * @return 会员等级
     */
    AdminUserLevelConfigDO getLevelConfig(Long id);

    /**
     * 获得会员等级列表
     *
     * @param ids 编号
     * @return 会员等级列表
     */
    List<AdminUserLevelConfigDO> getLevelList(Collection<Long> ids);

    /**
     * 获得会员等级列表
     *
     * @param listReqVO 查询参数
     * @return 会员等级列表
     */
    List<AdminUserLevelConfigDO> getLevelList(AdminUserLevelConfigListReqVO listReqVO);

    /**
     * 获得指定状态的会员等级列表
     *
     * @param status 状态
     * @return 会员等级列表
     */
    List<AdminUserLevelConfigDO> getLevelListByStatus(Integer status);

    /**
     * 获得指定状态的会员等级列表
     *
     * @param roleId 状态
     * @return 会员等级列表
     */
    AdminUserLevelConfigDO getLevelByRoleId(Long roleId);

    /**
     * 获得开启状态的会员等级列表
     *
     * @return 会员等级列表
     */
    default List<AdminUserLevelConfigDO> getEnableLevelList() {
        return getLevelListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

//    /**
//     * 修改会员的等级
//     *
//     * @param updateReqVO 修改参数
//     */
//    void updateUserLevel(MemberUserUpdateLevelReqVO updateReqVO);


}
