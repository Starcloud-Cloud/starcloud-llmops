package com.starcloud.ops.business.dataset.mq.consumer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;

@Slf4j
@Component
public class DataSetSourceDataCleanSendConsumer extends AbstractDataProcessor<DatasetSourceDataCleanSendMessage> {

    @Resource
    private FileApi fileApi;

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    private static final String PATH_OBJECT = "dataset-source-data/clean/";


    /**
     * @param message
     */
    @Override
    protected void setDataState(DatasetSourceSendMessage message) {
        message.setStatus(DataSetSourceDataStatusEnum.CLEANING_IN.getStatus());
        message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_IN.getName());
    }

    /**
     * @param message
     */
    @Override
    protected void processBusinessLogic(DatasetSourceSendMessage message) {
        log.info("开始清洗数据，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());

        // 根据数据源 ID获取数据储存ID
        DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());

        // 根据储存ID 获取存储地址
        DatasetStorageDO storageDO = selectDatasetStorage(sourceDataDO.getStorageId());

        Tika tika = new Tika();
        try {

            // 获取文件数据
            String text = tika.parseToString(new URL(storageDO.getStorageKey()));
            // 执行数据清洗
            String cleanText = TextCleanUtils.cleanText(text, message.getSplitRule());

            // 存储清洗数据
            String cleanPath = uploadFile(cleanText, message.getUserId());

            // TODO 总结流程暂时不做修改
            String summary;
            try {
                // 开始总结内容
                summary = documentSegmentsService.segmentSummary(String.valueOf(message.getDataSourceId()), cleanText, message.getSplitRule(), 500);
                sourceDataDO.setSummary(summary);
            } catch (RuntimeException e) {
                sourceDataDO.setSummary(null);
                log.error("清洗过程中，生成总结内容，总结内容生成失败");
            }

            // 保存清洗地址
            Long cleanId = setStorageData(message.getDataSourceId() + "_clean", cleanPath, (long) cleanPath.getBytes().length, "text/html", "html", message.getUserId());


            if (StrUtil.isBlank(sourceDataDO.getDescription())){
                sourceDataDO.setDescription(truncateAndSetContent(cleanText));
            }
            sourceDataDO.setCleanStorageId(cleanId);

            datasetSourceDataService.updateDatasourceById(sourceDataDO);

            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getName());

            log.info("清洗数据完毕，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());
        } catch (Exception e) {
            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
            message.setErrMsg(e.getMessage());
            log.info("清洗失败，错误原因是:({})",e.getMessage(),e);
        }

    }


    /**
     * @param message
     */
    @Override
    protected void sendMessage(DatasetSourceSendMessage message) {

        if (Objects.equals(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus(), message.getStatus())){
            throw new RuntimeException(DataSetSourceDataStatusEnum.CLEANING_ERROR.getName());
        }

        if (message.getSync()) {
            dataSplitProducer.sendMessage(message);
        } else {
            // 发送消息
            dataSplitProducer.asyncSendMessage(message);

        }
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

    private Long setStorageData(String sourceName, String storageAddress, Long size, String mimeType, String extension, Long userId) {
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

    private DatasetStorageDO selectDatasetStorage(Long id) {
        return datasetStorageMapper.selectById(id);

    }

    /**
     * 根据清洗内容 返回预设描述信息
     *
     * @param input
     * @return
     */
    private static String truncateAndSetContent(String input) {
        if (StrUtil.isBlank(input)) {
            return ""; // 如果输入为空，返回空字符串
        }

        if (input.length() <= 300) {
            return input; // 如果长度不足 300，返回全部字符串
        }

        return input.substring(0, 300); // 截取前 300 字符
    }
}