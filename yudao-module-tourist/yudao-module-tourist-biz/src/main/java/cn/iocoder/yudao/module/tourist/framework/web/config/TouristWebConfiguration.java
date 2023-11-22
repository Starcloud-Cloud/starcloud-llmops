package cn.iocoder.yudao.module.tourist.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tourist 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class TouristWebConfiguration {

    /**
     * member 模块的 API 分组
     */
    @Bean(name = "touristGroupedOpenApi2")
    public GroupedOpenApi memberGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("member");
    }

}
