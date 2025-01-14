package com.starcloud.ops.business.app.convert.opus;

import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OpusConvert {

    OpusConvert INSTANCE = Mappers.getMapper(OpusConvert.class);

    DirectoryNodeVO convert(OpusDirectoryDO directoryDO);
}
