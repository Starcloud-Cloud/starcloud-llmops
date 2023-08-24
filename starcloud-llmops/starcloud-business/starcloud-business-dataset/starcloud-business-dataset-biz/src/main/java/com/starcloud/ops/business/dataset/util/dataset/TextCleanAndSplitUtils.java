package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.URLUtil;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.pojo.dto.CommonCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.HTMLCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.github.furstenheim.CopyDown;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.FILE_TYPE_NOT_ALLOW;

public class TextCleanAndSplitUtils {

    public static final List<String> ALLOWTYPE = Arrays.asList("TXT", "PDF");

    public static String splitText(String text, SplitRule splitRule, String type) {
        if (!ALLOWTYPE.contains(type.toUpperCase())) {
            throw exception(FILE_TYPE_NOT_ALLOW);
        }
        return splitText(text, splitRule);
    }

    public static String processHtmlRule(String data, HTMLCleanRule htmlCleanRule) {
        String text;
        // 根据用户设置语言设置
        try {
            String normalize = URLUtil.normalize(data);

            Connection connection = Jsoup.connect(normalize);

            // 设置请求头中的 Accept-Language 属性

            connection.header("Accept-Language", htmlCleanRule.getAcceptLanguage());

             text = connection.get().toString();
        }catch (Exception e){
            throw new RuntimeException("数据预处理失败，无法请求到地址！");
        }
        // 根据标签白名单和黑名单清洗数据
        if (CollUtil.isNotEmpty(htmlCleanRule.getWhiteList()) || CollUtil.isNotEmpty(htmlCleanRule.getBlackList())) {
            text = processHtmlTags(text, htmlCleanRule.getWhiteList(), htmlCleanRule.getBlackList());
        }

        return processFormat(text,htmlCleanRule.getConvertFormat());
    }

    public static String processCommonRule(String text, CommonCleanRule commonCleanRule) {
        Document doc = Jsoup.parse(text);
        StringBuilder processedText = new StringBuilder(doc.text());

        if (commonCleanRule.getRemoveAllHtmlTags()) {
            processedText = new StringBuilder(doc.text());
        }

        if (commonCleanRule.getRemoveAllImage()) {
            String imgRegex = "<img.*?>";
            Pattern imgPattern = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = imgPattern.matcher(processedText);
            processedText = new StringBuilder(matcher.replaceAll(""));
        }

        if (commonCleanRule.getRemoveConsecutiveSpaces()) {
            processedText = new StringBuilder(processedText.toString().replaceAll("\\s+", " "));
        }

        if (commonCleanRule.getRemoveConsecutiveNewlines()) {
            processedText = new StringBuilder(processedText.toString().replaceAll("\\n+", "\n"));
        }

        if (commonCleanRule.getRemoveConsecutiveTabs()) {
            processedText = new StringBuilder(processedText.toString().replaceAll("\\t+", "\t"));
        }

        if (commonCleanRule.getRemoveUrlsEmails()) {
            String emailRegex = "([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)";
            processedText = new StringBuilder(processedText.toString().replaceAll(emailRegex, ""));
        }

        return processedText.toString();
    }


    public static String splitText(String text, SplitRule splitRule) {

        // 用户自定义 正则
        if (StringUtils.isNotBlank(splitRule.getPattern())) {
            Pattern line = Pattern.compile(splitRule.getPattern());
            text = line.matcher(text).replaceAll(StringUtils.EMPTY);
        }
        // 去除 空格
        if (BooleanUtils.isNotFalse(splitRule.getAutomatic())) {
            text = removeUrlsEmails(text);
            return text;
        }
        // 去除 空白区域
        if (BooleanUtils.isTrue(splitRule.getRemoveExtraSpaces())) {
            text = removeExtraSpaces(text);
        }
        // 去除 链接中的邮箱
        if (BooleanUtils.isTrue(splitRule.getRemoveUrlsEmails())) {
            text = removeUrlsEmails(text);
        }
        return text;
    }


    /**
     * 根据标签白名单和黑名单清洗数据
     *
     * @param data
     * @param whiteRules
     * @param blackRules
     * @return
     */
    private static String processHtmlTags(String data, List<String> whiteRules, List<String> blackRules) {


        Document doc = Jsoup.parse(data);

        if (CollUtil.isEmpty(whiteRules)) {
            // 处理黑名单，直接移除指定标签内容
            if (CollUtil.isNotEmpty(blackRules)) {
                String blackRule = String.join(",", blackRules);
                doc.select(blackRule).remove();
            }
        } else {
            String whiteRule = String.join(",", whiteRules);

            if (CollUtil.isNotEmpty(blackRules)) {
                String blackRule = String.join(",", blackRules);
                doc.select(whiteRule).select(blackRule).remove();
            } else {
                // 只处理白名单
                Elements whiteText = doc.select(whiteRule);
                doc.body().html(whiteText.html());
            }
        }

        return doc.html();

    }

    /**
     * 根据用户定义的转换格式存储清洗后的数据
     *
     * @param data
     * @param format
     * @return
     */
    private static String processFormat(String data, String format) {

        if (DataSourceDataFormatEnum.MARKDOWN.name().equals(format)) {
            return html2Markdown(data);
        }
        if (DataSourceDataFormatEnum.TXT.name().equals(format)) {
            return Jsoup.parse(data).text();
        }
        return data;
    }


    private static String html2Markdown(String html) {
        CopyDown converter = new CopyDown();
        return converter.convert(html);
    }


    private static String removeExtraSpaces(String text) {
        Pattern line = Pattern.compile("\n{2,}");
        text = line.matcher(text).replaceAll(StringUtils.LF);
        Pattern space = Pattern.compile("[\t\f\r\\x20\u00a0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]{2,}");
        return space.matcher(text).replaceAll(StringUtils.SPACE);
    }

    private static String removeUrlsEmails(String text) {
        Pattern email = Pattern.compile("([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)");
        return email.matcher(text).replaceAll(StringUtils.EMPTY);
    }


}
