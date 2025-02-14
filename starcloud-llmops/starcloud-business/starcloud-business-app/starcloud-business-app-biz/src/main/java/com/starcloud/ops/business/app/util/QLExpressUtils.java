package com.starcloud.ops.business.app.util;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.starcloud.ops.business.app.util.qlOperator.ListOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class QLExpressUtils {

    private static ExpressRunner runner;

//    private static String expressionPrefix = "{{";
//
//    private static String expressionSuffix = "}}";
//

    private static String MATCH_REGEX = "\\{\\{(.*?)\\}\\}";

    static {
        QLExpressUtils.runner = new ExpressRunner();
        QLExpressRunStrategy.setMaxArrLength(50);

        //对List增加扩展，支持  array.list('key1','key2', false) 方法
        ListOperator listOperator = new ListOperator();
        runner.addClassMethod("list", List.class, listOperator);

    }

    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @return
     */
    public static Boolean check(String content) {

        Pattern pattern = Pattern.compile(MATCH_REGEX);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }


    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @param params
     * @return
     */
    public static Object execute(String content, Map<String, Object> params) {

        return execute(content, params, true);
    }

    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @param params
     * @param defEmpty 占位符不存在是否返回空字符串
     * @return
     */
    public static Object execute(String content, Map<String, Object> params, Boolean defEmpty) {

        DefaultContext<String, Object> rootMap = new DefaultContext<>();

        rootMap.putAll(params);

        Object value = execute(content, rootMap, defEmpty);

        if (value instanceof String) {

            //判断是否有占位符结构，需要递归替换，实现不太好先这样
            if (QLExpressUtils.check((String) value)) {
                value = execute((String) value, rootMap, defEmpty);
            }
        }

        return value;
    }

    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @param rootMap
     * @return
     */
    private static Object execute(String content, DefaultContext<String, Object> rootMap, Boolean defEmpty) {

        try {

            // 定义正则表达式
            Pattern pattern = Pattern.compile(MATCH_REGEX);
            Matcher matcher = pattern.matcher(content);

            StringBuffer varsBuffer = new StringBuffer();
            while (matcher.find()) {
                String variable = matcher.group(1);
                String vars = String.valueOf(executeNative(variable, rootMap));

                if (!StringUtils.isBlank(vars) && !"null".equalsIgnoreCase(vars)) {
                    matcher.appendReplacement(varsBuffer, Matcher.quoteReplacement(vars));
                } else {
                    if (defEmpty) {
                        matcher.appendReplacement(varsBuffer, StringUtils.EMPTY);
                    }
                }
            }
            matcher.appendTail(varsBuffer);
            return varsBuffer.toString();

        } catch (Exception e) {

            log.error("QLExpressUtils.execute is fail ", e);
        }

        return content;
    }


    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @param rootMap
     * @return
     */
    private static Object execute(String content, DefaultContext<String, Object> rootMap) {

        try {

            // 定义正则表达式
            Pattern pattern = Pattern.compile(MATCH_REGEX);
            Matcher matcher = pattern.matcher(content);

            StringBuffer varsBuffer = new StringBuffer();
            while (matcher.find()) {
                String variable = matcher.group(1);
                String vars = String.valueOf(executeNative(variable, rootMap));

                if (!StringUtils.isBlank(vars) && !"null".equalsIgnoreCase(vars)) {
                    matcher.appendReplacement(varsBuffer, Matcher.quoteReplacement(vars));
                } else {
                    matcher.appendReplacement(varsBuffer, StringUtils.EMPTY);
                }
            }
            matcher.appendTail(varsBuffer);
            return varsBuffer.toString();

        } catch (Exception e) {

            log.error("QLExpressUtils.execute is fail: {}. content: {}", e.getMessage(), content);
        }

        return content;
    }


    private static Object executeNative(String content, DefaultContext<String, Object> rootMap) {

        try {

            Object r = runner.execute(content, rootMap, null, false, false);

            return r;
        } catch (Exception e) {

            log.error("QLExpressUtils.executeNative is fail content: {}, {}", content, e.getMessage());

        }

        return null;

    }


    public static String[] getOutVarNames(String content, DefaultContext<String, Object> rootMap) {

        try {

            String vars[] = runner.getOutVarNames(content);

            log.info("vars:{}", vars);

            return vars;

        } catch (Exception e) {

            log.error("QLExpressUtils.getOutVarNames is fail: {}", e.getMessage(), e);
        }

        return null;
    }


}
