package com.starcloud.ops.business.app.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ql.util.express.*;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.instruction.op.OperatorBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;

import java.util.*;
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

        //对List增加扩展，支持  array.list('key') 方法
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

        DefaultContext<String, Object> rootMap = new DefaultContext<>();

        rootMap.putAll(params);

        return execute(content, rootMap);
    }


    /**
     * 把字符串中的占位符进行 QLExpress 的批量替换
     *
     * @param content
     * @param rootMap
     * @return
     */
    public static Object execute(String content, DefaultContext<String, Object> rootMap) {

        try {

            // 定义正则表达式
            Pattern pattern = Pattern.compile(MATCH_REGEX);
            Matcher matcher = pattern.matcher(content);

            StringBuffer varsBuffer = new StringBuffer();
            while (matcher.find()) {
                String variable = matcher.group(1);
                String vars = (String) executeNative(variable, rootMap);

                if (vars != null) {
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

            log.error("QLExpressUtils.execute is fail: {}, content: {}", e.getMessage(), content);

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

    /**
     * 定义一个继承自com.ql.util.express.Operator的操作符
     * <p>
     * 多字段支持 docs.list('author','bookName')
     */
    public static class ListOperator extends OperatorBase {

        @Override
        public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {

            OperateData docs = list.get(0);

            int length = list.length;
            List<String> fieldList = new ArrayList<>(length - 1);
            for (int i = 1; i < length; i++) {
                fieldList.add((String) list.get(i).getObject(parent));
            }

            log.info("list: {}", list);

            Object ddocs = docs.getObject(parent);

            List<Object> result = new ArrayList<>();
            if (ddocs instanceof List) {
                List<Object> fdocs = (List<Object>) ddocs;

                for (int i = 0; i < fdocs.size(); i++) {
                    Object doc = fdocs.get(i);
                    StringJoiner sj = new StringJoiner("-");
                    for (String field : fieldList) {
                        Object fieldValue = BeanUtil.getFieldValue(doc, field);
                        if (Objects.nonNull(fieldValue)) {
                            sj.add(fieldValue.toString());
                        }
                    }
                    result.add(sj);
                }
                return new OperateData(StrUtil.join("\r\n", result), String.class);
            }

            return null;
        }
    }

}
