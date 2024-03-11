package com.starcloud.ops.business.app.convert.xhs.material;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.material.CreativeMaterialDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CreativeMaterialConvert {

    CreativeMaterialConvert INSTANCE = Mappers.getMapper(CreativeMaterialConvert.class);

    /**
     * 转换素材内容实体类
     */
//    default AbstractBaseCreativeMaterialDTO covertMaterialDetail(String materialType, String json) {
//        MaterialTypeEnum typeEnum = MaterialTypeEnum.of(materialType);
//        Set<String> jsonField = JSONUtil.parseObj(json).keySet();
//        List<String> dtoField = Arrays.stream(typeEnum.getAClass().getDeclaredFields()).filter(field -> Objects.nonNull(field.getAnnotation(FieldDefine.class))).map(Field::getName).collect(Collectors.toList());
//
//        if (!CollUtil.containsAll(dtoField, jsonField)) {
//            throw exception(TYPE_UNMATCH_FIELD, materialType);
//        }
//
//        try {
//            return JSONUtil.toBean(json, typeEnum.getAClass()).setType(materialType);
//        } catch (Exception e) {
//            throw exception(TYPE_UNMATCH_FIELD, materialType);
//        }
//    }

    default CreativeMaterialDO convert(BaseMaterialVO baseMaterialVO, AbstractBaseCreativeMaterialDTO materialDetail) {
        CreativeMaterialDO materialDO = baseConvert(baseMaterialVO);
        materialDO.setContent(materialDetail.generateContent());
        return materialDO;
    }

    @Mapping(source = "materialDetail", target = "materialDetail", qualifiedByName = "toJson")
    CreativeMaterialDO baseConvert(BaseMaterialVO baseMaterialVO);

    @Mapping(source = "materialDetail", target = "materialDetail", qualifiedByName = "parseMaterial")
    MaterialRespVO convert(CreativeMaterialDO creativeMaterialDO);

    List<MaterialRespVO> convert(List<CreativeMaterialDO> materialDOList);


    @Named("parseMaterial")
    default AbstractBaseCreativeMaterialDTO parseMaterial(String str) {
        return JsonUtils.parseObject(str, AbstractBaseCreativeMaterialDTO.class);
    }

    @Named("toJson")
    default String toJson(AbstractBaseCreativeMaterialDTO dto) {
        return JsonUtils.toJsonString(dto);
    }
}
