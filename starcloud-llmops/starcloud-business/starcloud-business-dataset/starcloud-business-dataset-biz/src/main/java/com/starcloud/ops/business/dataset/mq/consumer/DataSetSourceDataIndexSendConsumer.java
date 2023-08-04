package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataIndexSendMessage;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */
@Component
@Slf4j
public class DataSetSourceDataIndexSendConsumer extends AbstractStreamMessageListener<DatasetSourceDataIndexSendMessage> {

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Override
    public void onMessage(DatasetSourceDataIndexSendMessage message) {

        // 设置数据源状态为正在创建索引
        datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.INDEX_IN.getStatus(),null);
        try {
            // 创建索引
            documentSegmentsService.indexDoc(message.getDatasetId(), String.valueOf(message.getDataSourceId()));
            // 设置数据源状态为创建索引完成
            datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.INDEX_COMPLETED.getStatus(),null);

        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据创建索引：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})｜错误原因({})", message.getUserId(), getTenantId(), message.getDataSourceId(),message.getDataSourceId(),e.getMessage(),e);
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.INDEX_ERROR.getStatus(),e.getMessage());
        }
    }

}