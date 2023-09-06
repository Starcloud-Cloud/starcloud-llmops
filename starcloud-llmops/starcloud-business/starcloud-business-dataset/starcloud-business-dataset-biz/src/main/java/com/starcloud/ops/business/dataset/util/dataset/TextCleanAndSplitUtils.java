package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.pojo.dto.BlackCleanVO;
import com.starcloud.ops.business.dataset.pojo.dto.CommonCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.HTMLCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.github.furstenheim.CopyDown;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.FILE_TYPE_NOT_ALLOW;

public class TextCleanAndSplitUtils {


    public static Safelist WHITELIST = new Safelist()

            .addTags(
                    "a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                    "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                    "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
                    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
                    "ul")


            .addAttributes("a", "href", "title")
            .addAttributes(":all", "js-content")
            .addAttributes("blockquote", "cite")
            .addAttributes("col", "span", "width")
            .addAttributes("colgroup", "span", "width")
            .addAttributes("img", "align", "alt", "height", "src", "title", "width")
            .addAttributes("ol", "start", "type")
            .addAttributes("q", "cite")
            .addAttributes("table", "summary", "width")
            .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
            .addAttributes(
                    "th", "abbr", "axis", "colspan", "rowspan", "scope",
                    "width")
            .addAttributes("ul", "type")

            .addProtocols("a", "href", "ftp", "http", "https", "mailto")
            .addProtocols("blockquote", "cite", "http", "https")
            .addProtocols("cite", "cite", "http", "https")
            .addProtocols("img", "src", "http", "https")
            .addProtocols("q", "cite", "http", "https");
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
        } catch (Exception e) {
            throw new RuntimeException("数据预处理失败，无法请求到地址！");
        }
        // 根据标签白名单和黑名单清洗数据
        if (CollUtil.isNotEmpty(htmlCleanRule.getWhiteList()) || htmlCleanRule.getBlackList() != null) {
            text = processHtmlTags(text, htmlCleanRule.getWhiteList(), htmlCleanRule.getBlackList());
        }

        return processFormat(text, htmlCleanRule.getConvertFormat());
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

        // // 用户自定义 正则
        // if (StringUtils.isNotBlank(splitRule.getPattern())) {
        //     Pattern line = Pattern.compile(splitRule.getPattern());
        //     text = line.matcher(text).replaceAll(StringUtils.EMPTY);
        // }
        // // 去除 空格
        // if (BooleanUtils.isNotFalse(splitRule.getAutomatic())) {
        //     text = removeUrlsEmails(text);
        //     return text;
        // }
        // // 去除 空白区域
        // if (BooleanUtils.isTrue(splitRule.getRemoveExtraSpaces())) {
        //     text = removeExtraSpaces(text);
        // }
        // // 去除 链接中的邮箱
        // if (BooleanUtils.isTrue(splitRule.getRemoveUrlsEmails())) {
        //     text = removeUrlsEmails(text);
        // }
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
    private static String processHtmlTags(String data, List<String> whiteRules, BlackCleanVO blackRules) {
        Document doc = Jsoup.parse(data);

        try {
            // 处理黑名单
            if (blackRules != null) {
                String blackRule = String.join(",", blackRules.getTags());
                WHITELIST.removeTags(blackRule);

                for (Map<String, String> map : blackRules.getAttributes()) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        WHITELIST.removeAttributes(entry.getKey(), entry.getValue());
                    }
                }
            }

            String cleanedHtml;

            if (CollUtil.isEmpty(whiteRules)) {
                // 白名单为空，直接使用全局白名单清理
                cleanedHtml = Jsoup.clean(doc.html(), WHITELIST);
            } else {
                // 处理白名单
                String whiteRule = String.join(",", whiteRules);
                Elements whiteText = doc.select(whiteRule);

                if (StrUtil.isBlank(whiteText.html())) {
                    throw new RuntimeException("数据预处理异常，网页白名单规则错误，无法预处理数据");
                }

                cleanedHtml = Jsoup.clean(whiteText.html(), WHITELIST);
            }

            if (StrUtil.isBlank(cleanedHtml)) {
                throw new RuntimeException("数据预处理异常，清理后的数据为空");
            }

            return cleanedHtml;
        } catch (RuntimeException e) {
            throw new RuntimeException("数据预处理异常：" + e.getMessage());
        }
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


    public static void main(String[] args) throws IOException {
        // String URL="https://sell.amazon.com/pricing#referral-fees";
        String URL = "https://mp.weixin.qq.com/s?__biz=Mzg5NDk3NzQyNA==&mid=2247485169&idx=1&sn=488decf3d46394226d5d249e41f20a30&chksm=c016120ef7619b18c4d30b791f52d2f44c52c54ba9ffcd89163191f0130f87451eb4f1b686c6&scene=178&cur_album_id=2968967010405842946#rd";
        //
        Document document = Jsoup.connect(URL).get();
        // String basic = Jsoup.clean(document.html(), Safelist.basic());
        // String basicWithImages = Jsoup.clean(document.html(), Safelist.basicWithImages());
        // String simpleText = Jsoup.clean(document.html(), Safelist.simpleText());
        // String relaxed = Jsoup.clean(document.html(), Safelist.relaxed());
        // String whitelist = Jsoup.clean(document.html(),WHITELIST);
        // String basicMark = html2Markdown(basic);
        // String basicWithImagesMark = html2Markdown(basicWithImages);
        // String simpleTextMark = html2Markdown(simpleText);
        // String relaxedMark = html2Markdown(relaxed);
        // String whitelistMark = html2Markdown(whitelist);
        // System.out.println(whitelistMark);

        String weqeq = Jsoup.clean(document.html(), WHITELIST);
        System.out.println(weqeq);

    }


}
