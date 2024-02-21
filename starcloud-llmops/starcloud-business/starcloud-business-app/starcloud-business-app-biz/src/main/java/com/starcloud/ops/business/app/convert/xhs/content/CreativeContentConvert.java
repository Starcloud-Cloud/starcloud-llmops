package com.starcloud.ops.business.app.convert.xhs.content;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageDTO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExtendDTO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDTO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
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

    PageResult<CreativeContentRespVO> convert(PageResult<CreativeContentDO> pageResult);

    CreativeContentRespVO convert(CreativeContentDO creativeContentDO);

    CreativeContentRespVO convert(CreativeContentDTO dto);

    List<CreativeContentRespVO> convertDto(List<CreativeContentDTO> dtoList);

    List<CreativeContentDO> convert(List<CreativeContentCreateReqVO> createReqs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateSelective(CreativeContentModifyReqVO modifyReq, @MappingTarget CreativeContentDO contentDO);


    List<CreativeImageDTO> convert2(List<XhsImageExecuteResponse> resp);

    default CreativeContentDO convert(CreativeContentCreateReqVO createReq) {
        if (createReq == null) {
            return null;
        }

        CreativeContentDO xhsCreativeContentDO = new CreativeContentDO();
        xhsCreativeContentDO.setSchemeUid(createReq.getSchemeUid());
        xhsCreativeContentDO.setPlanUid(createReq.getPlanUid());
        xhsCreativeContentDO.setTempUid(createReq.getTempUid());
        xhsCreativeContentDO.setUsePicture(toStr(createReq.getUsePicture()));
        xhsCreativeContentDO.setExecuteParams(toStr(createReq.getExecuteParams()));
        xhsCreativeContentDO.setExtend(toStr(createReq.getExtend()));
        xhsCreativeContentDO.setUid(IdUtil.fastSimpleUUID());
        xhsCreativeContentDO.setStatus(CreativeContentStatusEnum.INIT.getCode());
        xhsCreativeContentDO.setBusinessUid(createReq.getBusinessUid());
        xhsCreativeContentDO.setConversationUid(createReq.getConversationUid());
        xhsCreativeContentDO.setType(createReq.getType());
        xhsCreativeContentDO.setIsTest(createReq.getIsTest());
        xhsCreativeContentDO.setTags(createReq.getTags());
        xhsCreativeContentDO.setBatch(createReq.getBatch());
        return xhsCreativeContentDO;
    }

    default String toStr(List<String> list) {
        return JSONUtil.toJsonStr(list);
    }

    default String imageToStr(List<CreativeImageDTO> images) {
        return JSONUtil.toJsonStr(images);
    }

    default String toStr(CreativePlanExecuteDTO executeParamsDTO) {
        return JSONUtil.toJsonStr(executeParamsDTO);
    }

    default String toStr(CreativeContentExtendDTO extendDTO) {
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

    default CreativeContentExtendDTO toExtend(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.toBean(string, CreativeContentExtendDTO.class);
    }

    default List<CreativeImageDTO> toContent(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return JSONUtil.parseArray(string).toList(CreativeImageDTO.class);
    }

}
