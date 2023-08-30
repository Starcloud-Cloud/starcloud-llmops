package com.starcloud.ops.business.app.service.chat.momory;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.CharacterDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataBasicInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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

    private Boolean storage = false;

    public MessageContentDocMemory(ConversationSummaryDbMessageMemory conversationSummaryDbMessageMemory) {
        this.messageMemory = conversationSummaryDbMessageMemory;
        this.initHistory();
    }

    /**
     * 初始化历史记录
     */
    public void initHistory() {

        this.messageMemory.getChatRequestVO();
        this.messageMemory.getLogAppMessage();
        this.messageMemory.getChatAppEntity();


        //查询数据集表
        this.searchSourceData();


        //填充history
        List<MessageContentDocDTO> history = this.convertMessageContentDoc(null);
        this.history = new MessageContentDocHistory(history);

    }

    public Boolean hasHistory() {
        return history != null && CollectionUtil.size(history.getDocs()) > 0;
    }

    /**
     * 查询当前上下文的全部文档记录
     */
    public List<DatasetSourceDataBasicInfoVO> searchSourceData() {


        return new ArrayList<>();
    }


    /**
     * 文档记录转换为 上下文文档结构
     */
    public List<MessageContentDocDTO> convertMessageContentDoc(List<DatasetSourceDataBasicInfoVO> datasetSourceDataBasicInfoVOS) {

        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();


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

                doc.getType();
                doc.getExt();

                UploadCharacterReqVO uploadCharacterReqVOS = new UploadCharacterReqVO();
                UploadCharacterReqVO characterReqVO = new UploadCharacterReqVO();
                characterReqVO.setSync(true);
                characterReqVO.setCharacterVOS(Collections.singletonList(new CharacterDTO().setTitle(title).setContext(content)));


                //@todo 需要增加扩展信息，如messageId


                //@todo 确定上传到哪个数据集


                //save db
                List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadCharactersSourceData(uploadCharacterReqVOS);
                SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

                //存在
                if (sourceDataUploadDTO != null) {

                    doc.setId(Long.valueOf(sourceDataUploadDTO.getSourceDataId()));
                    sourceDataId = sourceDataUploadDTO.getSourceDataId();

                    if (!sourceDataUploadDTO.getStatus()) {
                        throw new RuntimeException("文档记录保存失败");
                    }

                    log.info("MessageContentDocMemory storageHistory add: {} {}", doc.getId(), doc.getTitle());
                }
            }

            //重新查询内容, 可获取到总结
            DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataListData(sourceDataId, true);

            //@todo 判断状态 需要封装
            if (detailsInfoVO != null && detailsInfoVO.getStatus() == 1) {

                String summary = StrUtil.isNotBlank(detailsInfoVO.getSummary()) ? detailsInfoVO.getSummary() : detailsInfoVO.getDescription();

                summary = StrUtil.subPre(summary, 200);

                //更新下最新的内容
                doc.setContent(summary);
            }

        } catch (Exception e) {

            //上传文档到异常不处理，如果失败就不增加到上下文。只靠messageHistory 老逻辑的历史记录 去实现上下文

            log.error("MessageContentDocMemory addHistory is error: {}", e.getMessage(), e);
        }

    }


}
