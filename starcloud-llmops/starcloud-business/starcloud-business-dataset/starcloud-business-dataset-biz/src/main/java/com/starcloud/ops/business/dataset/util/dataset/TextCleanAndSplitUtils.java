package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.pojo.dto.CommonCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.HTMLCleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.github.furstenheim.CopyDown;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    private static String processHtmlTags(String data, List<String> whiteRules, List<String> blackRules) {
        Document doc = Jsoup.parse(data);

        if(CollUtil.isNotEmpty(whiteRules)){
            String whiteRule = String.join(",", whiteRules);

            String whiteContent = doc.select(whiteRule).html();
            if (StrUtil.isBlank(whiteContent)){
                throw exception(11);
            }
            doc= Jsoup.parse(whiteContent);
        }

        if(CollUtil.isNotEmpty(blackRules)){
            String blackRule = String.join(",", blackRules);

            Elements select = doc.select(blackRule);
            for (Element element : select) {
                element.remove();
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


    public static void main(String[] args) throws IOException {
        // String URL="https://sell.amazon.com/pricing#referral-fees";
        String URL = "https://mp.weixin.qq.com/s?__biz=Mzg5NDk3NzQyNA==&mid=2247485169&idx=1&sn=488decf3d46394226d5d249e41f20a30&chksm=c016120ef7619b18c4d30b791f52d2f44c52c54ba9ffcd89163191f0130f87451eb4f1b686c6&scene=178&cur_album_id=2968967010405842946#rd";
        //
        Document document = Jsoup.connect(URL).get();

        Elements select = document.select("#js_content");

        System.out.println(document.html());


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

        // String weqeq = Jsoup.clean(document.html(), WHITELIST);

    }


}
