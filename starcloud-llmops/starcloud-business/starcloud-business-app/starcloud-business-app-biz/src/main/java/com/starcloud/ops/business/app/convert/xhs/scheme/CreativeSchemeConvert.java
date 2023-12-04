package com.starcloud.ops.business.app.convert.xhs.scheme;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ImageExampleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        if (CollectionUtil.isNotEmpty(request.getRefers())) {
            creativeScheme.setRefers(JSONUtil.toJsonStr(request.getRefers()));
        }
        if (request.getConfiguration() != null) {
            creativeScheme.setConfiguration(JSONUtil.toJsonStr(request.getConfiguration()));
        }
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
        if (StringUtils.isNotBlank(creativeScheme.getRefers())) {
            TypeReference<List<CreativeSchemeReferenceDTO>> typeReference = new TypeReference<List<CreativeSchemeReferenceDTO>>() {
            };
            creativeSchemeResponse.setRefers(JSONUtil.toBean(creativeScheme.getRefers(), typeReference, Boolean.TRUE));
        }
        if (StringUtils.isNotBlank(creativeScheme.getConfiguration())) {
            creativeSchemeResponse.setConfiguration(JSONUtil.toBean(creativeScheme.getConfiguration(), CreativeSchemeConfigDTO.class));
        }
        if (StringUtils.isNotBlank(creativeScheme.getCopyWritingExample())) {
            TypeReference<List<CopyWritingContentDTO>> typeReference = new TypeReference<List<CopyWritingContentDTO>>() {
            };
            creativeSchemeResponse.setCopyWritingExample(JSONUtil.toBean(creativeScheme.getCopyWritingExample(), typeReference, Boolean.TRUE));
        }
        if (StringUtils.isNotBlank(creativeScheme.getImageExample())) {
            TypeReference<List<ImageExampleDTO>> typeReference = new TypeReference<List<ImageExampleDTO>>() {
            };
            creativeSchemeResponse.setImageExample(JSONUtil.toBean(creativeScheme.getImageExample(), typeReference, Boolean.TRUE));
        }
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

    /**
     * 转换为 PageResult<CreativeSchemeRespVO>
     *
     * @param page 创作方案分页
     * @return PageResult<CreativeSchemeRespVO>
     */
    default PageResult<CreativeSchemeRespVO> convertPage(IPage<CreativeSchemeDO> page) {
        PageResult<CreativeSchemeRespVO> pageResult = new PageResult<>();
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return new PageResult<>(Collections.emptyList(), page.getTotal());
        }
        List<CreativeSchemeDO> records = page.getRecords();

        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        // 用户更新者ID列表。
        List<Long> updaterList = records.stream().map(item -> Long.valueOf(item.getUpdater())).distinct().collect(Collectors.toList());
        // 获取用户更新者ID，昵称 Map。
        Map<Long, String> updaterMap = UserUtils.getUserNicknameMapByIds(updaterList);

        List<CreativeSchemeRespVO> collect = records.stream().map(item -> {
            CreativeSchemeRespVO response = this.convertResponse(item);
            response.setCreator(creatorMap.get(Long.valueOf(item.getCreator())));
            response.setUpdater(updaterMap.get(Long.valueOf(item.getUpdater())));
            return response;
        }).collect(Collectors.toList());

        pageResult.setTotal(page.getTotal());
        pageResult.setList(collect);
        return pageResult;
    }
}
