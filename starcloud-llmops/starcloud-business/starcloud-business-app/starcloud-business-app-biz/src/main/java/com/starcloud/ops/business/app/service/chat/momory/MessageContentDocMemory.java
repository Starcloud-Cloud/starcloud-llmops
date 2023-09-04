package com.starcloud.ops.business.app.service.chat.momory;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 另扩展出来的Memory，存储对话中所有上传的文档和工具执行的结果的历史记录
 * 统一包装为文档，最后生成为 上下文prompt，方便处理对话中的历史问题
 */
@Slf4j
@Data
public class MessageContentDocMemory {

    private DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private MessageContentDocHistory history;

    private ConversationSummaryDbMessageMemory messageMemory;

    private Boolean storage = true;

    public MessageContentDocMemory(ConversationSummaryDbMessageMemory conversationSummaryDbMessageMemory) {
        this.messageMemory = conversationSummaryDbMessageMemory;
        this.initHistory();
    }

    /**
     * 初始化历史记录
     */
    public void initHistory() {
//
//        this.messageMemory.getChatRequestVO();
//        this.messageMemory.getLogAppMessage();
//        this.messageMemory.getChatAppEntity();

        String appUid = this.messageMemory.getChatRequestVO().getAppUid();
        String conversationUid = this.messageMemory.getChatRequestVO().getConversationUid();

        //查询数据集表
        List<DatasetSourceDataDetailRespVO> sourceDataBasicInfoVOS = this.searchSourceData(appUid, conversationUid);


        //填充history
        List<MessageContentDocDTO> history = this.convertMessageContentDoc(sourceDataBasicInfoVOS);
        this.history = new MessageContentDocHistory(history);

        log.info("MessageContentDocMemory init: {}", JsonUtils.toJsonString(history));

    }

    public Boolean hasHistory() {
        return history != null && CollectionUtil.size(history.getDocs()) > 0;
    }

    /**
     * 查询当前上下文的全部文档记录
     */
    protected List<DatasetSourceDataDetailRespVO> searchSourceData(String appUid, String conversationUid) {

        List<DatasetSourceDataDetailRespVO> sourceDataDetailRespVOS = datasetSourceDataService.getSessionSourceDataList(appUid, conversationUid, null, false);

        return sourceDataDetailRespVOS;
    }


    /**
     * 文档记录转换为 上下文文档结构
     */
    protected List<MessageContentDocDTO> convertMessageContentDoc(List<DatasetSourceDataDetailRespVO> dataBasicInfoVOS) {

        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();

        Optional.ofNullable(dataBasicInfoVOS).orElse(new ArrayList<>()).stream().map(dataBasicInfoVO -> {

            MessageContentDocDTO contentDocDTO = new MessageContentDocDTO();

            //初始化为了减少内容，主要总结的，如果总结没有取描述
            String summary = StrUtil.isNotBlank(dataBasicInfoVO.getSummary()) ? dataBasicInfoVO.getSummary() : dataBasicInfoVO.getDescription();
            // dataBasicInfoVO.getAddress();
            contentDocDTO.setId(dataBasicInfoVO.getId());
            //contentDocDTO.setContent(dataBasicInfoVO.getContent());
            contentDocDTO.setSummary(summary);
            contentDocDTO.setTitle(dataBasicInfoVO.getName());

//            contentDocDTO.setUrl();

            if (DataSourceDataTypeEnum.HTML.name().equals(dataBasicInfoVO.getDataType())) {

                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());

            } else if (DataSourceDataTypeEnum.DOCUMENT.name().equals(dataBasicInfoVO.getDataType())) {

                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.FILE.name());

            } else {

                //默认都为工具调用结果
                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.TOOL.name());

                //contentDocDTO.setToolName("toolName");
            }

            return contentDocDTO;

        }).collect(Collectors.toList());


        return messageContentDocDTOList;
    }

    /**
     * 增加文档上下文内容
     * 1，每次都重新查询文档都内容获取 描述或总结
     * 2，文档状态正常的才会增加到上下文中
     *
     * @param messageContentDocDTOList
     */
    public void addHistory(List<MessageContentDocDTO> messageContentDocDTOList) {

        Optional.ofNullable(messageContentDocDTOList).orElse(new ArrayList<>()).forEach(doc -> {

            if (this.getStorage()) {
                this.storageHistory(doc);
            }

            //增加到当前历史中，上面异常也可以增加到历史，因为方法传入的就表示有返回值了，可以做为上下文了
            this.getHistory().addDoc(doc);
        });

    }


    /**
     * 查询加载历史
     * 1，主要查询出之前对话中 文档的总结信息（因为总结是异步的）
     */
    public MessageContentDocHistory reloadHistory() {

        return this.getHistory();
    }


    /**
     * 保存文档历史
     *
     * @param doc
     */
    private void storageHistory(MessageContentDocDTO doc) {

        String sourceDataId = "";

        try {

            //之前用过文档存储，如联网功能
            if (doc.getId() != null) {

                //更新状态？
                sourceDataId = String.valueOf(doc.getId());

                log.info("MessageContentDocMemory storageHistory update: {} {}", doc.getId(), doc.getTitle());

            } else {

                String title = doc.getTitle();
                String content = doc.getContent();
                doc.getExt();
                doc.getToolName();
                //@todo 需要增加扩展信息，如messageId

                if (MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name().equals(doc.getType())) {

                    //上游已经保存过
                    log.info("storageHistory web: {}", JsonUtils.toJsonString(doc));

                    UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();
                    uploadUrlReqVO.setCleanSync(true);
                    uploadUrlReqVO.setSplitSync(false);
                    uploadUrlReqVO.setIndexSync(false);

                    uploadUrlReqVO.setUrls(Arrays.asList(doc.getUrl()));

                    List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceData(uploadUrlReqVO);

                    SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

                    //存在
                    if (sourceDataUploadDTO != null) {

                        if (!sourceDataUploadDTO.getStatus()) {
                            throw new RuntimeException("文档记录保存失败");
                        }

                        doc.setId(Long.valueOf(sourceDataUploadDTO.getSourceDataId()));
                        sourceDataId = sourceDataUploadDTO.getSourceDataId();

                        log.info("MessageContentDocMemory uploadUrlsSourceData add: {} {}", doc.getId(), doc.getTitle());
                    }


                } else if (MessageContentDocDTO.MessageContentDocTypeEnum.FILE.name().equals(doc.getType())) {

                    //文件不会直接保存都，都是先单独上传，后续用文档ID去处理

                    log.info("storageHistory file: {}", JsonUtils.toJsonString(doc));

                } else {

                    //默认是工具类型上传

                    UploadCharacterReqVO characterReqVO = new UploadCharacterReqVO();
                    characterReqVO.setCleanSync(true);
                    characterReqVO.setSplitSync(false);
                    characterReqVO.setIndexSync(false);

                    //@todo 增加扩展字段

                    characterReqVO.setCharacterVOS(Collections.singletonList(new CharacterDTO().setTitle(title).setContext(content)));

                    List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadCharactersSourceData(characterReqVO);

                    SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

                    //存在
                    if (sourceDataUploadDTO != null) {

                        if (!sourceDataUploadDTO.getStatus()) {
                            throw new RuntimeException("文档记录保存失败");
                        }

                        doc.setId(Long.valueOf(sourceDataUploadDTO.getSourceDataId()));
                        sourceDataId = sourceDataUploadDTO.getSourceDataId();

                        log.info("MessageContentDocMemory uploadCharactersSourceData add: {} {}", doc.getId(), doc.getTitle());
                    }
                }
            }

            //重新查询内容, 可获取到总结
            DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataListData(sourceDataId, true);
            //@todo 判断状态 需要封装
            if (detailsInfoVO != null) {
                if (StrUtil.isNotBlank(detailsInfoVO.getSummary())) {
                    //更新下最新的内容
                    doc.setSummary(detailsInfoVO.getSummary());
                } else {
                    //summary = StrUtil.subPre(summary, 200);
                    doc.setContent(detailsInfoVO.getContent());
                }
            } else {
                log.error("storageHistory is fail, getSourceDataListData is null. sourceDataId: {}", sourceDataId);
            }

        } catch (Exception e) {

            //上传文档到异常不处理，如果失败就不增加到上下文。只靠messageHistory 老逻辑的历史记录 去实现上下文

            log.error("MessageContentDocMemory addHistory is error: {}", e.getMessage(), e);
        }

    }


}
