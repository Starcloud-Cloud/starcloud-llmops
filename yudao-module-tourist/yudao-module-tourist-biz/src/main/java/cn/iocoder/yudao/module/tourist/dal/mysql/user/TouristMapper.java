package cn.iocoder.yudao.module.tourist.dal.mysql.user;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员 User Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TouristMapper extends BaseMapperX<TouristDO> {

    default TouristDO selectByMobile(String mobile) {
        return selectOne(TouristDO::getMobile, mobile);
    }

    default List<TouristDO> selectListByNicknameLike(String nickname) {
        return selectList(new LambdaQueryWrapperX<TouristDO>()
                .likeIfPresent(TouristDO::getNickname, nickname));
    }

}
