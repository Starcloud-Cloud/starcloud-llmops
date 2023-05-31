package com.starcloud.ops.business.app.domain.handler;

import com.starcloud.ops.business.app.domain.entity.BaseStepEntity;
import org.springframework.stereotype.Component;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Component
public class OpenAiChatStepHandler extends BaseStepHandler {



   public void handle() {
      System.out.println("OpenAiChatStepHandler.handle");
   }
}
