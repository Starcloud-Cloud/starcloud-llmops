package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.PictureCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType(MaterialTypeEnum.PICTURE)
public class PictureMaterialHandler extends AbstractMaterialHandler<PictureCreativeMaterialDTO> {

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param materialList 资料库列表
     * @param posterStyle  海报风格
     * @param total        总数
     * @param index        索引
     * @return 处理后的资料库列表
     */
    @Override
    public List<PictureCreativeMaterialDTO> handleMaterialList(List<PictureCreativeMaterialDTO> materialList, PosterStyleDTO posterStyle, Integer total, Integer index) {
        if (isReturnEmpty(materialList, posterStyle, total, index)) {
            return Collections.emptyList();
        }

        Integer maxTotalImageCount = posterStyle.getMaxTotalImageCount();
        Map<Integer, List<PictureCreativeMaterialDTO>> materialMap = this.getMaterialMap(materialList, maxTotalImageCount, total);
        if (CollectionUtil.isEmpty(materialMap) || !materialMap.containsKey(index)) {
            return Collections.emptyList();
        }
        return materialMap.get(index);
    }

    /**
     * 处理海报风格，返回处理后的海报风格
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @return 处理后的海报风格
     */
    @Override
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<PictureCreativeMaterialDTO> materialList) {
        // 如果资料库为空，直接返回海报风格，不做处理
        if (CollectionUtil.isEmpty(materialList)) {
            return posterStyle;
        }

        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
        List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (PosterTemplateDTO posterTemplate : posterTemplateList) {
            if (PosterModeEnum.SEQUENCE.name().equals(posterTemplate.getMode())) {
                assembleSequence(posterTemplate, materialList);
            } else {
                assembleRandom(posterTemplate, materialList);
            }
        }
        // 设置资料库列表，后续可能会用到
        style.setMaterialList(materialList);
        style.setTemplateList(posterTemplateList);
        return style;
    }

    /**
     * 判断是否需要返回空集合
     *
     * @param materialList 资料库列表
     * @param posterStyle  海报风格
     * @param total        总数
     * @param index        索引
     * @return 是否需要返回空集合
     */
    private Boolean isReturnEmpty(List<PictureCreativeMaterialDTO> materialList, PosterStyleDTO posterStyle, Integer total, Integer index) {
        // 如果资料库为空，或者海报风格为空，或者海报风格的最大图片数量为空，或者海报风格的最大图片数量小于等于0，或者总数为空，或者总数小于等于0，或者索引为空，或者索引小于0，则返回true
        return CollectionUtil.isEmpty(materialList) ||
                posterStyle == null ||
                posterStyle.getMaxTotalImageCount() == null ||
                posterStyle.getMaxTotalImageCount() <= 0 ||
                total == null ||
                total <= 0 ||
                index == null ||
                index < 0;
    }

    /**
     * 顺序模式变量组装
     *
     * @param posterTemplate 海报模板
     * @param materialList   资料库列表
     */
    private void assembleSequence(PosterTemplateDTO posterTemplate, List<PictureCreativeMaterialDTO> materialList) {
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
        for (PosterVariableDTO variable : variableList) {
            if (AppVariableTypeEnum.IMAGE.name().equals(variable.getType())) {
                // 如果资料库为空，直接跳出循环
                if (CollectionUtil.isEmpty(materialList)) {
                    break;
                }
                PictureCreativeMaterialDTO pictureMaterial = materialList.get(0);
                variable.setValue(pictureMaterial.getPictureUrl());
                // 移除已使用的资料库
                materialList.remove(0);
            }
        }
        posterTemplate.setVariableList(variableList);
    }

    /**
     * 随机模式变量组装
     *
     * @param posterTemplate 海报模板
     * @param materialList   资料库列表
     */
    private void assembleRandom(PosterTemplateDTO posterTemplate, List<PictureCreativeMaterialDTO> materialList) {
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
        for (PosterVariableDTO variable : variableList) {
            if (AppVariableTypeEnum.IMAGE.name().equals(variable.getType())) {
                // 如果资料库为空，直接跳出循环
                if (CollectionUtil.isEmpty(materialList)) {
                    break;
                }
                int randomIndex = RandomUtil.randomInt(materialList.size());
                PictureCreativeMaterialDTO pictureMaterial = materialList.get(randomIndex);
                variable.setValue(pictureMaterial.getPictureUrl());
                // 移除已使用的资料库
                materialList.remove(randomIndex);
            }
        }
        posterTemplate.setVariableList(variableList);
    }

}
