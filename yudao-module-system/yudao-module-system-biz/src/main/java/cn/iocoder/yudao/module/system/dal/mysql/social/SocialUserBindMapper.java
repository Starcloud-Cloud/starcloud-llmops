package cn.iocoder.yudao.module.system.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SocialUserBindMapper extends BaseMapperX<SocialUserBindDO> {

    default void deleteByUserTypeAndUserIdAndSocialType(Integer userType, Long userId, Integer socialType) {
        delete(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserType, userType)
                .eq(SocialUserBindDO::getUserId, userId)
                .eq(SocialUserBindDO::getSocialType, socialType));
    }

    @Select(
            "<script> "
            + " select * from system_social_user_bind where social_user_id = #{id} AND user_type = #{type} order by id desc limit 1"
            + "</script>"
    )
    SocialUserBindDO selectDeleteDO(@Param("id") Long id, @Param("type")Integer type);

    default void deleteByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        delete(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserType, userType)
                .eq(SocialUserBindDO::getSocialUserId, socialUserId));
    }

    default SocialUserBindDO selectByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return selectOne(SocialUserBindDO::getUserType, userType,
                SocialUserBindDO::getSocialUserId, socialUserId);
    }

    default List<SocialUserBindDO> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserId, userId)
                .eq(SocialUserBindDO::getUserType, userType));
    }

}
