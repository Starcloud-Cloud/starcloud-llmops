package com.starcloud.ops.business.app.dal.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.dal.databoject.AppOperateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模版操作关联 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Mapper
public interface AppOperateMapper extends BaseMapper<AppOperateDO> {

}
