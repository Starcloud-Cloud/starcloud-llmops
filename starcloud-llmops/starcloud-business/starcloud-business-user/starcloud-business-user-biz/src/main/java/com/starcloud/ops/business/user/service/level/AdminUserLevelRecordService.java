package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;

/**
 * 会员等级记录 Service 接口
 *
 * @author owen
 */
public interface AdminUserLevelRecordService {

    /**
     * 获得会员等级记录
     *
     * @param id 编号
     * @return 会员等级记录
     */
    AdminUserLevelRecordDO getLevelRecord(Long id);

    /**
     * 获得会员等级记录分页
     *
     * @param pageReqVO 分页查询
     * @return 会员等级记录分页
     */
    PageResult<AdminUserLevelRecordDO> getLevelRecordPage(AdminUserLevelRecordPageReqVO pageReqVO);

    /**
     * 创建会员等级记录
     *
     * @param levelRecord 会员等级记录
     */
    void createLevelRecord(AdminUserLevelRecordDO levelRecord);

}
