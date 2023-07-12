package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataSplitSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataIndexProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.llm.langchain.core.indexes.splitter.SplitterContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */
@Component
@Slf4j
public class DataSetSourceDataSplitSendConsumer extends AbstractStreamMessageListener<DatasetSourceDataSplitSendMessage> {

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataIndexProducer dataIndexProducer;


    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Override
    public void onMessage(DatasetSourceDataSplitSendMessage message) {

        // 设置数据源状态为清洗中
        datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_IN.getStatus());

        try {
            List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(message.getCleanText(), message.getSplitRule().getChunkSize(), message.getSplitRule().getSeparator());
            datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_COMPLETED.getStatus());
            // 发送消息
            dataIndexProducer.sendIndexDatasetsSendMessage(message.getDatasetId() ,message.getDataSourceId(),splitText);

        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据清洗失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})", getLoginUserId(), getTenantId(), message.getDataSourceId(), message.getDataSourceId());
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_ERROR.getStatus());
        }
    }

}