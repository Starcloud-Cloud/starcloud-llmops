package com.starcloud.ops.business.app.util;

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
}
