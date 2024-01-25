package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.RandomUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.reference.ReferenceSchemeDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class ActionUtils {

    /**
     * 获取生成模型的参数key
     *
     * @param generateModel 生成模式
     * @param key           key
     * @return 处理之后的key
     */
    public static String getGenerateModeParamKey(String generateModel, String key) {
        //return generateModel + "_" + key;
        return key;
    }

    /**
     * 随机获取一个对象
     *
     * @param referList  参考内容
     * @param recordList 已经处理过的参考内容
     * @return 参考内容
     */
    public static ReferenceSchemeDTO randomReference(List<ReferenceSchemeDTO> referList, List<ReferenceSchemeDTO> recordList) {
        // 创建一个新集合，只包含不在 recordList 中的对象
        List<ReferenceSchemeDTO> filterReferList = referList.stream()
                .filter(reference -> recordList.stream().noneMatch(record -> record.getId().equals(reference.getId())))
                .collect(Collectors.toList());

        // 如果过滤后的集合为空，说明 recordList 已经包含了所有的对象，直接返回一个随机的对象即可。
        if (filterReferList.isEmpty()) {
            return referList.get(RandomUtil.randomInt(referList.size()));
        }

        // 随机获取一个对象
        int randomIndex = RandomUtil.randomInt(filterReferList.size());
        ReferenceSchemeDTO reference = filterReferList.get(randomIndex);

        recordList.add(reference);
        return reference;
    }

}
