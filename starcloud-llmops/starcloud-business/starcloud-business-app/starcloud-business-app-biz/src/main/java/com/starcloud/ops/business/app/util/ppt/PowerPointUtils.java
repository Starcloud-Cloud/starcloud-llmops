package com.starcloud.ops.business.app.util.ppt;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
public class PowerPointUtils {

    /**
     * 占位符前缀
     */
    private static final String PLACEHOLDER_PREFIX = "${";

    /**
     * 占位符后缀
     */
    private static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * 占位符正则表达式
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)}");

    /**
     * 单词占位符正则表达式
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("word\\d+");

    /**
     * 翻译占位符正则表达式
     */
    private static final Pattern TRANSLATION_PATTERN = Pattern.compile("translation\\d+");

    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

    private static final Map<String, Object> REPLACEMENTS = new HashMap<>();

    private static final List<ReplaceObject> REPLACE_OBJECTS = new ArrayList<>();

    static {
        REPLACEMENTS.put("word1", "apple");
        REPLACEMENTS.put("word2", "banana");
        REPLACEMENTS.put("word3", "cherry");
        REPLACEMENTS.put("word4", "durian");
        REPLACEMENTS.put("word5", "elderberry");
        REPLACEMENTS.put("word6", "fig");
        REPLACEMENTS.put("word7", "grape");
        REPLACEMENTS.put("word8", "honeydew");
        REPLACEMENTS.put("word9", "jackfruit");
        REPLACEMENTS.put("word10", "kiwi");
        REPLACEMENTS.put("translation1", "苹果");
        REPLACEMENTS.put("translation2", "香蕉");
        REPLACEMENTS.put("translation3", "樱桃");
        REPLACEMENTS.put("translation4", "榴莲");
        REPLACEMENTS.put("translation5", "黑莓");
        REPLACEMENTS.put("translation6", "无花果");
        REPLACEMENTS.put("translation7", "葡萄");
        REPLACEMENTS.put("translation8", "猕猴桃");
        REPLACEMENTS.put("translation9", "榴莲");
        REPLACEMENTS.put("translation10", "奇异果");

        REPLACE_OBJECTS.add(ReplaceObject.of("apple", "苹果", 1));
        REPLACE_OBJECTS.add(ReplaceObject.of("banana", "香蕉", 2));
        REPLACE_OBJECTS.add(ReplaceObject.of("cherry", "樱桃", 3));
        REPLACE_OBJECTS.add(ReplaceObject.of("durian", "榴莲", 4));
        REPLACE_OBJECTS.add(ReplaceObject.of("elderberry", "黑莓", 5));
        REPLACE_OBJECTS.add(ReplaceObject.of("fig", "无花果", 6));
        REPLACE_OBJECTS.add(ReplaceObject.of("grape", "葡萄", 7));
        REPLACE_OBJECTS.add(ReplaceObject.of("honeydew", "猕猴桃", 8));
        REPLACE_OBJECTS.add(ReplaceObject.of("jackfruit", "榴莲", 9));
        REPLACE_OBJECTS.add(ReplaceObject.of("kiwi", "奇异果", 10));
        REPLACE_OBJECTS.add(ReplaceObject.of("lemon", "柠檬", 11));
        REPLACE_OBJECTS.add(ReplaceObject.of("mango", "芒果", 12));
        REPLACE_OBJECTS.add(ReplaceObject.of("nectarine", "油桃", 13));
        REPLACE_OBJECTS.add(ReplaceObject.of("orange", "橙子", 14));
        REPLACE_OBJECTS.add(ReplaceObject.of("papaya", "木瓜", 15));
        REPLACE_OBJECTS.add(ReplaceObject.of("quince", "榅桲", 16));
        REPLACE_OBJECTS.add(ReplaceObject.of("raspberry", "覆盆子", 17));
        REPLACE_OBJECTS.add(ReplaceObject.of("strawberry", "草莓", 18));
        REPLACE_OBJECTS.add(ReplaceObject.of("tangerine", "橘子", 19));
        REPLACE_OBJECTS.add(ReplaceObject.of("ugli", "丑橘", 20));
        REPLACE_OBJECTS.add(ReplaceObject.of("vanilla", "香草", 21));
    }

    public static void main(String[] args) {
        replacePlaceholders("抗遗忘单词卡模板1.pptx", REPLACE_OBJECTS);
    }

    /**
     * 替换PPT中的占位符
     *
     * @param filePath     PPT文件路径
     * @param replacements 替换参数
     */
    public static void replacePlaceholders(String filePath, Map<String, Object> replacements) {
        ClassLoader classLoader = PowerPointUtils.class.getClassLoader();
        InputStream pptInputStream = null;
        FileOutputStream outputStream = null;
        try {
            pptInputStream = classLoader.getResourceAsStream(filePath);
            if (Objects.isNull(pptInputStream)) {
                throw new IllegalStateException("无法找到PPT模板文件");
            }
            // 读取PPT
            XMLSlideShow ppt = new XMLSlideShow(pptInputStream);
            replacePlaceholders(ppt, replacements);

            // 输出PPT
            outputStream = new FileOutputStream("output.pptx");
            ppt.write(outputStream);
        } catch (Exception exception) {
            log.error("替换PPT中的占位符失败", exception);
        } finally {
            IoUtil.close(pptInputStream);
            IoUtil.close(outputStream);
        }
    }

    public static void replacePlaceholders(String filePath, List<ReplaceObject> replaceObjects) {
        ClassLoader classLoader = PowerPointUtils.class.getClassLoader();
        InputStream pptInputStream = null;
        FileOutputStream outputStream = null;
        try {
            pptInputStream = classLoader.getResourceAsStream(filePath);

            if (Objects.isNull(pptInputStream)) {
                throw new IllegalStateException("无法找到PPT模板文件");
            }

            int replaceObjectMaxIndex = ReplaceObject.getReplaceObjectMaxIndex(replaceObjects);
            if (replaceObjectMaxIndex <= 0) {
                throw new IllegalStateException("替换对象列表为空");
            }

            // 读取PPT
            XMLSlideShow ppt = new XMLSlideShow(pptInputStream);
            int pptPlaceholderMaxCount = getPptPlaceholderMaxCount(ppt);
            if (pptPlaceholderMaxCount == -1) {
                throw new IllegalStateException("无法找到PPT中的占位符");
            }

            if (replaceObjectMaxIndex <= pptPlaceholderMaxCount) {
                // 如果替换参数的数量小于PPT中的占位符数量。则说明只有一张幻灯片
                replacePlaceholders(ppt, ReplaceObject.toMap(replaceObjects));
                // 将替换后的PPT转换成PDF
                outputStream = new FileOutputStream("output.pptx");
                ppt.write(outputStream);

            } else {
                int size = replaceObjects.size();
                int count = size / pptPlaceholderMaxCount;
                int remainder = size % pptPlaceholderMaxCount;
                int number = count + (remainder > 0 ? 1 : 0);
                List<XMLSlideShow> pptList = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    int start = i * pptPlaceholderMaxCount;
                    int end = Math.min((i + 1) * pptPlaceholderMaxCount, size);
                    List<ReplaceObject> subList = replaceObjects.subList(start, end);

                    for (int j = 0; j < subList.size(); j++) {
                        ReplaceObject replaceObject = subList.get(j);
                        replaceObject.setIndex(j + 1);
                    }
                    if (i == 0) {
                        replacePlaceholders(ppt, ReplaceObject.toMap(subList));
                        pptList.add(ppt);
                    } else {
                        InputStream cloneInputStream = classLoader.getResourceAsStream(filePath);
                        if (Objects.isNull(cloneInputStream)) {
                            throw new IllegalStateException("无法找到PPT模板文件");
                        }
                        try {
                            XMLSlideShow clone = new XMLSlideShow(cloneInputStream);
                            replacePlaceholders(clone, ReplaceObject.toMap(subList));
                            pptList.add(clone);
                        } finally {
                            IoUtil.close(cloneInputStream);
                        }
                    }
                }
                // 合并PPT
                XMLSlideShow merge = merge(pptList);
                // 将替换后的PPT转换成PDF
                outputStream = new FileOutputStream("output.pptx");
                merge.write(outputStream);
                merge.close();
            }
        } catch (Exception exception) {
            log.error("替换PPT中的占位符失败", exception);
        } finally {
            IoUtil.close(pptInputStream);
            IoUtil.close(outputStream);
        }
    }

    /**
     * 合并PPT
     *
     * @param pptList PPT列表
     * @return 合并后的PPT
     */
    public static XMLSlideShow merge(List<XMLSlideShow> pptList) {
        if (CollectionUtils.isEmpty(pptList)) {
            throw new IllegalArgumentException("PPT列表不能为空");
        }
        XMLSlideShow ppt = pptList.get(0);
        for (int i = 1; i < pptList.size(); i++) {
            XMLSlideShow merge = pptList.get(i);
            List<XSLFSlide> slides = merge.getSlides();
            for (XSLFSlide slide : slides) {
                ppt.createSlide().importContent(slide);
            }
        }
        return ppt;
    }

    /**
     * 替换PPT中的占位符
     *
     * @param pptx         PPT
     * @param replacements 替换参数
     */
    public static void replacePlaceholders(XMLSlideShow pptx, Map<String, Object> replacements) {
        // 1. 获取所有的幻灯片
        List<XSLFSlide> slideList = pptx.getSlides();
        if (CollectionUtils.isEmpty(slideList)) {
            return;
        }

        // 2. 循环处理幻灯片
        for (XSLFSlide slide : slideList) {
            // 获取幻灯片中的所有形状
            List<XSLFShape> shapes = slide.getShapes();
            if (CollectionUtils.isEmpty(shapes)) {
                continue;
            }
            // 获取所有的文本段落
            List<XSLFTextParagraph> paragraphList = new ArrayList<>();
            for (XSLFShape shape : shapes) {
                List<XSLFTextParagraph> textParagraphs = parseParagraph(shape);
                paragraphList.addAll(textParagraphs);
            }
            if (CollectionUtils.isEmpty(paragraphList)) {
                continue;
            }
            // 循环处理文本段落
            for (XSLFTextParagraph paragraph : paragraphList) {
                replaceTextParagraph(paragraph, replacements, -1);
            }

            // 获取幻灯片中的所有表格
            List<XSLFTable> tableList = getTableList(slide);
            if (CollectionUtils.isEmpty(tableList)) {
                continue;
            }
            for (XSLFTable table : tableList) {
                List<XSLFTableRow> rows = table.getRows();
                if (CollectionUtils.isEmpty(rows)) {
                    continue;
                }
                for (XSLFTableRow row : rows) {
                    List<XSLFTableCell> cellList = row.getCells();
                    if (CollectionUtils.isEmpty(cellList)) {
                        continue;
                    }
                    for (XSLFTableCell cell : cellList) {
                        List<XSLFTextParagraph> cellTextParagraphList = cell.getTextParagraphs();
                        if (CollectionUtils.isEmpty(cellTextParagraphList)) {
                            continue;
                        }
                        for (XSLFTextParagraph textParagraph : cellTextParagraphList) {
                            replaceTextParagraph(textParagraph, replacements, -1);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取PPT中的占位符最大数字
     *
     * @param pptx PPT
     * @return 占位符最大数字
     */
    public static int getPptPlaceholderMaxCount(XMLSlideShow pptx) {
        // 1. 获取所有的幻灯片
        List<XSLFSlide> slideList = pptx.getSlides();
        if (CollectionUtils.isEmpty(slideList)) {
            return -1;
        }

        // 2. 循环处理幻灯片
        int max = -1;
        for (XSLFSlide slide : slideList) {
            // 获取幻灯片中的所有形状
            List<XSLFShape> shapes = slide.getShapes();
            if (CollectionUtils.isEmpty(shapes)) {
                continue;
            }
            // 获取所有的文本段落
            List<XSLFTextParagraph> paragraphList = new ArrayList<>();
            for (XSLFShape shape : shapes) {
                List<XSLFTextParagraph> textParagraphs = parseParagraph(shape);
                paragraphList.addAll(textParagraphs);
            }
            if (CollectionUtils.isEmpty(paragraphList)) {
                continue;
            }
            // 循环处理文本段落
            for (XSLFTextParagraph paragraph : paragraphList) {
                int maxNumberFormTextParagraph = getMaxNumberFormTextParagraph(paragraph);
                if (maxNumberFormTextParagraph > max) {
                    max = maxNumberFormTextParagraph;
                }
            }

            // 获取幻灯片中的所有表格
            List<XSLFTable> tableList = getTableList(slide);
            if (CollectionUtils.isEmpty(tableList)) {
                continue;
            }
            for (XSLFTable table : tableList) {
                List<XSLFTableRow> rows = table.getRows();
                if (CollectionUtils.isEmpty(rows)) {
                    continue;
                }
                for (XSLFTableRow row : rows) {
                    List<XSLFTableCell> cellList = row.getCells();
                    if (CollectionUtils.isEmpty(cellList)) {
                        continue;
                    }
                    for (XSLFTableCell cell : cellList) {
                        List<XSLFTextParagraph> cellTextParagraphList = cell.getTextParagraphs();
                        if (CollectionUtils.isEmpty(cellTextParagraphList)) {
                            continue;
                        }
                        for (XSLFTextParagraph textParagraph : cellTextParagraphList) {
                            int maxNumberFormTextParagraph = getMaxNumberFormTextParagraph(textParagraph);
                            if (maxNumberFormTextParagraph > max) {
                                max = maxNumberFormTextParagraph;
                            }
                        }
                    }
                }
            }
        }
        return max;
    }

    /**
     * 从幻灯片中获取表格列表
     *
     * @param slide 幻灯片
     * @return 表格列表
     */
    public static List<XSLFTable> getTableList(XSLFSlide slide) {
        List<XSLFTable> tables = new ArrayList<>();
        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTable) {
                tables.add((XSLFTable) shape);
            }
        }
        return tables;
    }

    /**
     * 替换文本段落内的占位符
     *
     * @param paragraph 段落
     * @param paramMap  参数
     * @param index     替换位置索引
     */
    public static void replaceTextParagraph(XSLFTextParagraph paragraph, Map<String, Object> paramMap, int index) {
        String paraText = paragraph.getText();
        // 正则匹配，循环匹配替换
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(paraText);
        while (matcher.find()) {
            // 获取占位符起始位置所在run的索引
            int prefixIndex = getRunIndex(paragraph, PLACEHOLDER_PREFIX, index);
            if (prefixIndex < index) {
                return;
            }
            // 获取占位符结束位置所在run的索引
            int suffixIndex = getRunIndex(paragraph, PLACEHOLDER_SUFFIX, index);
            List<XSLFTextRun> textRuns = paragraph.getTextRuns();

            // 获取占位符的值
            String placeholder = matcher.group(0);
            // 如果没在 paramMap，则不做替换
            String replace = replace(placeholder, paramMap);
            XSLFTextRun textRun = textRuns.get(prefixIndex);
            setText(textRun, replace);
            // 存在 ${ 和 } 不在同一个CTRegularTextRun内的情况，将其他替换为空字符
            for (int i = prefixIndex + 1; i < suffixIndex + 1; i++) {
                setText(textRuns.get(i), StringUtils.EMPTY);
            }
            index = suffixIndex + 1;
        }
    }

    /**
     * 获取文本段落中占位符的最大数字
     *
     * @param paragraph 段落
     * @return 最大数字
     */
    public static int getMaxNumberFormTextParagraph(XSLFTextParagraph paragraph) {
        String paraText = paragraph.getText();
        // 正则匹配，循环匹配替换
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(paraText);
        int max = -1;
        while (matcher.find()) {
            int maxNumber = getMaxNumber(matcher.group(0));
            if (maxNumber > max) {
                max = maxNumber;
            }
        }
        return max;
    }

    /**
     * 获取一个 shape 下的所有文本段落
     *
     * @param shape shape
     * @return 文本段落列表
     */
    public static List<XSLFTextParagraph> parseParagraph(XSLFShape shape) {
        if (shape instanceof XSLFAutoShape) {
            XSLFAutoShape autoShape = (XSLFAutoShape) shape;
            return autoShape.getTextParagraphs();

        } else if (shape instanceof XSLFTextShape) {
            XSLFTextShape textShape = (XSLFTextShape) shape;
            return textShape.getTextParagraphs();

        } else if (shape instanceof XSLFFreeformShape) {
            XSLFFreeformShape freeformShape = (XSLFFreeformShape) shape;
            return freeformShape.getTextParagraphs();

        } else if (shape instanceof TextBox) {
            TextBox textBox = (TextBox) shape;
            return textBox.getTextParagraphs();
        }

        return new ArrayList<>();
    }

    /**
     * 设置run的值
     *
     * @param run  run
     * @param text run值
     */
    private static void setText(XSLFTextRun run, String text) {
        run.setText(text);
    }

    /**
     * 获取word在段落中出现第一次的run的索引
     *
     * @param paragraph 段落
     * @param word      目标值
     * @param index     索引
     */
    private static int getRunIndex(XSLFTextParagraph paragraph, String word, int index) {
        List<CTRegularTextRun> rList = paragraph.getXmlObject().getRList();
        for (int i = (Math.max(index, 0)); i < rList.size(); i++) {
            String text = rList.get(i).getT();
            if (text.contains(word)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 替换文本中的占位符
     *
     * @param text         待替换的文本
     * @param replacements 替换内容
     * @return 替换后的文本
     */
    private static String replace(String text, Map<String, Object> replacements) {
        StringLookup lookup = key -> replacements.getOrDefault(key, StringUtils.EMPTY).toString();
        StringSubstitutor stringSubstitutor = new StringSubstitutor(replacements);
        stringSubstitutor.setVariableResolver(lookup);
        return stringSubstitutor.replace(text);
    }

    /**
     * 获取文本中的最大数字
     *
     * @param text 文本
     * @return 最大数字
     */
    private static int getMaxNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return -1;
        }
        // 定义正则表达式匹配方括号中的数字
        Matcher matcher = PATTERN_NUMBER.matcher(text);
        // 如果匹配到，则返回最大的数字。
        int max = -1;
        try {
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group());
                if (number > max) {
                    max = number;
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return max;
    }

}
