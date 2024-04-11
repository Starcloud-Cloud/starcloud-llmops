package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.xhs.material.dto.ContractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 合同号素材处理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType(MaterialTypeEnum.CONTRACT)
public class ContractMaterialHandler extends AbstractMaterialHandler<ContractCreativeMaterialDTO> {

    @Override
    public void validatePosterStyle(PosterStyleDTO posterStyle) {
        super.validatePosterStyle(posterStyle);
    }

    /**
     * 获取每个海报风格需要的素材数量
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    @Override
    protected List<Integer> needMaterialSizeList(List<PosterStyleDTO> posterStyleList) {
        return super.needMaterialSizeList(posterStyleList);
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
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<ContractCreativeMaterialDTO> materialList, MaterialMetadata metadata) {
        // 如果资料库为空，直接返回海报风格，不做处理
        if (CollectionUtil.isEmpty(materialList)) {
            return posterStyle;
        }

        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
        // 进行变量替换
        Map<String, Object> replaceValueMap = this.replaceVariable(style, materialList, metadata, Boolean.TRUE);

        List<PosterTemplateDTO> templates = Lists.newArrayList();
        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        // // 进行海报风格的处理
        for (PosterTemplateDTO template : templateList) {
            // 如果图片数量不为 1，直接跳过，不进行处理
            if (template.getTotalImageCount() != 1) {
                templates.add(template);
                continue;
            }

            // 如果图片数量为 1，则需要判断，当前图片变量替换之后是否有值，没有值，这个图片不需要进行生成。
            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(template.getVariableList());
            for (PosterVariableDTO variable : variableList) {
                if (CreativeUtils.isImageVariable(variable) &&
                        StringUtil.objectNotBlank(replaceValueMap.get(variable.getUuid()))) {
                    templates.add(template);
                }
            }
        }
        style.setTemplateList(templates);
        return style;
    }

}
