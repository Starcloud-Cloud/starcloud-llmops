package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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
public abstract class AbstractMaterialHandler<M extends AbstractBaseCreativeMaterialDTO> {

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param materialList 资料库列表
     * @param posterStyle  海报风格
     * @param total        总数
     * @param index        索引
     * @return 处理后的资料库列表
     */
    public abstract List<M> handleMaterialList(List<M> materialList, PosterStyleDTO posterStyle, Integer total, Integer index);

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @return 处理后的海报风格
     */
    public abstract PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<M> materialList);

    /**
     * 将资料库列表按照指定的大小和总数进行分组
     *
     * @param materialList 资料库列表
     * @param copySize     复制大小
     * @param total        总数
     * @return 分组后的资料库列表
     */
    protected Map<Integer, List<M>> getMaterialListMap(List<M> materialList, int copySize, int total) {
        // 结果集合
        Map<Integer, List<M>> resultMap = new HashMap<>();

        // 如果imageList为空或者copySize小于等于0或者total小于等于0，则直接返回空结果
        if (CollectionUtil.isEmpty(materialList) || copySize <= 0 || total <= 0) {
            return resultMap;
        }

        // 深度复制一份资料库列表，避免修改原列表
        List<M> copyMaterialList = SerializationUtils.clone((ArrayList<M>) materialList);

        // 记录原始imageList的大小
        int originalSize = copyMaterialList.size();
        // 计算需要复制的总数
        int copyTotal = copySize * total;

        // 如果imageList的数量足够大，则直接按照顺序复制
        if (originalSize >= copyTotal) {
            for (int i = 0; i < total; i++) {
                List<M> copy = new ArrayList<>(copyMaterialList.subList(i * copySize, (i + 1) * copySize));
                resultMap.put(i, copy);
            }
            return resultMap;
        }

        // 如果imageList的数量不够，则需要先对imageList进行扩容
        int requiredSize = copyTotal - originalSize;
        // 将imageList从头开始按顺序复制，直至满足扩容要求
        for (int i = 0; i < requiredSize; i++) {
            copyMaterialList.add(copyMaterialList.get(i % originalSize));
        }

        // 重新计算结果集合
        for (int i = 0; i < total; i++) {
            List<M> copy = new ArrayList<>(copyMaterialList.subList(i * copySize, (i + 1) * copySize));
            resultMap.put(i, copy);
        }

        return resultMap;
    }

}
