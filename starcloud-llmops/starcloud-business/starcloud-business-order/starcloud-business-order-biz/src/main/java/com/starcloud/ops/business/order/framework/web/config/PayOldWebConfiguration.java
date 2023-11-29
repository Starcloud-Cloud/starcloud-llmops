package com.starcloud.ops.business.order.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * pay 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class PayOldWebConfiguration {

    /**
     * pay 模块的 API 分组
     */
    @Bean(name ="payGroupedOpenApi_old" )
    public GroupedOpenApi payGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("pay");
    }

}
