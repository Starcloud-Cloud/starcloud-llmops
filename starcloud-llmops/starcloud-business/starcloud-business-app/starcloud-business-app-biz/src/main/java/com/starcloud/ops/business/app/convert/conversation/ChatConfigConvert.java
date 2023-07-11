package com.starcloud.ops.business.app.convert.conversation;

import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author starcloud
 */
@Mapper
public interface ChatConfigConvert {

    ChatConfigConvert INSTANCE = Mappers.getMapper(ChatConfigConvert.class);

    ChatOpenAI toOpenaiModel(OpenaiCompletionParams params);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateParams(OpenaiCompletionParams params, @MappingTarget ChatOpenAI chatOpenAi);
}
