package com.starcloud.ops.business.app.dal.mysql.xhs;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsNoteDetailDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XhsNoteDetailMapper extends BaseMapperX<XhsNoteDetailDO> {


    default XhsNoteDetailDO selectByNoteId(String noteId) {
        LambdaQueryWrapper<XhsNoteDetailDO> wrapper = Wrappers.lambdaQuery(XhsNoteDetailDO.class)
                .eq(XhsNoteDetailDO::getNoteId, noteId);
        return selectOne(wrapper);
    }
}
