package com.starcloud.ops.business.app.dal.mysql.opus;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OpusMapper extends BaseMapperX<OpusDO> {

    default OpusDO selectByOpusUid(String opusUid) {
        LambdaQueryWrapper<OpusDO> wrapper = Wrappers.lambdaQuery(OpusDO.class)
                .eq(OpusDO::getOpusUid, opusUid);
        return selectOne(wrapper);
    }

    default PageResult<OpusDO> page(PageParam pageParam) {
        LambdaQueryWrapper<OpusDO> wrapper = Wrappers.lambdaQuery(OpusDO.class)
                .orderByDesc(OpusDO::getUpdateTime);
        return selectPage(pageParam, wrapper);
    }
}
