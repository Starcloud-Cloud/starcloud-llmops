package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param materialList 资料库列表
     * @param posterStyle  海报风格
     * @param total        总数
     * @param index        索引
     * @return 处理后的资料库列表
     */
    @Override
    public List<BookListCreativeMaterialDTO> handleMaterialList(List<BookListCreativeMaterialDTO> materialList, PosterStyleDTO posterStyle, Integer total, Integer index) {
        if (isReturnEmpty(materialList, posterStyle, total, index)) {
            return Collections.emptyList();
        }

        Integer copySize = posterStyle.getMaxTotalImageCount();
        Map<Integer, List<BookListCreativeMaterialDTO>> materialMap = this.getMaterialListMap(materialList, copySize, total);
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
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<BookListCreativeMaterialDTO> materialList) {
        // 如果资料库为空，直接返回海报风格，不做处理
        if (CollectionUtil.isEmpty(materialList)) {
            return posterStyle;
        }

        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
        // 进行海报风格的处理


        // 设置资料库列表，后续可能会用到
        style.setMaterialList(materialList);
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
    private Boolean isReturnEmpty(List<BookListCreativeMaterialDTO> materialList, PosterStyleDTO posterStyle, Integer total, Integer index) {
        return CollectionUtil.isEmpty(materialList) ||
                ObjectUtil.isNull(posterStyle) ||
                NumberUtils.isNegative(total) ||
                NumberUtils.isNegative(index);
    }

}
