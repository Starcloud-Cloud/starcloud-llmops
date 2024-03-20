package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
public abstract class AbstractMaterialHandler<M extends AbstractBaseCreativeMaterialDTO> {

    private static final String PLACEHOLDER = "{{素材.docs[%s].%s}}";

    private static final Pattern PATTERN = Pattern.compile("\\[(\\d+)]");

    /**
     * 获取每个海报风格需要的素材数量
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    protected abstract List<Integer> needMaterialSizeList(List<PosterStyleDTO> posterStyleList);

    /**
     * 处理素材为一个Map
     *
     * @param materialList     素材列表
     * @param materialSizeList 每个海报风格需要的素材数量
     * @return 海报素材 map
     */
    protected Map<Integer, List<M>> doHandleMaterialMap(List<M> materialList, List<Integer> materialSizeList) {
        // 提供一个默认的复制逻辑，不同的素材可能需要不同的复制逻辑, 具体子类实现即可
        return getMaterialListMap(materialList, materialSizeList);
    }

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param materialList    资料库列表
     * @param posterStyleList 海报风格列表
     * @return 处理后的资料库列表
     */
    public Map<Integer, List<M>> handleMaterialMap(List<M> materialList, List<PosterStyleDTO> posterStyleList) {
        if (CollectionUtil.isEmpty(materialList) || CollectionUtil.isEmpty(posterStyleList)) {
            return Collections.emptyMap();
        }
        List<Integer> needMaterialSizeList = needMaterialSizeList(posterStyleList);
        if (CollectionUtil.isEmpty(needMaterialSizeList)) {
            return Collections.emptyMap();
        }
        return doHandleMaterialMap(materialList, needMaterialSizeList);
    }

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
     * @param needSizeList 需要复制的列表
     * @return 分组后的资料库列表
     */
    private Map<Integer, List<M>> getMaterialListMap(List<M> materialList, List<Integer> needSizeList) {
        // 结果集合
        Map<Integer, List<M>> resultMap = new HashMap<>();

        // 如果素材列表为空或者需要复制的数量集合为空
        if (CollectionUtil.isEmpty(materialList) || CollectionUtil.isEmpty(needSizeList)) {
            return resultMap;
        }

        // 处理制的数量集合
        needSizeList = needSizeList.stream().map(item -> {
            if (item == null || item <= 0) {
                return 0;
            }
            return item;
        }).collect(Collectors.toList());

        // 深度复制一份资料库列表，避免修改原列表
        List<M> copyMaterialList = SerializationUtils.clone((ArrayList<M>) materialList);

        // 记录原始素材列表的大小
        int originalSize = copyMaterialList.size();
        // 计算需要复制的总数
        int copyTotal = needSizeList.stream().mapToInt(Integer::intValue).sum();

        // 如果素材列表的数量小于需要复制的总数，说明需要进行复制。
        if (originalSize < copyTotal) {
            // 如果imageList的数量不够，则需要先对imageList进行扩容
            int requiredSize = copyTotal - originalSize;
            // 将imageList从头开始按顺序复制，直至满足扩容要求
            for (int i = 0; i < requiredSize; i++) {
                copyMaterialList.add(copyMaterialList.get(i % originalSize));
            }
        }

        // 扩容后按照顺序复制图片
        int currentIndex = 0;
        for (int i = 0; i < needSizeList.size(); i++) {
            int size = needSizeList.get(i);
            List<M> copiedImages = new ArrayList<>(copyMaterialList.subList(currentIndex, currentIndex + size));
            resultMap.put(i, copiedImages);
            currentIndex += size;
        }

        return resultMap;
    }

    /**
     * 获取风格海报素材选择最大索引列表
     *
     * @param posterStyleList 海报列表
     * @return 风格海报素材选择最大索引列表
     */
    protected List<Integer> findMaterialIndexList(List<PosterStyleDTO> posterStyleList) {
        List<Integer> materialIndexList = new ArrayList<>();
        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(posterStyleList)) {
            // 如果风格为空，填充 0
            if (Objects.isNull(posterStyle)) {
                materialIndexList.add(0);
                continue;
            }

            List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());
            // 如果海报模板为空，填充 0
            if (CollectionUtil.isEmpty(posterTemplateList)) {
                materialIndexList.add(0);
                continue;
            }

            // 报模板，图片变量值，获取到选择素材的最大索引列表
            List<Integer> templateIndexList = new ArrayList<>();
            for (PosterTemplateDTO template : posterTemplateList) {
                // 如果海报模板为空，设置默认值为 -1
                if (Objects.isNull(template)) {
                    templateIndexList.add(-1);
                    continue;
                }
                // 获取每一个海报模板，图片变量值，获取到选择素材的最大索引
                Integer maxIndex = CollectionUtil.emptyIfNull(template.getVariableList()).stream().filter(Objects::nonNull).filter(item -> AppVariableTypeEnum.IMAGE.name().equals(item.getType())).map(item -> matcherFirstInt(String.valueOf(item.getValue()))).max(Comparator.comparingInt(Integer::intValue)).orElse(-1);
                templateIndexList.add(maxIndex);
            }

            // 所有的海报模板中获取最大的那个素材索引。如果没有，为 -1
            Integer maxIndex = templateIndexList.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(-1);
            if (maxIndex == -1) {
                materialIndexList.add(0);
            } else {
                materialIndexList.add(maxIndex);
            }
        }
        // 说明均没有匹配到
        if (materialIndexList.stream().noneMatch(item -> item > 0)) {
            // 返回图片
            return CollectionUtil.emptyIfNull(posterStyleList).stream()
                    .map(item -> (item == null || NumberUtils.isNegative(item.getTotalImageCount()) ? 0 : item.getTotalImageCount()))
                    .collect(Collectors.toList());
        }
        return materialIndexList;
    }

    /**
     * 获取素材占位符
     *
     * @param i     素材列表下表
     * @param field 素材字段
     * @return 素材占位符
     */
    protected String materialPlaceholder(Integer i, String field) {
        return String.format(PLACEHOLDER, i, field);
    }

    /**
     * 获取到 [n] 中的数字，如果包含多个 [n],只返回第一个匹配到的。
     *
     * @param input 输入
     * @return 返回的数字，没有匹配到，返回 -1
     */
    protected static Integer matcherFirstInt(String input) {
        if (StringUtils.isBlank(input)) {
            return -1;
        }
        // 定义正则表达式匹配方括号中的数字
        Matcher matcher = PATTERN.matcher(input);
        // 使用正则表达式匹配，只返回匹配到的第一个。
        if (!matcher.find()) {
            // 没有匹配到，返回 -1
            return -1;
        }
        return Integer.valueOf(matcher.group(1));
    }

}
