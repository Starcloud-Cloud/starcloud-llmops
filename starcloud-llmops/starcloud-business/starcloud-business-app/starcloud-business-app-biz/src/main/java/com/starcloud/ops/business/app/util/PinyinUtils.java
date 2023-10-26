package com.starcloud.ops.business.app.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 汉字转拼音工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@SuppressWarnings("unused")
@Slf4j
public class PinyinUtils {

    /**
     * 汉字正则
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]+";

    /**
     * 将汉字转换为拼音全拼 首字母大写，多音字的返回多个拼音以逗号分隔
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static String pinyin(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "";
        }
        return join(doConvertAndToSet(chinese));
    }

    /**
     * 获取拼音集合，首字母大写；
     *
     * @param chinese 汉字
     * @return 拼音集合
     */
    public static List<String> pinyinList(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(doConvertAndToSet(chinese));
    }

    /**
     * 将汉字转换为拼音全拼 大写，多音字的返回多个拼音以逗号分隔
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static String pinyinToUpperCase(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "";
        }
        return join(doConvertAndToSet(chinese)).toUpperCase();
    }

    /**
     * 将汉字转换为拼音全拼 小写，多音字的返回多个拼音以逗号分隔
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static String pinyinToLowerCase(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "";
        }
        return join(doConvertAndToSet(chinese)).toLowerCase();
    }

    /**
     * 将汉字转换为拼音简拼 大写，多音字的返回多个拼音以逗号分隔
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static String pinyinSimple(String chinese) {
        String[] strArray = pinyin(chinese).split(",");
        StringBuilder strChar = new StringBuilder();
        for (String str : strArray) {
            char[] arr = str.toCharArray();
            for (char character : arr) {
                if (CharUtils.isAsciiAlphaUpper(character)) {
                    strChar.append(character);
                }
            }
            strChar.append(",");
        }
        return strChar.substring(0, strChar.length() - 1);
    }

    /**
     * 将汉字转换为拼音简拼 大写，多音字的返回多个拼音以逗号分隔
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static String pinyinFirstSimple(String chinese) {
        String[] strArray = pinyin(chinese).split(",");
        StringBuilder strChar = new StringBuilder();
        if (ArrayUtils.isEmpty(strArray)) {
            return "";
        }
        String first = strArray[0];
        char[] arr = first.toCharArray();
        for (char character : arr) {
            if (CharUtils.isAsciiAlphaUpper(character)) {
                strChar.append(character);
            }
        }
        return strChar.toString();
    }

    /**
     * 将汉字转换为全拼 小写 不识别多音字
     *
     * @param chinese 汉字
     * @return 全拼
     */
    public static String pinyinNoPolyphonic(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "";
        }
        char[] chars = chinese.toCharArray();
        String[] fullSpells;
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        StringBuilder spellBuilder = new StringBuilder();
        for (char character : chars) {
            // 是中文则转为拼音
            if (isChinese(character)) {
                try {
                    fullSpells = PinyinHelper.toHanyuPinyinStringArray(character, format);
                    if (ArrayUtils.isNotEmpty(fullSpells)) {
                        spellBuilder.append(fullSpells[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination exception) {
                    // 转换失败则不做任何处理, 继续循环
                    log.error("转为拼音失败：失败的字符：{}，失败原因: {}", character, exception.getMessage());
                }
            } else if (CharUtils.isAsciiAlphaLower(character) || CharUtils.isAsciiAlphaUpper(character)) {
                // a-z或者A-Z转换则不做任何处理
                spellBuilder.append(character);
            }
        }
        return spellBuilder.toString();
    }

    /**
     * 汉字转换为拼音
     *
     * @param chinese 汉字
     * @return 拼音
     */
    public static Set<String> doConvertAndToSet(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return Collections.emptySet();
        }
        char[] chars = chinese.toCharArray();
        String[][] convert = new String[chinese.length()][];

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];
            // 是中文则转为拼音
            if (isChinese(character)) {
                try {
                    convert[i] = PinyinHelper.toHanyuPinyinStringArray(character, format);
                } catch (BadHanyuPinyinOutputFormatCombination exception) {
                    log.error("转为拼音失败：失败的字符：{}，失败原因: {}", character, exception.getMessage());
                }
            } else if (CharUtils.isAsciiAlphaLower(character) || CharUtils.isAsciiAlphaUpper(character)) {
                // a-z或者A-Z转换则不做任何处理
                convert[i] = new String[]{String.valueOf(character)};
            } else {
                // 不是中文或者a-z或者A-Z则不转换, 空字符串
                convert[i] = new String[]{""};
            }
        }
        String[] exchange = exchange(convert);
        if (ArrayUtils.isEmpty(exchange)) {
            return Collections.emptySet();
        }
        return Arrays.stream(exchange).collect(Collectors.toSet());
    }

    /**
     * 将二维数组转换为一维数组
     *
     * @param strJaggedArray 二维数组
     * @return 一维数组
     */
    public static String[] exchange(String[][] strJaggedArray) {
        String[][] temp = doExchange(strJaggedArray);
        return temp[0];
    }

    /**
     * 递归实现多数组的排列组合
     *
     * @param strJaggedArray 多数组
     * @return 排列组合后的数组
     */
    private static String[][] doExchange(String[][] strJaggedArray) {
        int len = strJaggedArray.length;
        if (len >= 2) {
            int len1 = strJaggedArray[0].length;
            int len2 = strJaggedArray[1].length;
            int length = len1 * len2;
            String[] temp = new String[length];
            int index = 0;
            for (int i = 0; i < len1; i++) {
                for (int j = 0; j < len2; j++) {
                    temp[index] = StringUtils.capitalize(strJaggedArray[0][i]) + StringUtils.capitalize(strJaggedArray[1][j]);
                    index++;
                }
            }
            String[][] newArray = new String[len - 1][];
            System.arraycopy(strJaggedArray, 2, newArray, 1, len - 2);
            newArray[0] = temp;
            return doExchange(newArray);
        } else {
            return strJaggedArray;
        }
    }

    /**
     * 字符串集合转换字符串(逗号分隔)
     *
     * @param set 字符串集合
     * @return 字符串
     */
    public static String join(Set<String> set) {
        return String.join(",", set);
    }

    /**
     * 判断字符是否为汉字
     *
     * @param character 字符
     * @return 是否为汉字
     */
    private static boolean isChinese(char character) {
        return String.valueOf(character).matches(CHINESE_PATTERN);
    }

}

