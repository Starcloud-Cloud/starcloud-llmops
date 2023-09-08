package com.starcloud.ops.business.app.domain.handler.imggeneration;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveData;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.vsearch.StylePresetEnum;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.pojo.dto.BaseDBHandleDTO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Open AI 执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class ImageGenerationHandler extends BaseToolHandler<ImageGenerationHandler.Request, ImageGenerationHandler.Response> {

    public static ImageService imageService = SpringUtil.getBean(ImageService.class);

    private String userName = "图片生成";

    private String userDescription = "可以根据描述词, 生成出任何风格的图片";

    private String name = "ImageGenerationHandler";

    private String description = "Generate images. This is used when you need to generate an image. The input is a sentence describing an image. The output is an access address to an image.";


    /**
     * 工具名称
     */
    private String toolName = "Images Generated";

    /**
     * 工具描述
     */
    private String toolDescription = "Generate images. This is used when you need to generate an image. The input is a sentence describing an image. The output is an access address to an image.";

    /**
     * 使用方法
     */
    private String toolInstructions = "1. Enter the search query: Type in the query you wish to search for in the input box.\n" +
            "2. Specify search type: Choose the appropriate search type from the options: \"image\", \"content\", or \"news\".";


    /**
     * 结果解释
     */
    private String interpretingResults =
            "- url: The address of the network content.\n" +
                    "- time: The generation of content.";


    /**
     * 示例输入
     */
    private String exampleInput = "prompt: \"Artificial Intelligence\"\n" +
            "stylePreset: \"news\"";


    /**
     * 示例输出
     */
    private String exampleOutput;


    /**
     * 注意
     */
    private String note;

    private int size = 1;

    @Override
    public Boolean isAddHistory() {
        return false;
    }


    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String prompt = context.getRequest().getPrompt();

        InteractiveInfo interactiveInfo = InteractiveInfo.buildImgCard("图片生成中[" + prompt + "]...").setToolHandler(this).setInput(context.getRequest());

        context.sendCallbackInteractiveStart(interactiveInfo);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);
        handlerResponse.setMessage(JsonUtils.toJsonString(context.getRequest()));

        List<ImageDTO> imageDTOS = this.generation(context);

        handlerResponse.setSuccess(true);

        Response result = new Response();

        List<InteractiveData> dataList = Optional.ofNullable(imageDTOS).orElse(new ArrayList<>()).stream().map(imageDTO -> {
            InteractiveData interactiveData = new InteractiveData();
            interactiveData.setUrl(imageDTO.getUrl());
            interactiveData.setTime(DateUtil.now());
            return interactiveData;
        }).collect(Collectors.toList());
        result.setImageUrls(dataList);

        handlerResponse.setOutput(result);

        interactiveInfo.setData(dataList);
        interactiveInfo.setTips("图片生成完成");

        context.sendCallbackInteractiveEnd(interactiveInfo);

        return handlerResponse;
    }


    private List<ImageDTO> generation(HandlerContext<Request> context) {

        Request request = context.getRequest();

        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setPrompt(request.getPrompt());
        imageRequest.setStylePreset(request.getStylePreset());
        imageRequest.setSamples(1);

        ImageReqVO imageReqVO = new ImageReqVO();

        imageReqVO.setEndUser(String.valueOf(context.getEndUser()));
        imageReqVO.setAppUid(context.getAppUid());
        imageReqVO.setScene(context.getScene().name());
        imageReqVO.setUserId(context.getUserId());

        imageReqVO.setImageRequest(imageRequest);

        ImageMessageRespVO imageMessageRespVO = imageService.generateImage(imageReqVO);

        return Optional.ofNullable(imageMessageRespVO.getImages()).orElse(new ArrayList<>());
    }

    /**
     * 包装为 下午文 文档结构
     * 默认实现，工具类型返回
     */
    @Override
    protected List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

//        StylePresetEnum
        return null;
    }


    @Data
    public static class Request implements Serializable {

        @JsonProperty(required = true)
        @JsonPropertyDescription("need to generate a prompt of the image")
        private String prompt;

        //@see StylePresetEnum
        @JsonProperty(required = true)
        @JsonPropertyDescription("Guiding the image model toward a particular style.Values include: ENHANCE, ANIME, PHOTOGRAPHIC, COMIC_BOOK. Can only use this value.")
        private String stylePreset;

    }


    @Data
    public static class Response implements Serializable {

        private List<InteractiveData> imageUrls;

    }
}
