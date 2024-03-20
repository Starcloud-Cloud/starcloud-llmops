package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType(MaterialTypeEnum.PICTURE)
class PictureMaterialHandler extends AbstractMaterialHandler<PictureCreativeMaterialDTO> {

    /**
     * 获取每个海报风格需要的素材数量
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    @Override
    protected List<Integer> needMaterialSizeList(List<PosterStyleDTO> posterStyleList) {
        return CollectionUtil.emptyIfNull(posterStyleList).stream()
                .map(item -> (item == null || NumberUtils.isNegative(item.getTotalImageCount()) ? 0 : item.getTotalImageCount()))
                .collect(Collectors.toList());
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
        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (PosterTemplateDTO template : templateList) {
            if (PosterModeEnum.SEQUENCE.name().equals(template.getMode())) {
                assembleSequence(template, materialList);
            } else {
                assembleRandom(template, materialList);
            }
        }
        // 设置资料库列表，后续可能会用到
        style.setMaterialList(materialList);
        style.setTemplateList(templateList);
        return style;
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
