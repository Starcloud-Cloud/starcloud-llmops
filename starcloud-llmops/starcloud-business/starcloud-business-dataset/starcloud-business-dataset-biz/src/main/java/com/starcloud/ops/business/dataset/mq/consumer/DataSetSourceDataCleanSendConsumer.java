package com.starcloud.ops.business.dataset.mq.consumer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRule;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_SOURCE_DATA_NOT_EXISTS;

@Slf4j
@Component
public class DataSetSourceDataCleanSendConsumer extends AbstractDataProcessor<DatasetSourceDataCleanSendMessage> {


    @Resource
    private FileApi fileApi;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Resource
    private DocumentSegmentsService documentSegmentsService;


    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetDataHandleRulesService datasetDataHandleRulesService;

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

        int retryCount = message.getRetryCount();
        try {
            // 获取数据集下的处理规则
            DatasetHandleRulesRespVO rulesRespVO = datasetDataHandleRulesService.getRuleByDatasetId(message.getDatasetId());

            // 根据数据源 ID获取数据储存ID
            DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());

            // 根据储存ID 获取存储地址
            DatasetStorageDO storageDO = selectDatasetStorage(sourceDataDO.getStorageId());
            if (storageDO == null) {
                log.error("清洗过程中，获取数据源失败，请检查数据信息，message 是({})", message);
                throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
            }
            Tika tika = new Tika();
            // 获取文件数据
            String text = tika.parseToString(new URL(storageDO.getStorageKey()));

            CleanRule cleanRule = getCleanRuleBySourceType(sourceDataDO.getDataType(), rulesRespVO);
            // 执行数据清洗
            String cleanText = TextCleanAndSplitUtils.cleanText(text, sourceDataDO.getDataType(), cleanRule);

            // 格式转换
            String formatText = TextCleanAndSplitUtils.processFormat(cleanText, cleanRule.getConvertFormat(), sourceDataDO.getDataType());

            // 数据上传
            String cleanPath = uploadFile(cleanText, message.getUserId(), formatText);

            // 保存清洗地址
            Long cleanId = setStorageData(message.getDataSourceId() + "_clean", cleanPath, (long) formatText.length(), "text/html", cleanRule.getConvertFormat(), message.getUserId());


            if (StrUtil.isBlank(sourceDataDO.getDescription())) {
                sourceDataDO.setDescription(truncateAndSetContent(cleanText, sourceDataDO.getDataType(), cleanRule.getConvertFormat()));
            }
            sourceDataDO.setCleanStorageId(cleanId);
            sourceDataDO.setDatasetProcessRuleId(Arrays.asList(rulesRespVO.getId()).toString());

            datasetSourceDataService.updateDatasourceById(sourceDataDO);

            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getName());
            // FIXME: 2023/8/22  后续由流程自己获取
            message.setSplitRule(rulesRespVO.getSplitRule());
            log.info("清洗数据完毕，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());
        } catch (Exception e) {
            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_ERROR.getName());
            message.setRetryCount(++retryCount);
            log.info("清洗失败，错误原因是:({})", e.getMessage(), e);
        }

    }


    /**
     * @param message
     */
    @Override
    protected void sendMessage(DatasetSourceSendMessage message) {

        if (0 == dictDataService.getDictData("QueueSwitch", "sendMessage").getStatus()) {

            if (Objects.equals(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus(), message.getStatus())) {
                throw new RuntimeException(DataSetSourceDataStatusEnum.CLEANING_ERROR.getName());
            }

            if (message.getSync()) {
                dataSplitProducer.sendMessage(message);
            } else {
                // 发送消息
                dataSplitProducer.asyncSendMessage(message);

            }
        }


    }


    private String uploadFile(String data, Long userId, String formatSuffix) {

        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);
        // 生成文件ID-生成
        String fileId = SecureUtil.md5(data);


        String fileName = fileId + "." + "formatSuffix";
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
    private static String truncateAndSetContent(String input, String dataType, String format) {
        if (DataSourceDataTypeEnum.URL.name().equals(dataType) && DataSourceDataFormatEnum.MARKDOWN.name().equals(format)) {
            input= Jsoup.parse(input).text();
        }
        if (StrUtil.isBlank(input)) {
            return ""; // 如果输入为空，返回空字符串
        }

        if (input.length() <= 300) {
            return input; // 如果长度不足 300，返回全部字符串
        }

        return input.substring(0, 300); // 截取前 300 字符
    }

    /**
     * 根据数据类型返回清洗规则
     *
     * @param dataType
     * @param rulesRespVO
     * @return
     */
    private CleanRule getCleanRuleBySourceType(String dataType, DatasetHandleRulesRespVO rulesRespVO) {
        DataSourceDataTypeEnum dataSourceDataTypeEnum = DataSourceDataTypeEnum.valueOf(dataType);

        switch (dataSourceDataTypeEnum) {
            case URL:
                return rulesRespVO.getCleanRuleVO().getURL();
            case CHARACTERS:
                return rulesRespVO.getCleanRuleVO().getCHARACTERS();
            case DOCUMENT:
                return rulesRespVO.getCleanRuleVO().getDOCUMENT();
            default:
                return rulesRespVO.getCleanRuleVO().getCHARACTERS();
        }
    }

}