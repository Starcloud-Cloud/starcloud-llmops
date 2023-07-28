package com.starcloud.ops.business.app.dal.mysql.operate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.convert.operate.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用操作关联 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Mapper
public interface AppOperateMapper extends BaseMapper<AppOperateDO> {

    /**
     * 创建应用操作关联
     *
     * @param marketUid   应用 uid
     * @param version     版本号
     * @param operateType 操作类型
     * @param userId      用户id
     * @return 应用操作关联
     */
    default AppOperateDO create(String marketUid, Integer version, String operateType, String userId) {
        AppOperateDO appOperateDO = AppOperateConvert.INSTANCE.convert(marketUid, version, operateType, userId);
        this.insert(appOperateDO);
        return appOperateDO;
    }
}
