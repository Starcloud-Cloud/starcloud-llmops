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
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书单资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
public class DefaultMaterialHandler extends AbstractMaterialHandler<BookListCreativeMaterialDTO> {

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
            //只有顺序
            assembleSequence(template, materialList);
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
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());

        for (int i = 0; i < variableList.size(); i++) {
            PosterVariableDTO variable = variableList.get(i);
            if (AppVariableTypeEnum.IMAGE.name().equals(variable.getType())) {
                // todo 是否需要数据填充?
            }
        }
        posterTemplate.setVariableList(variableList);
    }

}
