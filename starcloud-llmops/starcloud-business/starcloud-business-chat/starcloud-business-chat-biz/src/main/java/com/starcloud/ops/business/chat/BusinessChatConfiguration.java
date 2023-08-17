package com.starcloud.ops.business.chat;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.starcloud.ops.business.chat.worktool")
public class BusinessChatConfiguration {

}
