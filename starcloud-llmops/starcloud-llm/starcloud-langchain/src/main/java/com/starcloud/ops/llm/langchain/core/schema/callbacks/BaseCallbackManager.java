package com.starcloud.ops.llm.langchain.core.schema.callbacks;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
public abstract class BaseCallbackManager implements BaseCallbackHandler {


    abstract BaseCallbackManager addCallbackHandler(BaseCallbackHandler callbackHandler);

    abstract BaseCallbackManager removeCallbackHandler(BaseCallbackHandler callbackHandler);

}
