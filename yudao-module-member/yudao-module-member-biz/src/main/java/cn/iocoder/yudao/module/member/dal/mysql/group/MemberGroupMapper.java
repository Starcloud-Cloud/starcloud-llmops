package cn.iocoder.yudao.module.member.dal.mysql.group;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.group.vo.MemberGroupPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户分组 Mapper
 *
 * @author owen
 */
@Mapper
public interface MemberGroupMapper extends BaseMapperX<MemberGroupDO> {

    default PageResult<MemberGroupDO> selectPage(MemberGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberGroupDO>()
                .likeIfPresent(MemberGroupDO::getName, reqVO.getName())
                .eqIfPresent(MemberGroupDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MemberGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberGroupDO::getId));
    }

    default List<MemberGroupDO> selectListByStatus(Integer status) {
        return selectList(MemberGroupDO::getStatus, status);
    }

    default MemberGroupDO selectListByName(String name) {
        LambdaQueryWrapper<MemberGroupDO> wrapper = Wrappers.lambdaQuery(MemberGroupDO.class)
                .eq(MemberGroupDO::getName, name);
        return selectOne(wrapper);
    }

    default MemberGroupDO selectByAdminUser(Long adminUserId) {
        LambdaQueryWrapper<MemberGroupDO> wrapper = Wrappers.lambdaQuery(MemberGroupDO.class)
                .eq(MemberGroupDO::getAdminUserId, adminUserId);
        return selectOne(wrapper);
    }
}
