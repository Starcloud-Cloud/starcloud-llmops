package com.starcloud.ops.business.app.dal.mysql.opus;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OpusDirectoryMapper extends BaseMapperX<OpusDirectoryDO> {

    default List<OpusDirectoryDO> selectByOpusUid(String opusUid) {
        LambdaQueryWrapper<OpusDirectoryDO> wrapper = Wrappers.lambdaQuery(OpusDirectoryDO.class)
                .eq(OpusDirectoryDO::getOpusUid, opusUid)
                ;
        return selectList(wrapper);
    }
}
