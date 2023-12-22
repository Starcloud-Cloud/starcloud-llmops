package com.starcloud.ops.business.user.dal.mysql.tag;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员标签 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AdminUserTagConfigMapper extends BaseMapperX<AdminUserTagConfigDO> {

    default PageResult<AdminUserTagConfigDO> selectPage(AdminUserTagConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserTagConfigDO>()
                .likeIfPresent(AdminUserTagConfigDO::getName, reqVO.getName())
                .betweenIfPresent(AdminUserTagConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdminUserTagConfigDO::getId));
    }

    default AdminUserTagConfigDO selectByName(String name) {
        return selectOne(AdminUserTagConfigDO::getName, name);
    }
    default AdminUserTagConfigDO selectNewUserTag() {
        LambdaQueryWrapper<AdminUserTagConfigDO> wrapper = Wrappers.lambdaQuery();
        wrapper.like(AdminUserTagConfigDO::getName, "新用户");
        return selectOne(wrapper);
    }
}
