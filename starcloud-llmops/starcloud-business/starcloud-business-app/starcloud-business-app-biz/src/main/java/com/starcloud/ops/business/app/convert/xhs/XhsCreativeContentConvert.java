package com.starcloud.ops.business.app.convert.xhs;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExecuteParamsDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExtendDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface XhsCreativeContentConvert {

    XhsCreativeContentConvert INSTANCE = Mappers.getMapper(XhsCreativeContentConvert.class);

    PageResult<XhsCreativeContentResp> convert(PageResult<XhsCreativeContentDO> pageResult);

    XhsCreativeContentResp convert(XhsCreativeContentDO creativeContentDO);

    XhsCreativeContentResp convert(XhsCreativeContentDTO dto);

    List<XhsCreativeContentResp> convertDto(List<XhsCreativeContentDTO> dtoList);

    List<XhsCreativeContentDO> convert(List<XhsCreativeContentCreateReq> createReqs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(XhsCreativeContentModifyReq modifyReq,@MappingTarget XhsCreativeContentDO contentDO);

    default XhsCreativeContentDO convert(XhsCreativeContentCreateReq createReq) {
        if (createReq == null) {
            return null;
        }

        XhsCreativeContentDO xhsCreativeContentDO = new XhsCreativeContentDO();

        xhsCreativeContentDO.setPlanUid(createReq.getPlanUid());
        xhsCreativeContentDO.setTempUid(createReq.getTempUid());
        xhsCreativeContentDO.setUsePicture(toStr(createReq.getUsePicture()));
        xhsCreativeContentDO.setExecuteParams(toStr(createReq.getExecuteParams()));
        xhsCreativeContentDO.setExtend(toStr(createReq.getExtend()));
        xhsCreativeContentDO.setUid(IdUtil.fastSimpleUUID());
        xhsCreativeContentDO.setStatus(XhsCreativeContentStatusEnums.INIT.getCode());

        return xhsCreativeContentDO;
    }

    default String toStr(List<String> list) {
        return JSONUtil.toJsonStr(list);
    }

    default String toStr(XhsCreativeContentExecuteParamsDTO executeParamsDTO) {
        return JSONUtil.toJsonStr(executeParamsDTO);
    }

    default String toStr(XhsCreativeContentExtendDTO extendDTO) {
        return JSONUtil.toJsonStr(extendDTO);
    }

    default List<String> toList(String string) {
        return JSONUtil.parseArray(string).toList(String.class);
    }

    default XhsCreativeContentExecuteParamsDTO toExecuteParams(String string) {
        return JSONUtil.toBean(string, XhsCreativeContentExecuteParamsDTO.class);
    }

    default XhsCreativeContentExtendDTO toExtend(String string) {
        return JSONUtil.toBean(string, XhsCreativeContentExtendDTO.class);
    }
}
