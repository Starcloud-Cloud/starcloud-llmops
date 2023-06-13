package com.starcloud.ops.business.app.dal.mysql.app;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用表 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Mapper
public interface AppMapper extends BaseMapperX<AppDO> {

}
