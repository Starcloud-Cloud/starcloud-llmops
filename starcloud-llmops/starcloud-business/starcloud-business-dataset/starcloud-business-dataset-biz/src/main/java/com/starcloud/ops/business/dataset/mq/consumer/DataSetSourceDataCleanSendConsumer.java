package com.starcloud.ops.business.dataset.mq.consumer;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataSplitProducer;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceIndoDTO;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    private FileApi fileApi;

    @Resource
    private DatasetSourceDataSplitProducer dataSplitProducer;

    @Resource
    private DatasetStorageService datasetStorageService;

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

            // 清洗后数据存储 文件存储
            String cleanPath = uploadFile(cleanText, "txt");

            Long cleanId = this.setStorageData(message.getDataSourceId() + "_clean", cleanPath, (long) cleanPath.getBytes().length, "text/html", "html");
            DataSourceIndoDTO DataSourceIndoDTO = new DataSourceIndoDTO();
            DataSourceIndoDTO.setCleanId(cleanId);
            datasetSourceDataService.updateDatasourceAndSourceInfo(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus(), JSONObject.toJSONString(DataSourceIndoDTO));
            // 发送消息
            dataSplitProducer.sendSplitDatasetsSendMessage(message.getDatasetId(), message.getDataSourceId(), message.getSplitRule(), cleanText);
        } catch (Exception e) {
            log.error("[DataSetSourceDataCleanSendConsumer][数据清洗失败：用户ID({})|租户 ID({})｜数据集 ID({})｜源数据 ID({})", getLoginUserId(), getTenantId(), message.getDataSourceId(), message.getDataSourceId());
            // 设置数据源状态为清洗中
            datasetSourceDataService.updateDatasourceStatus(message.getDataSourceId(), DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus());
        }
        // 设置数据源状态为清洗结束

    }

    private String uploadFile(String data, String fileType) {

        // 将结果转换为InputStream流
        InputStream utf8Stream = IoUtil.toUtf8Stream(data);
        // 生成文件ID-生成
        String fileId = SecureUtil.md5(data);
        String path = fileId + "." + fileType;

        return fileApi.createFile(path, IoUtil.readBytes(utf8Stream));
    }

    public Long setStorageData(String sourceName, String storageAddress, Long size, String mimeType, String extension) {
        DatasetStorageCreateReqVO createReqVO = new DatasetStorageCreateReqVO();
        createReqVO.setUid(IdUtil.getSnowflakeNextIdStr());
        createReqVO.setName(sourceName);
        createReqVO.setStorageKey(storageAddress);
        createReqVO.setType(extension.toUpperCase());
        createReqVO.setSize(size);
        createReqVO.setMimeType(mimeType.toUpperCase());
        createReqVO.setUsed(false);
        // 数据入库
        return datasetStorageService.addStorageData(createReqVO);
    }

}