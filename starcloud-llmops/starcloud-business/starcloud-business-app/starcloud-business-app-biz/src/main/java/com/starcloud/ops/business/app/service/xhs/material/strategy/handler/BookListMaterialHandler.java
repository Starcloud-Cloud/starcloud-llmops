package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 书单资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType(MaterialTypeEnum.BOOK_LIST)
class BookListMaterialHandler extends AbstractMaterialHandler<BookListCreativeMaterialDTO> {

    /**
     * 获取每个海报风格需要的素材数量
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    @Override
    protected List<Integer> needMaterialSizeList(List<PosterStyleDTO> posterStyleList) {
        return this.findMaterialIndexList(posterStyleList);
    }

    /**
     * 处理海报风格，返回处理后的海报风格
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @return 处理后的海报风格
     */
    @Override
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<BookListCreativeMaterialDTO> materialList) {
        // 如果资料库为空，直接返回海报风格，不做处理
        if (CollectionUtil.isEmpty(materialList)) {
            return posterStyle;
        }

        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
        // 进行海报风格的处理
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
    private void assembleSequence(PosterTemplateDTO posterTemplate, List<BookListCreativeMaterialDTO> materialList) {
        // 如果资料库为空，直接跳出循环
        if (CollectionUtil.isEmpty(materialList)) {
            return;
        }

        // 图片类型的变量列表
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList()).stream()
                .filter(item -> AppVariableTypeEnum.IMAGE.name().equals(item.getType()))
                .collect(Collectors.toList());

        for (int i = 0; i < variableList.size(); i++) {
            PosterVariableDTO variable = variableList.get(i);
            // 如果用户没有选择占位符，则按照顺序放置占位符
            if (Objects.isNull(variable.getValue())) {
                // 防止溢出 i % materialList.size()。
                variable.setValue(this.materialPlaceholder(i % materialList.size(), "coverUrl"));
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
    private void assembleRandom(PosterTemplateDTO posterTemplate, List<BookListCreativeMaterialDTO> materialList) {
        // 如果资料库为空，直接跳出循环
        if (CollectionUtil.isEmpty(materialList)) {
            return;
        }

        // 图片类型的变量列表
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList()).stream()
                .filter(item -> AppVariableTypeEnum.IMAGE.name().equals(item.getType()))
                .collect(Collectors.toList());

        List<String> usedMaterialList = new ArrayList<>();
        List<String> materialUrlList = materialList.stream().map(BookListCreativeMaterialDTO::getCoverUrl).collect(Collectors.toList());
        for (PosterVariableDTO variable : variableList) {
            // 如果用户没有选择占位符，则随机放置占位符, 否则不做任何处理
            if (Objects.isNull(variable.getValue())) {
                int randomInt = randomInt(usedMaterialList, materialUrlList);
                variable.setValue(this.materialPlaceholder(randomInt, "coverUrl"));
            }
        }
        posterTemplate.setVariableList(variableList);
    }

    /**
     * 递归实现获取随机下标，且获取到的值不在 usedMaterialList 中
     *
     * @param usedMaterialList 已经使用的下标
     * @param materialList     资料库列表
     * @return 随机下标
     */
    protected Integer randomInt(List<String> usedMaterialList, List<String> materialList) {
        int randomInt = RandomUtil.randomInt(materialList.size());
        if (usedMaterialList.size() == materialList.size()) {
            return randomInt;
        }
        if (usedMaterialList.contains(materialList.get(randomInt))) {
            return randomInt(materialList, usedMaterialList);
        }
        usedMaterialList.add(materialList.get(randomInt));
        return randomInt;
    }

}
