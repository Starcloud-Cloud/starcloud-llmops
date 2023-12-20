package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelPageReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.NotifyExpiringLevelRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.NotifyExpiringRightsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;

import java.util.List;

/**
 * 会员等级记录 Service 接口
 *
 * @author owen
 */
public interface AdminUserLevelService {

    /**
     * 获得会员等级记录明细
     *
     * @param id 编号
     * @return 会员等级记录
     */
    AdminUserLevelDO getLevel(Long id);

    /**
     * 获得会员等级记录分页
     *
     * @param pageReqVO 分页查询
     * @return 会员等级记录分页
     */
    PageResult<AdminUserLevelDO> getLevelPage(AdminUserLevelPageReqVO pageReqVO);

    /**
     * 创建会员等级记录
     *
     * @param levelRecord 会员等级记录
     */
    void createLevelRecord(AdminUserLevelCreateReqVO levelRecord);

    /**
     * 创建会员默认等级记录
     *
     * @param userId 用户 ID
     */
    void createInitLevelRecord(Long userId);

    /**
     * 获取会员下有效的等级列表
     * @param userId 用户 ID
     */
    List<AdminUserLevelDO> getLevelList(Long userId);

    /**
     * 等级过期提醒
     * @param userId 用户 ID
     */
    NotifyExpiringLevelRespVO notifyExpiringLevel(Long userId);
}
