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
                .eq(OpusDirectoryDO::getOpusUid, opusUid);
        return selectList(wrapper);
    }

    default OpusDirectoryDO selectByDirUid(String dirUid) {
        LambdaQueryWrapper<OpusDirectoryDO> wrapper = Wrappers.lambdaQuery(OpusDirectoryDO.class)
                .eq(OpusDirectoryDO::getDirUid, dirUid);
        return selectOne(wrapper);
    }

    default OpusDirectoryDO selectDir(String opusUid, String dirUid) {
        LambdaQueryWrapper<OpusDirectoryDO> wrapper = Wrappers.lambdaQuery(OpusDirectoryDO.class)
                .eq(OpusDirectoryDO::getOpusUid, opusUid)
                .eq(OpusDirectoryDO::getDirUid, dirUid);
        return selectOne(wrapper);
    }

    default OpusDirectoryDO selectParentDir(String opusUid, String parentUid) {
        LambdaQueryWrapper<OpusDirectoryDO> wrapper = Wrappers.lambdaQuery(OpusDirectoryDO.class)
                .eq(OpusDirectoryDO::getDirUid, parentUid)
                .eq(OpusDirectoryDO::getOpusUid, opusUid);
        return selectOne(wrapper);
    }

    default List<OpusDirectoryDO> selectByParentUid(String opusUid, String parentUid) {
        LambdaQueryWrapper<OpusDirectoryDO> wrapper = Wrappers.lambdaQuery(OpusDirectoryDO.class)
                .eq(OpusDirectoryDO::getParentUid, parentUid)
                .eq(OpusDirectoryDO::getOpusUid, opusUid);
        return selectList(wrapper);
    }
}
