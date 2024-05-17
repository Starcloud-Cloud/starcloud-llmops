package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图片资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType("picture")
class PictureMaterialHandler extends AbstractMaterialHandler {

    private static final String MATERIAL_KEY = "pictureUrl";

    @Override
    public void validatePosterStyle(PosterStyleDTO posterStyle) {
        AppValidate.notNull(posterStyle, "创作方案配置异常！海报风格不能为空！");
        AppValidate.notEmpty(posterStyle.getTemplateList(), "创作方案配置异常！海报模板不能为空！");
    }

    /**
     * 获取每个海报风格需要的素材数量
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    @Override
    protected List<Integer> needMaterialSizeList(List<PosterStyleDTO> posterStyleList) {
        return CollectionUtil.emptyIfNull(posterStyleList)
                .stream()
                .map(item -> (item == null || NumberUtils.isNegative(item.getTotalImageCount()) ? 0 : item.getTotalImageCount()))
                .collect(Collectors.toList());
    }

    /**
     * 处理海报风格，返回处理后的海报风格
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @param metadata     素材元数据
     * @return 处理后的海报风格
     */
    @Override
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<Map<String, Object>> materialList, MaterialMetadata metadata) {
        // 如果资料库为空，直接返回海报风格，不做处理
        if (CollectionUtil.isEmpty(materialList)) {
            return posterStyle;
        }

        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (PosterTemplateDTO template : templateList) {
            String mode = StringUtil.isBlank(template.getMode()) ? PosterModeEnum.SEQUENCE.name() : template.getMode();
            if (PosterModeEnum.SEQUENCE.name().equals(mode)) {
                assembleSequence(template, materialList);
            } else {
                assembleRandom(template, materialList);
            }
            template.setIsExecute(Boolean.TRUE);
        }
        style.setTemplateList(templateList);
        // 调用父类，文字类型可能需要处理填充
        return style;
    }

    /**
     * 顺序模式变量组装
     *
     * @param posterTemplate 海报模板
     * @param materialList   资料库列表
     */
    private void assembleSequence(PosterTemplateDTO posterTemplate, List<Map<String, Object>> materialList) {
        // 复制一份的资料库列表，防止对原列表造成影响
        List<Map<String, Object>> copyMaterialList = SerializationUtils.clone((ArrayList<Map<String, Object>>) materialList);
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
        for (PosterVariableDTO variable : variableList) {
            if (CreativeUtils.isImageVariable(variable) && StringUtil.objectBlank(variable.getValue())) {
                // 如果资料库为空，直接跳出循环
                if (CollectionUtil.isEmpty(copyMaterialList)) {
                    break;
                }
                Map<String, Object> pictureMaterial = copyMaterialList.get(0);
                variable.setValue(pictureMaterial.get(MATERIAL_KEY));
                // 移除已使用的资料库
                copyMaterialList.remove(0);
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
    private void assembleRandom(PosterTemplateDTO posterTemplate, List<Map<String, Object>> materialList) {
        // 复制一份的资料库列表，防止对原列表造成影响
        List<Map<String, Object>> copyMaterialList = SerializationUtils.clone((ArrayList<Map<String, Object>>) materialList);
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
        for (PosterVariableDTO variable : variableList) {
            if (CreativeUtils.isImageVariable(variable) && StringUtil.objectBlank(variable.getValue())) {
                // 如果资料库为空，直接跳出循环
                if (CollectionUtil.isEmpty(copyMaterialList)) {
                    break;
                }
                int randomIndex = RandomUtil.randomInt(copyMaterialList.size());
                Map<String, Object> pictureMaterial = copyMaterialList.get(randomIndex);
                variable.setValue(pictureMaterial.get(MATERIAL_KEY));
                // 移除已使用的资料库
                copyMaterialList.remove(randomIndex);
            }
        }
        posterTemplate.setVariableList(variableList);
    }

}
