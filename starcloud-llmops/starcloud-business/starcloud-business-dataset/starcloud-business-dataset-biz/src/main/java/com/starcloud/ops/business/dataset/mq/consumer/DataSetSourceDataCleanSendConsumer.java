package com.starcloud.ops.business.dataset.mq.consumer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.HandleRuleProcessResultRespVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Component
public class DataSetSourceDataCleanSendConsumer extends AbstractDataProcessor<DatasetSourceDataCleanSendMessage> {


    @Resource
    private FileApi fileApi;


    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;


    @Resource
    private DatasetSourceDataCleanProducer dataCleanProducer;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;


    @Resource
    private DatasetDataHandleRulesService datasetDataHandleRulesService;

    private static final String PATH_OBJECT = "dataset-source-data/clean/";


    /**
     * @param message message 信息
     */
    @Override
    protected void setDataState(DatasetSourceSendMessage message) {
        message.setStatus(DataSetSourceDataStatusEnum.CLEANING_IN.getStatus());
        message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_IN.getName());
    }

    /**
     * @param message message 信息
     */
    @Override
    protected void processBusinessLogic(DatasetSourceSendMessage message) {
        log.info("开始清洗数据，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());

        try {
            // 根据数据源 ID获取数据储存ID
            DatasetSourceDataDO sourceDataDO = datasetSourceDataService.selectDataById(message.getDataSourceId());
            // 执行数据清洗
            HandleRuleProcessResultRespVO cleanResultVO = datasetDataHandleRulesService.processDataClean(sourceDataDO);

            // 数据上传
            String cleanPath = uploadFile(cleanResultVO.getResult(), message.getUserId(), cleanResultVO.getFormatSuffix());

            // 保存清洗地址
            Long cleanId = setStorageData(cleanResultVO.getResultName(), cleanPath, cleanResultVO.getConvertFormat(), (long) cleanResultVO.getResult().length(), message.getUserId(), message.getTenantId());

            // 获取描述
            String descriptionContent = getDescriptionContent(sourceDataDO, cleanResultVO.getResult());

            // 设置描述
            sourceDataDO.setDescription(descriptionContent);
            sourceDataDO.setCleanStorageId(cleanId);
            sourceDataDO.setRuleId(cleanResultVO.getRuleId());

            // 更新数据
            datasetSourceDataService.updateDatasourceById(sourceDataDO);

            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus());
            message.setErrMsg(DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getName());
            message.setSplitRule(cleanResultVO.getSplitRule());
            log.info("清洗数据完毕，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());
        } catch (Exception e) {
            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_RETRY.getStatus());
            message.setErrCode(DataSetSourceDataStatusEnum.CLEANING_RETRY.getStatus());
            message.setErrMsg(e.getMessage());
            log.info("清洗失败，错误原因是:({})", e.getMessage(), e);
        }

    }


    /**
     * 发送信息
     *
     * @param message message 信息
     */
    @Override
    protected void sendMessage(DatasetSourceSendMessage message) {
        if (Objects.equals(message.getStatus(), DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus())) {
            // 如果执行成功 重置重试次数
            message.setRetryCount(0);
            // 发送消息
            if (message.getSplitSync()) {
                log.info("同步执行数据分块操作，数据为{}", JSONObject.toJSONString(message));
                dataSplitProducer.sendMessage(message);
            } else {
                log.info("异步执行数据分块操作，数据为{}", JSONObject.toJSONString(message));
                // 发送消息
                dataSplitProducer.asyncSendMessage(message);
            }

        } else if (message.getRetryCount() < 3 && Objects.equals(DataSetSourceDataStatusEnum.CLEANING_RETRY.getStatus(), message.getStatus())) {
            int retryCount = message.getRetryCount();
            message.setRetryCount(++retryCount);
            log.warn("数据清洗异常，开始重试，当前重试次数为{}", message.getRetryCount());
            if (message.getCleanSync()) {
                log.info("同步执行数据清洗操作，数据为{}", JSONObject.toJSONString(message));
                dataCleanProducer.sendMessage(message);
            } else {
                log.info("异步执行数据清洗操作，数据为{}", JSONObject.toJSONString(message));
                // 发送消息
                dataCleanProducer.asyncSendMessage(message);
            }
        } else {
            message.setStatus(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
            message.setErrCode(DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
            message.setErrMsg(message.getErrMsg());
            updateDataState(message);
            log.error("执行数据清洗失败，重试失败！！！数据为{}", JSONObject.toJSONString(message));
        }


    }

    /**
     * 文件上传
     *
     * @param data         清洗后的数据
     * @param userId       用户 ID
     * @param formatSuffix 文件后缀
     * @return 上传后的地址
     */
    private String uploadFile(String data, Long userId, String formatSuffix) {

        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);
        // 生成文件ID-生成
        String fileId = SecureUtil.md5(data);


        String fileName = fileId + formatSuffix;
        String path = String.format(PATH_OBJECT + "%s" + "/", userId) + fileName;

        return fileApi.createFile(fileName, path, IoUtil.readBytes(utf8Stream));
    }

    /**
     * 设置存储信息
     *
     * @param sourceName     资源名称
     * @param storageAddress 资源地址
     * @param size           大小
     * @param userId         用户 ID
     * @return 存储ID
     */
    private Long setStorageData(String sourceName, String storageAddress, String type, Long size, Long userId, Long tenantId) {
        DatasetStorageDO datasetStorageDO = new DatasetStorageDO();

        datasetStorageDO.setUid(IdUtil.getSnowflakeNextIdStr());
        datasetStorageDO.setName(sourceName);
        datasetStorageDO.setStorageKey(storageAddress);
        datasetStorageDO.setType(type);
        datasetStorageDO.setSize(size);
        datasetStorageDO.setMimeType(getMimeType(getExtension(sourceName)));
        datasetStorageDO.setUsed(false);
        datasetStorageDO.setCreator(String.valueOf(userId));
        datasetStorageDO.setUpdater(String.valueOf(userId));
        datasetStorageDO.setTenantId(tenantId);

        datasetStorageMapper.insert(datasetStorageDO);
        // 数据入库
        return datasetStorageDO.getId();
    }


    /**
     * 根据清洗内容 返回预设描述信息
     *
     * @param sourceDataDO 源数据 DO
     * @param cleanText    清洗后的数据
     * @return 描述信息
     */
    private static String getDescriptionContent(DatasetSourceDataDO sourceDataDO, String cleanText) {

        if (StrUtil.isBlank(sourceDataDO.getDescription())) {

            if (DataSourceDataTypeEnum.HTML.name().equals(sourceDataDO.getDataType())) {
                DataSourceInfoDTO dataSourceInfoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceInfoDTO.class);
                String url = dataSourceInfoDTO.getInitAddress();
                try {
                    cleanText = Jsoup.connect(URLUtil.normalize(url)).get().text();
                } catch (IOException e) {
                    log.error("清洗过程中获取描述失败，请检查链接{}", url);
                    return ""; // 如果输入为空，返回空字符串
                }
            }
            if (StrUtil.isBlank(cleanText)) {
                return ""; // 如果输入为空，返回空字符串
            }

            if (cleanText.length() <= 300) {
                return cleanText; // 如果长度不足 300，返回全部字符串
            }

            return cleanText.substring(0, 300); // 截取前 300 字符


        }
        return sourceDataDO.getDescription();
    }

    private static String getMimeType(String extension) {
        switch (extension) {
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "md":
                return "text/x-markdown";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    private static String getExtension(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1).toLowerCase();
            }
        }
        return "";
    }

}