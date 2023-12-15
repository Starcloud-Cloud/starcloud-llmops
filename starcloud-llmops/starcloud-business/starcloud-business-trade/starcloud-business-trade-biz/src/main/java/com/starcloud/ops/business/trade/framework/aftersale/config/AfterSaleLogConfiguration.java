package com.starcloud.ops.business.trade.framework.aftersale.config;

import com.starcloud.ops.business.trade.framework.aftersale.core.aop.AfterSaleLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO @chenchen：改成 aftersale 好点哈；
/**
 * trade 模块的 afterSaleLog 组件的 Configuration
 *
 * @author 陈賝
 * @since 2023/6/18 11:09
 */
@Configuration(proxyBeanMethods = false)
public class AfterSaleLogConfiguration {

    @Bean
    public AfterSaleLogAspect afterSaleLogAspect() {
        return new AfterSaleLogAspect();
    }

}
