package com.starcloud.ops.business.app.convert.xhs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExtendDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativePictureContentDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    void updateSelective(XhsCreativeContentModifyReq modifyReq, @MappingTarget XhsCreativeContentDO contentDO);


    List<XhsCreativePictureContentDTO> convert2(List<XhsImageExecuteResponse> resp);

    default XhsCreativeContentDO convert(XhsCreativeContentCreateReq createReq) {
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

    default String toStr(XhsCreativePictureContentDTO pictureContentDTO) {
        return JSONUtil.toJsonStr(pictureContentDTO);
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

    /**
     * 转换成执行参数
     *
     * @param imageStyleExecuteRequest 图片风格执行参数
     * @param useImageList             使用的图片
     * @param copyWriting              文案
     * @return 执行参数
     */
    default XhsBathImageExecuteRequest toExecuteImageStyle(CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest, List<String> useImageList, CopyWritingContentDTO copyWriting) {
        XhsBathImageExecuteRequest executeRequest = new XhsBathImageExecuteRequest();
        List<XhsImageExecuteRequest> imageExecuteRequests = Lists.newArrayList();

        List<CreativePlanImageExecuteDTO> imageRequests = CollectionUtil.emptyIfNull(imageStyleExecuteRequest.getImageRequests());
        for (CreativePlanImageExecuteDTO imageRequest : imageRequests) {

            Map<String, Object> params = Maps.newHashMap();
            List<VariableItemDTO> variableItemList = CollectionUtil.emptyIfNull(imageRequest.getParams());
            for (VariableItemDTO variableItem : variableItemList) {
                if ("IMAGE".equals(variableItem.getStyle())) {
                    int randomInt = RandomUtil.randomInt(useImageList.size());
                    params.put(variableItem.getField(), useImageList.get(randomInt));
                } else {
                    if (Objects.isNull(variableItem.getValue())) {
                        // 只有主图才会替换标题和副标题
                        if (imageRequest.getIsMain()) {
                            if ("TITLE".equals(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgTitle());
                            } else if ("SUB_TITLE".equals(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgSubTitle());
                            } else {
                                params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
                            }
                        }
                    }
                    params.put(variableItem.getField(), variableItem.getValue());
                }
            }

            XhsImageExecuteRequest request = new XhsImageExecuteRequest();
            request.setImageTemplate(imageRequest.getImageTemplate());
            request.setIndex(imageRequest.getIndex());
            request.setIsMain(imageRequest.getIsMain());
            request.setParams(params);
            imageExecuteRequests.add(request);
        }

        executeRequest.setImageRequests(imageExecuteRequests);
        return executeRequest;
    }


}
