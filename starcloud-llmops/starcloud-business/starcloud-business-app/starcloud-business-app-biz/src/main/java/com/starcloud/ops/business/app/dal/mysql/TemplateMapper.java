package com.starcloud.ops.business.app.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.app.dal.databoject.TemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模版表 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Mapper
public interface TemplateMapper extends BaseMapperX<TemplateDO> {

}
