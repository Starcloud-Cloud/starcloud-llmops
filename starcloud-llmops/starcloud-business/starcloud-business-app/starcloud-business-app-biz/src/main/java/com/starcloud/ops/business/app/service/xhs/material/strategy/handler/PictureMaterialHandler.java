package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.PictureCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import org.springframework.stereotype.Component;

import java.util.List;


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


        return null;
    }
}
