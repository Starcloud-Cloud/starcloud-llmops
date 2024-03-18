package com.starcloud.ops.business.app.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.ql.util.express.*;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.instruction.op.OperatorBase;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class QLExpressUtils {

    private static ExpressRunner runner;

//    private static String expressionPrefix = "{{";
//
//    private static String expressionSuffix = "}}";
//

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
            String regex = "\\{\\{(.*?)\\}\\}";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);

            StringBuffer varsBuffer = new StringBuffer();
            while (matcher.find()) {
                String variable = matcher.group(1);

                String map = "map.put(\"" + variable + "\", " + variable + ");";
                varsBuffer.append(map);
            }
            //没有找到占位符，返回原内容
            if (varsBuffer.length() <= 0) {
                return content;
            }

            //把找的占位符 拼接为  QLExpress 支持的Map结构
            String qlMap = "map = new HashMap();";
            String varStr = qlMap + varsBuffer.toString() + "return map;";

            //获取 替换占位符后的变量数组结果
            Object vars = executeNative(varStr, rootMap);


            if (vars instanceof HashMap) {
                matcher.reset();
                StringBuffer sb = new StringBuffer();

                HashMap replacements = (HashMap) vars;

                while (matcher.find()) {
                    String placeholder = matcher.group(1);
                    String replacement = (String) replacements.get(placeholder);

                    if (replacement != null) {
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                    }
                }
                matcher.appendTail(sb);

                return sb.toString();
            }

        } catch (Exception e) {

            log.error("QLExpressUtils.execute is fail: {}", e.getMessage(), e);
        }

        return content;
    }


    private static Object executeNative(String content, DefaultContext<String, Object> rootMap) {

        try {

            Object r = runner.execute(content, rootMap, null, false, false);

            return r;
        } catch (Exception e) {

            log.error("QLExpressUtils.execute is fail: {}", e.getMessage(), e);

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
     */
    public static class ListOperator extends OperatorBase {

        @Override
        public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {

            OperateData docs = list.get(0);
            OperateData field = list.get(1);

            log.info("list: {}", list);

            Object ddocs = docs.getObject(parent);

            List<Object> result = new ArrayList<>();
            if (ddocs instanceof List) {
                List<Object> fdocs = (List<Object>) ddocs;

                for (int i = 0; i < fdocs.size(); i++) {
                    Object fieldValue = BeanUtil.getFieldValue(fdocs.get(i), (String) field.getObject(parent));
                    result.add(fieldValue);
                }

                return new OperateData(StrUtil.join("\r\n", result), String.class);
            }

            return null;
        }
    }

}
