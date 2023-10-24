package com.starcloud.ops.business.app.service.spell;

/**
 * 拼音服务接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
public interface SpellService {

    /**
     * 获取拼音
     *
     * @param character 字符
     * @return 拼音
     */
    String[] getSpell(char character);

    /**
     * 获取拼音
     *
     * @param chinese 中文
     * @return 拼音
     */
    String getSpell(String chinese);
}
