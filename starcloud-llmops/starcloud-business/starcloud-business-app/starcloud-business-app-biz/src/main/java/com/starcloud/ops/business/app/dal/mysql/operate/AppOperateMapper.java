package com.starcloud.ops.business.app.dal.mysql.operate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}
