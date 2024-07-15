package com.starcloud.ops.business.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import com.ql.util.express.*;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.instruction.op.OperatorBase;
import com.starcloud.ops.BaseUserContextTest;
import com.starcloud.ops.business.app.controller.admin.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.request.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.QLExpressUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 多step功能执行测试
 */
@Slf4j
public class WorkflowV2Test extends BaseUserContextTest {

//    @Autowired
//    private StoryEngine storyEngine;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedisMQTemplate redisMQTemplate;

    final String appId = "appId-test";


    @Test
    public void testRunTest() {


        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();

        executeReqVO.setAppUid("77a466b2e70f48f9b61910105f5db0f7");
        executeReqVO.setUserId(186L);
        executeReqVO.setAppReqVO(new AppReqVO());
        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        executeReqVO.setTenantId(2L);

        SseEmitter emitter = new SseEmitter(60000L);

        //executeReqVO.setSseEmitter(emitter);

        AppEntity app = AppFactory.factoryApp(executeReqVO.getAppUid());

        app.execute(executeReqVO);

    }


    @Test
    public void str2JsonSchema() {

        String json = "{\n" +
                "  \"$schema\" : \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"type\" : \"array\",\n" +
                "  \"items\" : {\n" +
                "    \"type\" : \"object\",\n" +
                "    \"properties\" : {\n" +
                "      \"id\" : {\n" +
                "        \"type\" : \"string\",\n" +
                "        \"description\" : \"图片模板ID\"\n" +
                "      },\n" +
                "      \"index\" : {\n" +
                "        \"type\" : \"integer\",\n" +
                "        \"description\" : \"图片序号\"\n" +
                "      },\n" +
                "      \"isMain\" : {\n" +
                "        \"type\" : \"boolean\",\n" +
                "        \"description\" : \"是否是主图\"\n" +
                "      },\n" +
                "      \"name\" : {\n" +
                "        \"type\" : \"string\",\n" +
                "        \"description\" : \"图片模板名称\"\n" +
                "      },\n" +
                "      \"url\" : {\n" +
                "        \"type\" : \"string\",\n" +
                "        \"description\" : \"海报图片地址\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonSchemaUtils.str2JsonSchema(json);

    }


    @Test
    public void spelTest() {

        SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null, true, true, 1);

        //SpelParserConfiguration config = new SpelParserConfiguration(true, true);


        // 创建spel表达式分析器
        ExpressionParser parser = new SpelExpressionParser(config);


        ParserContext parserContext = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return true;
            }

            @Override
            public String getExpressionPrefix() {
                return "{{";
            }

            @Override
            public String getExpressionSuffix() {
                return "}}";
            }
        };


        HashMap<String, Object> rootMap = new HashMap<>();


        BookListCreativeMaterialDTO bookList = new BookListCreativeMaterialDTO();
        bookList.setBookName("nnn");
        bookList.setAuthor("authtrr");


        SC sc = new SC();
        sc.setDocs(Arrays.asList(
                bookList,
                bookList,
                bookList,
                bookList

        ));


        rootMap.put("素材", sc);


        rootMap.put("内容生成", new HashMap() {{
            put("data", "title13343434");
        }});

        rootMap.put("内容生成2", new HashMap() {{
            put("data", "[1,2,3]");
        }});

        rootMap.put("内容生成4", new HashMap() {{
            put("data", "{\"dsd\": 12, \"tt\": 343}");
        }});


        rootMap.put("内容生成5", bookList);


        Root root = new Root();

        root.setSC(sc);


        StandardEvaluationContext context = new StandardEvaluationContext(root);
        //context.setVariable("内容生成.data", "3333");

        // 输入表达式
        Expression exp = parser.parseExpression("测试：{{SC.docs[1].author}}", parserContext);
        // 获取表达式的输出结果，getValue入参是返回参数的类型
        Object value = exp.getValue(context);

        log.info("out: {}", value);

    }


    @Test
    public void qlTest() {


        ExpressRunner runner = new ExpressRunner();

        HashMap<String, Object> rootMap = new HashMap<>();

        rootMap.put("a", 1);
        rootMap.put("b", 2);
        rootMap.put("c", 3);

        BookListCreativeMaterialDTO bookList = new BookListCreativeMaterialDTO();
        bookList.setBookName("nnn");
        bookList.setAuthor("authtrr");


        SC sc = new SC();
        sc.setDocs(Arrays.asList(
                bookList,
                bookList,
                bookList,
                bookList

        ));


        String json = "{\n" +
                "\t\"fff\": 12,\n" +
                "\t\"zzz\": \"dsd\"\n" +
                "}";

        JSONObject jsonObject = JSONUtil.parseObj(json);

        //JsonNode jsonNode = JsonSchemaUtils.str2JsonNode(json);

        rootMap.put("素材2", jsonObject);

        rootMap.put("素材", sc);


        rootMap.put("内容生成", new HashMap() {{
            put("data", "title13343434");
        }});

        rootMap.put("内容生成2", new HashMap() {{
            put("sss", "[1,  2,    3]");
            put("list", Arrays.asList(6, 7, 8));
        }});

        rootMap.put("内容生成4", new HashMap() {{
            put("data", "{\"dsd\": 12, \"tt\": 343}");
        }});


        rootMap.put("内容生成5", bookList);


        try {


            // setSecureMethods 设置方式
            Set<String> secureMethods = new HashSet<>();
            secureMethods.add("SC");

            //QLExpressRunStrategy.setSecureMethods(secureMethods);

            //QLExpressRunStrategy.setSandBoxMode(true);
            QLExpressRunStrategy.setMaxArrLength(50);

//            runner.addOperator("join", new JoinOperator());
//
//            runner.replaceOperator("+", new JoinOperator());

            //runner.addFunction("join", new JoinOperator());

            runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
            //runner.addOperatorWithAlias();

            ListOperator listOperator = new ListOperator();
            runner.addClassMethod("list", List.class, listOperator);

            //添加类的属性字段
            //runner.addClassField(String field, Class<?>bindingClass, Class<?>returnType, Operator op);

            //添加类的方法
            //runner.addClassMethod(String name, Class<?>bindingClass, OperatorBase op);

            String content = "体育; 1 join 2 join 3";

            runner.addMacro("飞机", "素材.docs[1].author");

            content = "素材.docs.list(author)";

            content = "素材.docs[1].author";

            content = "素材.docs.list('author')";

            content = "内容生成2.sss";

            content = "内容生成2.list";

            content = "内容生成4.data";

            content = "内容生成5.bookName";

            content = "String[] ds = {内容生成5.bookName, 内容生成5.bookName}; return ds;";

            content = "[内容生成5.bookName, 素材.docs.list('author'), 素材.docs[1].author, 内容生成4.data]";

            content = "sdsd{{内容生成5.bookName}} ++ {{素材.docs.list('author')}} sdas";

            content = "{{素材2.zzz}}";
           // content = "{{素材2.get('zzz')}}";

//            String vars[] = runner.getOutVarNames(content);
//
//            log.info("vars:{}", vars);


            Object r = QLExpressUtils.execute(content, rootMap);

            //Object r = runner.execute(content, rootMap, null, false, false);
//
            log.info("data:{}", r);
            log.info("data type: {}", r.getClass());

        } catch (Exception e) {


            log.error(e.getMessage(), e);
        }

    }

    @Test
    public void mainTest() {
        String input = "sdsdsd{{变量1}}sdsdsd {{变量2}}sdsdsd {{变量3}}sdsdsdsd";


        // 定义正则表达式
        String regex = "\\{\\{\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();


        while (matcher.find()) {
            String variable = matcher.group(1);
            System.out.println("Found variable: " + variable);
        }


        matcher.reset();

        // 找到所有匹配项并提取变量名
        while (matcher.find()) {
            String variable = matcher.group(1);

            matcher.appendReplacement(result, "new+" + variable);
        }

        //matcher.appendTail(result);

        log.info("rr: {}", result.toString());
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


    @Data
    public static class SC {

        private List<Object> docs;
    }

    @Data
    public static class Root {

        private SC SC;

        private String TTT = "333";

        private HashMap STEP = new HashMap<String, Object>() {{
            put("开头._OUT", "outttt");

            put("段落._OUT", "tttt\n xxxxx\n 4444444\n");

            put("段落._DATA", new ArrayList<String>() {{
                add("tttt");
                add("xxxxx");
                add("4444444 #{STEP['段落._DATA'][0]}");

            }});

            put("开头", new HashMap<String, Object>() {{
                put("key1", "vvv");
                put("key2", new HashMap() {{
                    put("xxx", "123");
                    put("_OUT", 77);
                }});
            }});

            put("段落", Arrays.asList(

                    new HashMap() {{
                        put("title", "title1");
                        put("content", "content1");
                    }},
                    new HashMap() {{
                        put("title", "title2");
                        put("content", "content2");
                    }},
                    new HashMap() {{
                        put("title", "title3");
                        put("content", "content3");
                    }}
            ));

        }};

    }

}
