package com.starcloud.ops.business.app.service.chat.momory;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import lombok.Data;
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

    private MessageContentDocHistory history = new MessageContentDocHistory(new ArrayList<>());

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
        String appUid = this.messageMemory.getChatAppEntity().getUid();
        String conversationUid = this.messageMemory.getChatRequestVO().getConversationUid();
        log.info("MessageContentDocMemory init start, appUid:[{}] conversationUid[{}]", appUid, conversationUid);
        this.loadHistory();
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

        List<MessageContentDocDTO> messageContentDocDTOList = Optional.ofNullable(dataBasicInfoVOS).orElse(new ArrayList<>()).stream().map(dataBasicInfoVO -> {

            MessageContentDocDTO contentDocDTO = new MessageContentDocDTO();

            // 初始化为了减少内容，主要总结的，如果总结没有取描述
            contentDocDTO.setId(dataBasicInfoVO.getId());
            contentDocDTO.setTitle(dataBasicInfoVO.getName());

            if (StrUtil.isNotBlank(dataBasicInfoVO.getSummary())) {
                contentDocDTO.setSummary(dataBasicInfoVO.getSummary());
            }
            if (StrUtil.isNotBlank(dataBasicInfoVO.getDescription())) {
                contentDocDTO.setContent(dataBasicInfoVO.getDescription());
            }

            if (DataSourceDataTypeEnum.HTML.name().equals(dataBasicInfoVO.getDataType())) {

                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
                contentDocDTO.setUrl(dataBasicInfoVO.getInitAddress());

            } else if (DataSourceDataTypeEnum.DOCUMENT.name().equals(dataBasicInfoVO.getDataType())) {

                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.FILE.name());
                contentDocDTO.setUrl(dataBasicInfoVO.getInitAddress());

            } else {

                // 默认都为工具调用结果
                contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.TOOL.name());

                // contentDocDTO.setToolName("toolName");
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

        this.storageHistoryList(messageContentDocDTOList);

        Optional.ofNullable(messageContentDocDTOList).orElse(new ArrayList<>()).forEach(doc -> {
            // 增加到当前历史中，上面异常也可以增加到历史，因为方法传入的就表示有返回值了，可以做为上下文了
            this.getHistory().addDoc(doc);
        });

    }


    /**
     * 查询加载历史
     * 1，主要查询出之前对话过程中存储的 上下文文档的总结信息（因为总结是异步的）
     */
    protected MessageContentDocHistory loadHistory() {

        String appUid = this.messageMemory.getChatAppEntity().getUid();
        String conversationUid = this.messageMemory.getChatRequestVO().getConversationUid();

        // 查询数据集表
        List<DatasetSourceDataDetailRespVO> sourceDataBasicInfoVOS = this.searchSourceData(appUid, conversationUid);

        // 填充history
        List<MessageContentDocDTO> history = this.convertMessageContentDoc(sourceDataBasicInfoVOS);
        this.history.setDocs(history);

        log.info("MessageContentDocMemory loadHistory appUid[{}] conversationUid[{}]: {}", appUid, conversationUid, JsonUtils.toJsonString(this.history));

        return this.history;
    }


    /**
     * 刷新上下文文档的总结字段内容
     * 1，总结是异步的，所以在这每次都刷新一遍
     *
     * @todo 查询返回的内容 和 总结内容
     */
    public MessageContentDocHistory reloadHistory() {


        return this.history;
    }


    /**
     * 批量上传上下文文档
     *
     * @param
     */
    private void storageHistoryList(List<MessageContentDocDTO> docs) {

        try {

            String appUid = this.messageMemory.getChatRequestVO().getAppUid();
            String conversationUid = this.messageMemory.getChatRequestVO().getConversationUid();
            Long userId = this.messageMemory.getChatRequestVO().getUserId();
            Long endUser = this.messageMemory.getChatRequestVO().getEndUserId();

            //一次只会有一种类型
            Map<String, List<MessageContentDocDTO>> docMaps = Optional.ofNullable(docs).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(MessageContentDocDTO::getType));

            UserBaseDTO baseDBHandleDTO = new UserBaseDTO();
            baseDBHandleDTO.setCreator(userId);
            baseDBHandleDTO.setEndUser(endUser);

            //分类型处理

            //web
            List<MessageContentDocDTO> urlDocs = Optional.ofNullable(docMaps.get(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name())).orElse(new ArrayList<>()).stream().filter(doc -> {
                if (doc.getId() != null || StrUtil.isBlank(doc.getUrl())) {
                    log.info("storageHistoryList urlDocs exception: {}", JsonUtils.toJsonString(doc));
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            List<String> docUrls = Optional.of(urlDocs).orElse(new ArrayList<>()).stream().map(MessageContentDocDTO::getUrl).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(docUrls)) {

                UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();
                uploadUrlReqVO.setCleanSync(true);
                uploadUrlReqVO.setSplitSync(false);
                uploadUrlReqVO.setIndexSync(false);
                uploadUrlReqVO.setAppId(appUid);
                uploadUrlReqVO.setSessionId(conversationUid);

                uploadUrlReqVO.setUrls(docUrls);
                List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceDataBySession(uploadUrlReqVO, baseDBHandleDTO);

                Assert.equals(urlDocs.size(), sourceDataUploadDTOS.size(), "storageHistoryList uploadUrls is fail");

                for (int i = 0; i < sourceDataUploadDTOS.size(); i++) {
                    Long docId = sourceDataUploadDTOS.get(i).getSourceDataId();
                    urlDocs.get(i).setId(docId);
                }
            }


            //tool

            List<MessageContentDocDTO> toolDocs = Optional.ofNullable(docMaps.get(MessageContentDocDTO.MessageContentDocTypeEnum.TOOL.name())).orElse(new ArrayList<>()).stream().filter(doc -> {
                if (doc.getId() != null || StrUtil.isBlank(doc.getTitle())) {
                    log.info("storageHistoryList toolDocs exception: {}", JsonUtils.toJsonString(doc));
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            List<CharacterDTO> characterDTOList = Optional.of(toolDocs).orElse(new ArrayList<>()).stream().map(doc -> {
                return new CharacterDTO().setTitle(doc.getTitle()).setContext(doc.getContent());
            }).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(characterDTOList)) {
                UploadCharacterReqVO characterReqVO = new UploadCharacterReqVO();
                characterReqVO.setCleanSync(true);
                characterReqVO.setSplitSync(false);
                characterReqVO.setIndexSync(false);
                characterReqVO.setAppId(appUid);
                characterReqVO.setSessionId(conversationUid);

                characterReqVO.setCharacterVOS(characterDTOList);

                List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadCharactersSourceDataBySession(characterReqVO, baseDBHandleDTO);

                Assert.equals(toolDocs.size(), sourceDataUploadDTOS.size(), "storageHistoryList uploadCharacters is fail");

                for (int i = 0; i < sourceDataUploadDTOS.size(); i++) {
                    Long docId = sourceDataUploadDTOS.get(i).getSourceDataId();
                    toolDocs.get(i).setId(docId);
                }
            }

            //FILE
            //文件不会直接保存都，都是先单独上传，后续用文档ID去处理

        } catch (Exception e) {

            // 上传文档到异常不处理，如果失败就不增加到上下文。只靠messageHistory 老逻辑的历史记录 去实现上下文

            log.error("MessageContentDocMemory storageHistory is error: {}", e.getMessage(), e);
        }

    }

    /**
     * 保存文档历史
     *
     * @param doc
     */
    private void storageHistory(MessageContentDocDTO doc) {

        Long sourceDataId = 0l;

        try {

            String appUid = this.messageMemory.getChatRequestVO().getAppUid();
            String conversationUid = this.messageMemory.getChatRequestVO().getConversationUid();
            Long userId = this.messageMemory.getChatRequestVO().getUserId();
            Long endUser = this.messageMemory.getChatRequestVO().getEndUserId();


            // 之前用过文档存储，如联网功能
            if (doc.getId() != null) {

                // 更新状态？
                sourceDataId = doc.getId();

                log.info("MessageContentDocMemory storageHistory update: {} {}", doc.getId(), doc.getTitle());

            } else {

                String title = doc.getTitle();
                String content = doc.getContent();
                doc.getExt();
                doc.getToolName();
                //@todo 需要增加扩展信息，如messageId

                UserBaseDTO baseDBHandleDTO = new UserBaseDTO();
                baseDBHandleDTO.setCreator(userId);
                baseDBHandleDTO.setEndUser(endUser);

                if (MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name().equals(doc.getType())) {

                    // 上游已经保存过
                    log.info("MessageContentDocMemory storageHistory web start: {}", JsonUtils.toJsonString(doc));

                    UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();
                    uploadUrlReqVO.setCleanSync(true);
                    uploadUrlReqVO.setSplitSync(false);
                    uploadUrlReqVO.setIndexSync(false);
                    uploadUrlReqVO.setAppId(appUid);
                    uploadUrlReqVO.setSessionId(conversationUid);

                    uploadUrlReqVO.setUrls(Arrays.asList(doc.getUrl()));
                    // TODO 添加创建人或者游客

                    List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceDataBySession(uploadUrlReqVO, baseDBHandleDTO);

                    SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

                    // 存在
                    if (sourceDataUploadDTO != null) {

                        if (!sourceDataUploadDTO.getStatus()) {
                            throw new RuntimeException("会话文档保存失败: " + JsonUtils.toJsonString(doc));
                        }

                        doc.setId(sourceDataUploadDTO.getSourceDataId());
                        sourceDataId = sourceDataUploadDTO.getSourceDataId();

                        log.info("MessageContentDocMemory uploadUrlsSourceData success: {} {}", doc.getId(), doc.getTitle());
                    }


                } else if (MessageContentDocDTO.MessageContentDocTypeEnum.FILE.name().equals(doc.getType())) {

                    // 文件不会直接保存都，都是先单独上传，后续用文档ID去处理

                    log.info("storageHistory file: {}", JsonUtils.toJsonString(doc));

                } else {

                    // 默认是工具类型上传

                    UploadCharacterReqVO characterReqVO = new UploadCharacterReqVO();
                    characterReqVO.setCleanSync(true);
                    characterReqVO.setSplitSync(false);
                    characterReqVO.setIndexSync(false);
                    characterReqVO.setAppId(appUid);
                    characterReqVO.setSessionId(conversationUid);

                    //@todo 增加扩展字段

                    characterReqVO.setCharacterVOS(Collections.singletonList(new CharacterDTO().setTitle(title).setContext(content)));
                    // TODO 添加创建人或者游客
                    List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadCharactersSourceDataBySession(characterReqVO, baseDBHandleDTO);

                    SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

                    // 存在
                    if (sourceDataUploadDTO != null) {

                        if (!sourceDataUploadDTO.getStatus()) {
                            throw new RuntimeException("会话文档保存失败: " + JsonUtils.toJsonString(doc));
                        }

                        doc.setId(sourceDataUploadDTO.getSourceDataId());
                        sourceDataId = sourceDataUploadDTO.getSourceDataId();

                        log.info("MessageContentDocMemory uploadCharactersSourceData add: {} {}", doc.getId(), doc.getTitle());
                    }
                }
            }

//            // 重新查询内容, 可获取到总结
//            DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataById(sourceDataId, true);
//
//            if (detailsInfoVO != null) {
//                if (StrUtil.isNotBlank(detailsInfoVO.getSummary())) {
//                    // 更新下最新的内容
//                    doc.setSummary(detailsInfoVO.getSummary());
//                    //精简内容只留总结的
//                    doc.setContent(null);
//                }
//            } else {
//                log.error("storageHistory is fail, getSourceDataListData is null. sourceDataId: {}", sourceDataId);
//            }

        } catch (Exception e) {

            // 上传文档到异常不处理，如果失败就不增加到上下文。只靠messageHistory 老逻辑的历史记录 去实现上下文

            log.error("MessageContentDocMemory storageHistory is error: {}", e.getMessage(), e);
        }

    }


}
