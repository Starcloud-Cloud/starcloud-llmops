package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;

import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelListReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 会员等级 Service 接口
 *
 * @author owen
 */
public interface AdminUserLevelService {

    /**
     * 创建会员等级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLevel(@Valid AdminUserLevelCreateReqVO createReqVO);

    /**
     * 更新会员等级
     *
     * @param updateReqVO 更新信息
     */
    void updateLevel(@Valid AdminUserLevelUpdateReqVO updateReqVO);

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
    AdminUserLevelDO getLevel(Long id);

    /**
     * 获得会员等级列表
     *
     * @param ids 编号
     * @return 会员等级列表
     */
    List<AdminUserLevelDO> getLevelList(Collection<Long> ids);

    /**
     * 获得会员等级列表
     *
     * @param listReqVO 查询参数
     * @return 会员等级列表
     */
    List<AdminUserLevelDO> getLevelList(AdminUserLevelListReqVO listReqVO);

    /**
     * 获得指定状态的会员等级列表
     *
     * @param status 状态
     * @return 会员等级列表
     */
    List<AdminUserLevelDO> getLevelListByStatus(Integer status);

    /**
     * 获得开启状态的会员等级列表
     *
     * @return 会员等级列表
     */
    default List<AdminUserLevelDO> getEnableLevelList() {
        return getLevelListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

//    /**
//     * 修改会员的等级
//     *
//     * @param updateReqVO 修改参数
//     */
//    void updateUserLevel(MemberUserUpdateLevelReqVO updateReqVO);


}
