package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataSplitSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataIndexProducer;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_SOURCE_DATA_NOT_EXISTS;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */

@Slf4j
@Component
public class DataSetSourceDataSplitSendConsumer extends AbstractDataProcessor<DatasetSourceDataSplitSendMessage> {


    @Resource
    private DictDataService dictDataService;
    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataIndexProducer dataIndexProducer;

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    /**
     * @param message
     */
    @Override
    protected void setDataState(DatasetSourceSendMessage message) {
        message.setStatus(DataSetSourceDataStatusEnum.SPLIT_IN.getStatus());
        message.setErrMsg(DataSetSourceDataStatusEnum.SPLIT_IN.getName());
    }

    /**
     * @param message
     */
    @Override
    protected void processBusinessLogic(DatasetSourceSendMessage message) {
        log.info("开始分割数据，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());


        try {
            Tika tika = new Tika();
            // 根据数据源 ID获取数据储存ID
            DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());

            if (sourceDataDO ==null){
                log.error("分割数据过程中，获取数据源失败，请检查数据信息，message 是({})",message);
                throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
            }
            // 根据储存ID 获取存储地址
            DatasetStorageDO storageDO = selectDatasetStorage(sourceDataDO.getCleanStorageId());
            String text = tika.parseToString(new URL(storageDO.getStorageKey()));
            // 数据分块
            documentSegmentsService.splitDoc(String.valueOf(message.getDatasetId()), String.valueOf(message.getDataSourceId()), text, message.getSplitRule());

            // 设置数据源状态
            message.setStatus(DataSetSourceDataStatusEnum.SPLIT_COMPLETED.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.SPLIT_COMPLETED.getName());
            // 发送消息
            log.info("分割数据完成，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());

        } catch (Exception e) {
            // 设置数据源状态
            message.setStatus(DataSetSourceDataStatusEnum.SPLIT_ERROR.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.SPLIT_ERROR.getName());
            log.error("[DataSetSourceDataCleanSendConsumer][数据分割失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})｜错误原因({})", getLoginUserId(), getTenantId(), message.getDatasetId(), message.getDataSourceId(), e.getMessage(), e);
        }
    }


    /**
     * @param message
     */
    @Override
    protected void sendMessage(DatasetSourceSendMessage message) {

        if (0 == dictDataService.getDictData("QueueSwitch", "sendMessage").getStatus()) {


            if (Objects.equals(message.getStatus(), DataSetSourceDataStatusEnum.SPLIT_COMPLETED.getStatus())) {
                // 如果执行成功 重置重试次数
                message.setRetryCount(0);
                if (message.getIndexSync()) {
                    log.info("同步执行数据索引操作，数据为{}", JSONObject.toJSONString(message));
                    dataIndexProducer.sendMessage(message);

                } else {
                    log.info("异步执行数据索引操作，数据为{}",JSONObject.toJSONString(message));
                    dataIndexProducer.asyncSendMessage(message);
                }
            } else if (message.getRetryCount() <= 3 && Objects.equals(DataSetSourceDataStatusEnum.SPLIT_ERROR.getStatus(), message.getStatus())) {
                int retryCount = message.getRetryCount();
                message.setRetryCount(++retryCount);
                log.warn("数据分块异常，开始重试，当前重试次数为{}",message.getRetryCount());
                if (message.getCleanSync()) {
                    log.info("同步执行数据清洗操作，数据为{}", JSONObject.toJSONString(message));
                    dataSplitProducer.sendMessage(message);
                } else {
                    log.info("异步执行数据清洗操作，数据为{}", JSONObject.toJSONString(message));
                    // 发送消息
                    dataSplitProducer.asyncSendMessage(message);
                }
            } else {
                log.error("执行数据分块失败，重试失败！！！数据为{}", JSONObject.toJSONString(message));
            }
        }else {
            log.warn("队列开关已关闭，数据为{}", JSONObject.toJSONString(message));
        }
    }


    private DatasetStorageDO selectDatasetStorage(Long id) {
        return datasetStorageMapper.selectById(id);

    }
}