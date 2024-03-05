package com.starcloud.ops.business.app.convert.xhs.scheme;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Mapper
public interface CreativeSchemeConvert {


    CreativeSchemeConvert INSTANCE = Mappers.getMapper(CreativeSchemeConvert.class);

    /**
     * 转换为 CreativeSchemeDO
     *
     * @param request 创作方案请求
     * @return CreativeSchemeDO
     */
    default CreativeSchemeDO convertCreateRequest(CreativeSchemeReqVO request) {
        CreativeSchemeDO creativeScheme = new CreativeSchemeDO();
        creativeScheme.setUid(IdUtil.fastSimpleUUID());
        creativeScheme.setName(request.getName());
        creativeScheme.setType(request.getType());
        creativeScheme.setCategory(request.getCategory());
        creativeScheme.setTags(StringUtil.toString(request.getTags()));
        creativeScheme.setDescription(request.getDescription());
        creativeScheme.setMode(request.getMode());
        // 创作计划配置信息
        if (request.getConfiguration() != null) {
            creativeScheme.setConfiguration(JsonUtils.toJsonString(request.getConfiguration()));
        }
        // 创作计划使用图片
        if (CollectionUtil.isNotEmpty(request.getUseImages())) {
            creativeScheme.setUseImages(JsonUtils.toJsonString(request.getUseImages()));
        }
        // 创作计划示例
        if (CollectionUtil.isNotEmpty(request.getExample())) {
            creativeScheme.setExample(JsonUtils.toJsonString(request.getExample()));
        }
        // 创作计划物料
        creativeScheme.setMateriel(request.getMateriel());
        creativeScheme.setCreateTime(LocalDateTime.now());
        creativeScheme.setUpdateTime(LocalDateTime.now());
        creativeScheme.setDeleted(Boolean.FALSE);
        return creativeScheme;
    }

    /**
     * 转换为 CreativeSchemeDO
     *
     * @param request 创作方案请求
     * @return CreativeSchemeDO
     */
    default CreativeSchemeDO convertModifyRequest(CreativeSchemeModifyReqVO request) {
        CreativeSchemeDO creativeScheme = this.convertCreateRequest(request);
        creativeScheme.setUid(request.getUid());
        return creativeScheme;
    }

    /**
     * 将CreativeSchemeDO对象转换为CreativeSchemeRespVO对象
     *
     * @param creativeScheme 待转换的CreativeSchemeDO对象
     * @return 转换后的CreativeSchemeRespVO对象
     */
    default CreativeSchemeRespVO convertResponse(CreativeSchemeDO creativeScheme) {
        CreativeSchemeRespVO creativeSchemeResponse = new CreativeSchemeRespVO();

        creativeSchemeResponse.setUid(creativeScheme.getUid());
        creativeSchemeResponse.setName(creativeScheme.getName());
        creativeSchemeResponse.setType(creativeScheme.getType());
        creativeSchemeResponse.setCategory(creativeScheme.getCategory());
        creativeSchemeResponse.setTags(StringUtil.toList(creativeScheme.getTags()));
        creativeSchemeResponse.setDescription(creativeScheme.getDescription());
        creativeSchemeResponse.setMode(creativeScheme.getMode());

        // 创作计划配置信息
        if (StringUtils.isNotBlank(creativeScheme.getConfiguration())) {
            creativeSchemeResponse.setConfiguration(JsonUtils.parseObject(creativeScheme.getConfiguration(), CreativeSchemeConfigurationDTO.class));
        }

        // 创作计划使用图片
        if (StringUtils.isNotBlank(creativeScheme.getUseImages())) {
            creativeSchemeResponse.setUseImages(JSONUtil.toList(creativeScheme.getUseImages(), String.class));
        }

        // 创作计划示例
        if (StringUtils.isNotBlank(creativeScheme.getExample())) {
            creativeSchemeResponse.setExample(JSONUtil.toList(creativeScheme.getExample(), String.class));
        }

        // 创作计划物料
        creativeSchemeResponse.setMateriel(creativeScheme.getMateriel());

        creativeSchemeResponse.setCreator(creativeScheme.getCreator());
        creativeSchemeResponse.setUpdater(creativeScheme.getUpdater());
        creativeSchemeResponse.setCreateTime(creativeScheme.getCreateTime());
        creativeSchemeResponse.setUpdateTime(creativeScheme.getUpdateTime());
        return creativeSchemeResponse;
    }

    /**
     * 转换为 CreativeSchemeDO 列表
     *
     * @param list 创作方案列表
     * @return CreativeSchemeDO 列表
     */
    default List<CreativeSchemeRespVO> convertList(List<CreativeSchemeDO> list) {
        return CollectionUtil.emptyIfNull(list).stream().map(this::convertResponse).collect(Collectors.toList());
    }

}
