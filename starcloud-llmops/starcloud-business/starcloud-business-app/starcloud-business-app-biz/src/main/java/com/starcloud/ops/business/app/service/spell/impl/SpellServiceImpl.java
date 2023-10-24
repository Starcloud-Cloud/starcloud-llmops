package com.starcloud.ops.business.app.service.spell.impl;

import com.google.common.collect.ArrayListMultimap;
import com.starcloud.ops.business.app.service.spell.SpellService;
import com.starcloud.ops.business.app.service.spell.VocabularyCache;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@Slf4j
@Service
public class SpellServiceImpl implements SpellService {

    @Resource
    private VocabularyCache vocabularyCache;

    /**
     * 获取拼音
     *
     * @param character 字符
     * @return 拼音
     */
    @Override
    public String[] getSpell(char character) {
        try {
            HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            //ASCII >=33 ASCII<=125的直接返回 ,ASCII码表：http://www.asciitable.com/
            if (character >= 32 && character <= 125) {
                return new String[]{String.valueOf(character)};
            }
            return PinyinHelper.toHanyuPinyinStringArray(character, outputFormat);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("getSpell error, character:{}", character, e);
        }
        return null;
    }

    /**
     * 获取拼音
     *
     * @param chinese 中文
     * @return 拼音
     */
    @Override
    public String getSpell(String chinese) {
        ArrayListMultimap<String, String> vocabularyMap = vocabularyCache.getVocabulary();
        if (StringUtils.isBlank(chinese)) {
            return null;
        }

        chinese = chinese.replaceAll("[\\.，\\,！·\\!？\\?；\\;\\(\\)（）\\[\\]\\:： ]+", " ").trim();

        StringBuilder py_sb = new StringBuilder(32);
        char[] chs = chinese.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            String[] py_arr = getSpell(chs[i]);
            if (py_arr == null || py_arr.length < 1) {
                log.warn("getSpell error, character:{}", chs[i]);
                continue;
            }
            if (py_arr.length == 1) {
                py_sb.append(convertInitialToUpperCase(py_arr[0]));
            } else if (py_arr.length == 2 && py_arr[0].equals(py_arr[1])) {
                py_sb.append(convertInitialToUpperCase(py_arr[0]));
            } else {
                String resultPy = null, defaultPy = null;
                ;
                for (String py : py_arr) {
                    String left = null;    //向左多取一个字,例如 银[行]
                    if (i >= 1 && i + 1 <= chinese.length()) {
                        left = chinese.substring(i - 1, i + 1);
                        if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(left)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String right = null;    //向右多取一个字,例如 [长]沙
                    if (i <= chinese.length() - 2) {
                        right = chinese.substring(i, i + 2);
                        if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(right)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String middle = null;    //左右各多取一个字,例如 龙[爪]槐
                    if (i >= 1 && i + 2 <= chinese.length()) {
                        middle = chinese.substring(i - 1, i + 2);
                        if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(middle)) {
                            resultPy = py;
                            break;
                        }
                    }
                    String left3 = null;    //向左多取2个字,如 芈月[传],列车长
                    if (i >= 2 && i + 1 <= chinese.length()) {
                        left3 = chinese.substring(i - 2, i + 1);
                        if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(left3)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String right3 = null;    //向右多取2个字,如 [长]孙无忌
                    if (i <= chinese.length() - 3) {
                        right3 = chinese.substring(i, i + 3);
                        if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(right3)) {
                            resultPy = py;
                            break;
                        }
                    }

                    if (vocabularyMap.containsKey(py) && vocabularyMap.get(py).contains(String.valueOf(chs[i]))) {    //默认拼音
                        defaultPy = py;
                    }
                }

                if (StringUtils.isEmpty(resultPy)) {
                    if (StringUtils.isNotEmpty(defaultPy)) {
                        resultPy = defaultPy;
                    } else {
                        resultPy = py_arr[0];
                    }
                }
                py_sb.append(convertInitialToUpperCase(resultPy));
            }
        }

        return py_sb.toString();
    }

    private String convertInitialToUpperCase(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
