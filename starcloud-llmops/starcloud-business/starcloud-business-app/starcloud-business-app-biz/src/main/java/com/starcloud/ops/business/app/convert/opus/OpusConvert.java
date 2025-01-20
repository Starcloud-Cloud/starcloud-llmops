package com.starcloud.ops.business.app.convert.opus;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.*;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusBindDO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusCreativeBindDTO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OpusConvert {

    OpusConvert INSTANCE = Mappers.getMapper(OpusConvert.class);

    DirectoryNodeVO convert(OpusDirectoryDO directoryDO);

    OpusDirectoryDO convert(OpusDirBaseVO nodeVO);

    OpusDO convert(OpusBaseVO opusBaseVO);

    OpusRespVO convert(OpusDO opusDO);

    PageResult<OpusRespVO> convert(PageResult<OpusDO> page);

    PageResult<OpusBindRespVO> convertBind(PageResult<OpusCreativeBindDTO> pageResult);

    @Mapping(source = "executeResult", target = "executeResult", qualifiedByName = "executeResult")
    OpusBindRespVO convert(OpusCreativeBindDTO bindDTO);

    OpusBindRespVO convert(OpusBindDO addBind);

    OpusBindDO convert(OpusBindBaseVO bindReqVO);

    @Named("executeResult")
    default CreativeContentExecuteResult executeResult(String str) {
        if (!JSONUtil.isTypeJSON(str)) {
            return null;
        }
        return JSONUtil.toBean(str, CreativeContentExecuteResult.class);
    }
}
