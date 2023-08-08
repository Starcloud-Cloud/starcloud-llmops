package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataSplitSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataIndexProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceIndoDTO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
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
public class DataSetSourceDataSplitSendConsumer extends AbstractStreamMessageListener<DatasetSourceDataSplitSendMessage> {

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataIndexProducer dataIndexProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Override
    public void onMessage(DatasetSourceDataSplitSendMessage message) {

        // 设置数据源状态为清洗中
        datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_IN.getStatus(), null);

        // 根据数据源 ID获取数据储存ID
        DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());
        DataSourceIndoDTO dataSourceIndoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceIndoDTO.class);

        // 根据储存ID 获取存储地址
        DatasetStorageDO storageDO = selectDatasetStorage(dataSourceIndoDTO.getCleanId());

        Tika tika = new Tika();
        try {
            // fixme 查询清洗数据
            String text = tika.parseToString(new URL(storageDO.getStorageKey()));
            documentSegmentsService.splitDoc(message.getDatasetId(), String.valueOf(message.getDataSourceId()), text, message.getSplitRule());
            datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_COMPLETED.getStatus(), null);
            // 发送消息

            if (message.getSync()) {

                dataIndexProducer.sendMessage(message);

            } else {
                dataIndexProducer.sendIndexDatasetsSendMessage(message.getDatasetId(), message.getDataSourceId());

            }


        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据分割失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})｜错误原因({})", getLoginUserId(), getTenantId(), message.getDataSourceId(), message.getDataSourceId(), e.getMessage(), e);
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.SPLIT_ERROR.getStatus(), e.getMessage());
        }
    }


    public DatasetStorageDO selectDatasetStorage(Long id) {
        return datasetStorageMapper.selectById(id);

    }

}