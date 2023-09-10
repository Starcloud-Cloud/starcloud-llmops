package com.starcloud.ops.business.app.domain.handler.datasearch;


import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveData;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.pojo.dto.BaseDBHandleDTO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 页面内容,网络文件内容爬取，并创建对应索引和总结
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class WebSearch2DocHandler extends BaseToolHandler<WebSearch2DocHandler.Request, WebSearch2DocHandler.Response> {

    private DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private String userName = "网页和文档分析";

    private String userDescription = "可访问网络上公开的网页，文档内容。可基于内容完成摘要，问答等。仅支持10M以内文档，不支持扫描件";

    private String name = "WebSearch2DocHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output should be a json string with two keys: \"content\" and\" docKey\". The value of \"content\" is a summary of the content of the website, and the value of\" docKey\" is the tag of the website to point to.";

    private int summarySubSize = 300;

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String url = context.getRequest().getUrl();


        //@todo 通过上下文获取当前可能配置的 tools 执行 tips
        InteractiveInfo interactiveInfo = InteractiveInfo.buildUrlCard("分析链接内容中[" + url + "]...").setToolHandler(this).setInput(context.getRequest());


        context.sendCallbackInteractiveStart(interactiveInfo);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);
        handlerResponse.setMessage(JsonUtils.toJsonString(context.getRequest()));


        UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();

        uploadUrlReqVO.setCleanSync(true);
        uploadUrlReqVO.setSplitSync(false);
        uploadUrlReqVO.setIndexSync(false);

        uploadUrlReqVO.setSessionId(context.getConversationUid());
        uploadUrlReqVO.setUrls(Arrays.asList(url));
        uploadUrlReqVO.setAppId(context.getAppUid());
        // TODO 添加创建人或者游客

        BaseDBHandleDTO baseDBHandleDTO = new BaseDBHandleDTO();
        baseDBHandleDTO.setCreator(context.getUserId());
        baseDBHandleDTO.setEndUser(context.getEndUser());

        List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceDataBySession(uploadUrlReqVO, baseDBHandleDTO);
        SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

        if (!sourceDataUploadDTO.getStatus()) {
            log.error("WebSearch2DocHandler uploadUrlsSourceDataBySession is fail:{}, {}", url, sourceDataUploadDTO.getErrMsg());

            throw new RuntimeException("URL解析失败");
        }

        // 查询内容
        DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataById(sourceDataUploadDTO.getSourceDataId(), true);
        String desc = detailsInfoVO.getDescription();
        handlerResponse.setSuccess(true);

        Response result = new Response();
        // 先截取
        result.setTitle(detailsInfoVO.getName());
        result.setDescription(desc);
        result.setDocId(detailsInfoVO.getId());
        handlerResponse.setOutput(result);


        InteractiveData interactiveData = new InteractiveData();

        interactiveData.setTitle(detailsInfoVO.getName());
        interactiveData.setContent(result.getDescription());
        interactiveData.setUrl(url);
        interactiveData.setTime(DateUtil.now());

        List<InteractiveData> dataList = Arrays.asList(interactiveData);

        // handlerResponse.setExt(dataList);

        interactiveInfo.setData(dataList);
        interactiveInfo.setTips("分析链接完成");
        context.sendCallbackInteractiveEnd(interactiveInfo);

        return handlerResponse;
    }

    /**
     * 包装为 下午文 文档结构
     * 默认实现，工具类型返回
     */
    @Override
    protected List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

        // 解析返回的内容 生成 MessageContentDocDTO
        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();

        MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

        messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());

        messageContentDocDTO.setTime(DateUtil.now());
        messageContentDocDTO.setTitle(this.getName());
        messageContentDocDTO.setContent(handlerResponse.getOutput().getDescription());
        messageContentDocDTO.setId(handlerResponse.getOutput().getDocId());

        messageContentDocDTOList.add(messageContentDocDTO);

        return messageContentDocDTOList;
    }


    @Data
    public static class Request implements Serializable {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a website url")
        private String url;

    }


    @Data
    public static class Response implements Serializable {

        private String title;

        private String description;

        private Long docId;

    }

}
