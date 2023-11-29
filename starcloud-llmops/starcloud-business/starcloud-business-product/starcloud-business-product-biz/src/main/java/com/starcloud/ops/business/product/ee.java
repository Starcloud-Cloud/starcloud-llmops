package com.starcloud.ops.business.product;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.product
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2023/11/29  11:49
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2023/11/29   AlanCusack    1.0         1.0 Version
 */
@Configuration
@Order(1)
public class ee implements ApplicationContextAware {

    @PostConstruct
    public void  ii(){
        System.out.println("rrrrrrrrrrrrrrrrrrr");
    }

    /**
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("rrrrrrrrrrrrrrrrrrr");
    }
}
