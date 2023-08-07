package com.starcloud.ops.business.dataset.mq.consumer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceIndoDTO;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.URL;

import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

/**
 * 针对 {@link DatasetSourceDataCleanSendMessage} 的消费者
 *
 * @author Alan Cusack
 */
@Component
@Slf4j
public class DataSetSourceDataCleanSendConsumer extends AbstractStreamMessageListener<DatasetSourceDataCleanSendMessage> {

    @Resource
    private FileApi fileApi;

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;


    private static final String PATH_OBJECT = "dataset-source-data/clean/";


    @Override
    public void onMessage(DatasetSourceDataCleanSendMessage message) {

        // 设置数据源状态为清洗中
        datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_IN.getStatus(),null);

        // 根据数据源 ID获取数据储存ID
        DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());

        // 根据储存ID 获取存储地址
        DatasetStorageDO storageDO = selectDatasetStorage(sourceDataDO.getStorageId());

        Tika tika = new Tika();
        String text = null;
        try {

            text = tika.parseToString(new URL(storageDO.getStorageKey()));
            String cleanText = TextCleanUtils.cleanText(text, message.getSplitRule());

            // 清洗后数据存储 文件存储
            String cleanPath = uploadFile(cleanText, message.getUserId());

            Long cleanId = this.setStorageData(message.getDataSourceId() + "_clean", cleanPath, (long) cleanPath.getBytes().length, "text/html", "html", message.getUserId());
            DataSourceIndoDTO DataSourceIndoDTO = new DataSourceIndoDTO();
            DataSourceIndoDTO.setCleanId(cleanId);
            datasetSourceDataService.updateDatasourceAndSourceInfo(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus(), JSONObject.toJSONString(DataSourceIndoDTO), message.getUserId());

            if (message.getSync()) {
                dataSplitProducer.sendMessage(message.getDataSourceId());
            } else {
                // 发送消息
                dataSplitProducer.sendSplitDatasetsSendMessage(message.getDatasetId(), message.getDataSourceId(), message.getSplitRule(), message.getUserId());

            }

        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据清洗失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})｜错误原因 ({})", message.getUserId(), getTenantId(), message.getDataSourceId(), message.getDataSourceId(),e.getMessage(),e);
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatusAndMessage(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus(),e.getMessage());
        }
        // 设置数据源状态为清洗结束

    }

    private String uploadFile(String data, Long userId) {

        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);
        // 生成文件ID-生成
        String fileId = SecureUtil.md5(data);


        String fileName = fileId + "." + "txt";
        String path = String.format(PATH_OBJECT + "%s" + "/", userId) + fileName;

        return fileApi.createFile(fileName, path, IoUtil.readBytes(utf8Stream));
    }

    public Long setStorageData(String sourceName, String storageAddress, Long size, String mimeType, String extension, Long userId) {
        DatasetStorageDO datasetStorageDO = new DatasetStorageDO();

        datasetStorageDO.setUid(IdUtil.getSnowflakeNextIdStr());
        datasetStorageDO.setName(sourceName);
        datasetStorageDO.setStorageKey(storageAddress);
        datasetStorageDO.setType(extension.toUpperCase());
        datasetStorageDO.setSize(size);
        datasetStorageDO.setMimeType(mimeType.toUpperCase());
        datasetStorageDO.setUsed(false);
        datasetStorageDO.setCreator(String.valueOf(userId));
        datasetStorageDO.setUpdater(String.valueOf(userId));

        datasetStorageMapper.insert(datasetStorageDO);
        // 数据入库
        return datasetStorageDO.getId();
    }

    public DatasetStorageDO selectDatasetStorage(Long id) {
        return datasetStorageMapper.selectById(id);

    }

}