package com.starcloud.ops.business.app.domain.handler.imggeneration;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.GenerateImageRequest;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveData;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.app.service.image.ImageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@Component
public class ImageGenerationHandler extends BaseToolHandler<ImageGenerationHandler.Request, ImageGenerationHandler.Response> {

    public static ImageService imageService = SpringUtil.getBean(ImageService.class);

    private String userName = "生成图片";

    private String userDescription = "可以根据描述词, 生成出任意风格的图片";

    private String name = "ImageGenerationHandler";

    private String description = "Generate images. This is used when you need to generate an image. The input is a sentence describing an image. The output is an access address to an image.";

    private String icon = "AddPhotoAlternate";


    private String usage = "1.帮我生成一张跑车的照片电影风格的\n" +
            "2.帮我生成一张少女在湖边散步的照片卡通风格的";

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
    private String toolInstructions = "";


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
            interactiveData.setImageUrl(imageDTO.getUrl());
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

        GenerateImageRequest imageRequest = new GenerateImageRequest();
        imageRequest.setPrompt(request.getPrompt());
        imageRequest.setStylePreset(request.getStylePreset());
        imageRequest.setSamples(1);

        ImageReqVO imageReqVO = new ImageReqVO();
        if (context.getEndUser() != null) {
            imageReqVO.setEndUser(String.valueOf(context.getEndUser()));
        }
        imageReqVO.setAppUid(RecommendAppEnum.GENERATE_IMAGE.name());
        imageReqVO.setScene(context.getScene().name());
        imageReqVO.setUserId(context.getUserId());

        imageReqVO.setImageRequest(imageRequest);

        ImageRespVO imageMessageRespVO = imageService.execute(imageReqVO);

        return Optional.ofNullable(imageMessageRespVO.getResponse().getImages()).orElse(new ArrayList<>());
    }

    /**
     * 包装为 下午文 文档结构
     * 默认实现，工具类型返回
     */
    @Override
    public List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

//        StylePresetEnum
        return null;
    }


    @Data
    public static class Request implements Serializable {

        @JsonProperty(required = true)
        @JsonPropertyDescription("need to generate a prompt of the image.Must be in English, if not, need to take the initiative to translate into English")
        private String prompt;

        //@see StylePresetEnum
        @JsonProperty(required = true)
        @JsonPropertyDescription("Guiding the image model toward a particular style.Values include: enhance, anime, photographic, comic-book, fantasy-art, analog-film, neon-punk, isometric, low-poly, origami, line-art, cinematic, 3d-model, pixel-art. Can only use this value.")
        private String stylePreset;

    }


    @Data
    public static class Response implements Serializable {

        private List<InteractiveData> imageUrls;

    }
}
