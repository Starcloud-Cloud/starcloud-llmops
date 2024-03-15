package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
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
public abstract class AbstractMaterialHandler<M extends AbstractBaseCreativeMaterialDTO> {

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @return 处理后的海报风格
     */
    public abstract PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<M> materialList);

}
