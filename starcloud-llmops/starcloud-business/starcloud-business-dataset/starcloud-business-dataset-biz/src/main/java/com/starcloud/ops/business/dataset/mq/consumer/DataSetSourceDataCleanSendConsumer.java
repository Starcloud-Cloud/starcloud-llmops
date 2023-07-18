package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;

import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */
@Component
@Slf4j
public class DataSetSourceDataCleanSendConsumer extends AbstractStreamMessageListener<DatasetSourceDataCleanSendMessage> {

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Override
    public void onMessage(DatasetSourceDataCleanSendMessage message) {

        // 设置数据源状态为清洗中
        datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_IN.getStatus());

        Tika tika = new Tika();
        String text = null;
        try {

            text = tika.parseToString(new URL(message.getStorageKey()));
            String cleanText = TextCleanUtils.cleanText(text, message.getSplitRule());

            // FIXME: 2023/7/18 TODO 文件存储

            datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus());
            // 发送消息
            dataSplitProducer.sendSplitDatasetsSendMessage(message.getDatasetId(), message.getDataSourceId(), message.getSplitRule(), cleanText);
        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据清洗失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})", getLoginUserId(), getTenantId(), message.getDataSourceId(), message.getDataSourceId());
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
        }
        // 设置数据源状态为清洗结束

    }

}