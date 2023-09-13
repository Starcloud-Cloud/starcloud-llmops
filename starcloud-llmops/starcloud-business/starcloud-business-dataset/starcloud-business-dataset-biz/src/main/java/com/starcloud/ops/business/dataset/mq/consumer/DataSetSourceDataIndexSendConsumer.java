package com.starcloud.ops.business.dataset.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataIndexSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataIndexProducer;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Objects;

import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */

@Slf4j
@Component
public class DataSetSourceDataIndexSendConsumer extends AbstractDataProcessor<DatasetSourceDataIndexSendMessage> {

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataIndexProducer dataIndexProducer;

    /**
     * @param message
     */
    @Override
    protected void setDataState(DatasetSourceSendMessage message) {
        message.setStatus(DataSetSourceDataStatusEnum.INDEX_IN.getStatus());
        message.setErrMsg(DataSetSourceDataStatusEnum.INDEX_IN.getName());
    }

    /**
     * @param message
     */
    @Override
    protected void processBusinessLogic(DatasetSourceSendMessage message) {

        log.info("开始创建索引，数据集 ID 为({}),源数据 ID 为({})",message.getDatasetId(),message.getDataSourceId());
        int retryCount = message.getRetryCount();
        try {
            // 创建索引
             documentSegmentsService.indexDoc(String.valueOf(message.getDatasetId()), String.valueOf(message.getDataSourceId()));

            // 设置数据状态
            message.setStatus(DataSetSourceDataStatusEnum.COMPLETED.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.COMPLETED.getName());
            log.info("创建索引完成，数据集 ID 为({}),源数据 ID 为({})",message.getDatasetId(),message.getDataSourceId());
        } catch (Exception e) {
            // 设置数据源状态
            message.setRetryCount(++retryCount);
            message.setStatus(DataSetSourceDataStatusEnum.INDEX_ERROR.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.INDEX_ERROR.getName());
            log.error("[DataSetSourceDataCleanSendConsumer][数据创建索引失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})｜错误原因({})", message.getUserId(), getTenantId(), message.getDatasetId(),message.getDataSourceId(),e.getMessage(),e);
        }


    }

    /**
     * @param message
     */
    @Override
    protected void sendMessage(DatasetSourceSendMessage message) {
    if (message.getRetryCount() <= 3 && Objects.equals(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus(), message.getStatus())) {
            log.warn("数据索引异常，开始重试，当前重试次数为{}",message.getRetryCount());
            if (message.getCleanSync()) {
                log.info("同步执行数据创建索引操作，数据为{}", JSONObject.toJSONString(message));
                dataIndexProducer.sendMessage(message);
            } else {
                log.info("异步执行数据数据创建索引操作，数据为{}", JSONObject.toJSONString(message));
                // 发送消息
                dataIndexProducer.asyncSendMessage(message);
            }
        } else {
            log.error("执行数据创建索引操作失败，重试失败！！！数据为{}", JSONObject.toJSONString(message));
        }
    }
}