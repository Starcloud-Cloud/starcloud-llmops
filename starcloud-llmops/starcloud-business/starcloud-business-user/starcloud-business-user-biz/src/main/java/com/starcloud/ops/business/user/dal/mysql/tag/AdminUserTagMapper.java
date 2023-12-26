package com.starcloud.ops.business.user.dal.mysql.tag;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员标签 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AdminUserTagMapper extends BaseMapperX<AdminUserTagDO> {

    default Long selectCountByTagId(Long tagId) {
            return selectCount(new LambdaQueryWrapperX<AdminUserTagDO>()
                .apply("FIND_IN_SET({0}, tag_ids)", tagId));
    }

}
