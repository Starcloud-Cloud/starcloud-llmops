package cn.iocoder.yudao.module.starcloud.adapter.ruoyipro;


import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import cn.iocoder.yudao.framework.tenant.config.TenantProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@MapperScan(value = "com.starcloud.ops", annotationClass = Mapper.class, lazyInitialization = "false")
public class AdapterRuoyiProConfiguration implements ApplicationListener<ApplicationStartedEvent> {


    @Autowired
    private TenantProperties tenantProperties;

    /**
     * swagger url: v3/api-docs/starcloud-llm
     */
    @Bean
    public GroupedOpenApi businessAppGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("starcloud-llm", "/llm");
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        tenantProperties.getIgnoreUrls().add("/app-api/llm/**");

    }

}
