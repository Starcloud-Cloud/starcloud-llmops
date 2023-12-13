package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import com.starcloud.ops.business.user.dal.mysql.level.AdminUserLevelRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 会员等级记录 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserLevelRecordServiceImpl implements AdminUserLevelRecordService {

    @Resource
    private AdminUserLevelRecordMapper adminUserLevelRecordMapper;

    @Override
    public AdminUserLevelRecordDO getLevelRecord(Long id) {
        return adminUserLevelRecordMapper.selectById(id);
    }

    @Override
    public PageResult<AdminUserLevelRecordDO> getLevelRecordPage(AdminUserLevelRecordPageReqVO pageReqVO) {
        return adminUserLevelRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLevelRecord(AdminUserLevelRecordDO levelRecord) {
        adminUserLevelRecordMapper.insert(levelRecord);
    }

}
