package com.starcloud.ops.llm.langchain.core.model.llm.azure;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface AzureAiApi extends com.theokanning.openai.OpenAiApi {


    @Override
    @Streaming
    @POST("/chat/completions?api-version=2023-05-15")
    Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionRequest request);


}
