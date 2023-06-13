package cn.iocoder.yudao.module.starcloud.adapter.ruoyipro;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Scanner;

/**
 * @author nacoyer
 */
public class CodeGenerator {

    public static final String MODULAR_NAME = "starcloud-llmops/starcloud-adapter-ruoyi-pro";

    public static final String SRC_MAIN_JAVA = "src/main/java/";

    public static final String FILE_PATH = System.getProperty("user.dir") + "/" + MODULAR_NAME + "/" + SRC_MAIN_JAVA;

    public static final String JDBC_MYSQL_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/ruoyi-vue-pro?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=CONVERT_TO_NULL";

    public static final String JDBC_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";

    public static final String JDBC_USERNAME = "starcloud_dev";

    public static final String JDBC_PASSWORD = "Hellostarcloud2022";

    /**
     * 代码生成位置
     */
    public static final String PARENT_NAME = "cn.iocoder.yudao.module.starcloud.adapter.ruoyipro";


    public static final String MODULE = "app";


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }


    public static void main(String[] args) {

        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        // TODO 指定本机路径
        gc.setOutputDir(FILE_PATH);
        gc.setAuthor("admin");
        // 生成后是否打开资源管理器
        gc.setOpen(false);
        // 重新生成时文件是否覆盖
        gc.setFileOverride(true);
        // 去掉Service接口的首字母I
        gc.setServiceName("%sService");
        // 设置实体类后缀
        gc.setEntityName("%sDO");
        // 主键策略
        gc.setIdType(IdType.AUTO);
        // 定义生成的实体类中日期类型
        gc.setDateType(DateType.ONLY_DATE);
        // 是否开启Swagger2模式
        gc.setSwagger2(false);

        mpg.setGlobalConfig(gc);

        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(JDBC_MYSQL_URL);
        dsc.setDriverName(JDBC_DRIVER_NAME);
        dsc.setUsername(JDBC_USERNAME);
        dsc.setPassword(JDBC_PASSWORD);
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4、包配置
        PackageConfig pc = new PackageConfig();
        // TODO 包路径设置
        pc.setParent(PARENT_NAME);
        pc.setEntity("entity." + MODULE);
        pc.setMapper("mapper." + MODULE);
        mpg.setPackageInfo(pc);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        // 控制 不生成以下内容
        templateConfig.setController("");
        templateConfig.setService("");
        templateConfig.setServiceImpl("");
        templateConfig.setXml("");
        mpg.setTemplate(templateConfig);

        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        // 数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 生成实体时去掉表前缀
        strategy.setTablePrefix(pc.getModuleName() + "_");
        // 数据库表字段映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setEntityLombokModel(true);
        // restful api风格控制器
        strategy.setRestControllerStyle(true);
        // url中驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setLogicDeleteFieldName("deleted");
        strategy.setEntityTableFieldAnnotationEnable(true);
        mpg.setStrategy(strategy);

        // 6、执行
        mpg.execute();
    }
}
