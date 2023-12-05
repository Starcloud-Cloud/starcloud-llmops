package com.starcloud.ops.business.app.convert.xhs.content;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.content.dto.XhsCreativeContentExtendDTO;
import com.starcloud.ops.business.app.api.xhs.content.dto.XhsCreativePictureContentDTO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentStatusEnums;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CreativeContentConvert {

    CreativeContentConvert INSTANCE = Mappers.getMapper(CreativeContentConvert.class);

    PageResult<CreativeContentRespVO> convert(PageResult<XhsCreativeContentDO> pageResult);

    CreativeContentRespVO convert(XhsCreativeContentDO creativeContentDO);

    CreativeContentRespVO convert(XhsCreativeContentDTO dto);

    List<CreativeContentRespVO> convertDto(List<XhsCreativeContentDTO> dtoList);

    List<XhsCreativeContentDO> convert(List<CreativeContentCreateReqVO> createReqs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(CreativeContentModifyReqVO modifyReq, @MappingTarget XhsCreativeContentDO contentDO);


    List<XhsCreativePictureContentDTO> convert2(List<XhsImageExecuteResponse> resp);

    default XhsCreativeContentDO convert(CreativeContentCreateReqVO createReq) {
        if (createReq == null) {
            return null;
        }

        XhsCreativeContentDO xhsCreativeContentDO = new XhsCreativeContentDO();
        xhsCreativeContentDO.setSchemeUid(createReq.getSchemeUid());
        xhsCreativeContentDO.setPlanUid(createReq.getPlanUid());
        xhsCreativeContentDO.setTempUid(createReq.getTempUid());
        xhsCreativeContentDO.setUsePicture(toStr(createReq.getUsePicture()));
        xhsCreativeContentDO.setExecuteParams(toStr(createReq.getExecuteParams()));
        xhsCreativeContentDO.setExtend(toStr(createReq.getExtend()));
        xhsCreativeContentDO.setUid(IdUtil.fastSimpleUUID());
        xhsCreativeContentDO.setStatus(XhsCreativeContentStatusEnums.INIT.getCode());
        xhsCreativeContentDO.setBusinessUid(createReq.getBusinessUid());
        xhsCreativeContentDO.setType(createReq.getType());

        return xhsCreativeContentDO;
    }

    default String toStr(List<String> list) {
        return JSONUtil.toJsonStr(list);
    }

    default String toStr(CreativePlanExecuteDTO executeParamsDTO) {
        return JSONUtil.toJsonStr(executeParamsDTO);
    }

    default String toStr(XhsCreativeContentExtendDTO extendDTO) {
        return JSONUtil.toJsonStr(extendDTO);
    }

    default List<String> toList(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.parseArray(string).toList(String.class);
    }

    default CreativePlanExecuteDTO toExecuteParams(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.toBean(string, CreativePlanExecuteDTO.class);
    }

    default XhsCreativeContentExtendDTO toExtend(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.toBean(string, XhsCreativeContentExtendDTO.class);
    }

    default List<XhsCreativePictureContentDTO> toContent(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.parseArray(string).toList(XhsCreativePictureContentDTO.class);
    }

}
