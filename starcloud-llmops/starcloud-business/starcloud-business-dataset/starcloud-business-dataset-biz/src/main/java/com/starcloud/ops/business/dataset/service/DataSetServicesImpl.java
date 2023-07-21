package com.starcloud.ops.business.dataset.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.SourceDataBatchCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.sourceDataUploadRespDTO;
import com.starcloud.ops.business.dataset.service.dto.sourceDataUrlUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 数据集 流程处理
 */

@Slf4j
@Service
public class DataSetServicesImpl {

    @Resource
    private FileApi fileApi;

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Resource
    private DatasetStorageService datasetStorageService;

    @Resource
    private DatasetSourceDataCleanProducer dataSetProducer;


    /**
     * 微信-上传URL
     *
     * @param urlList   Url 列表
     * @param datasetId 数据集 ID
     * @return String
     */
    public sourceDataUrlUploadDTO uploadWechatURLSourceData(List<String> urlList, SplitRule splitRule) {
        sourceDataUrlUploadDTO sourceDataUrlUploadDTO = new sourceDataUrlUploadDTO();

        // 上传的数据校验
        if (CollUtil.isEmpty(urlList)) {
            // 抛出异常 Url；列表不可以为空
            throw exception(111);
        }
        List<sourceDataUploadRespDTO> source = new ArrayList<>();

        // 创建数据 存在则返回数据集 ID
        String datasetId = datasetsService.createWechatDatasets();


        // 遍历处理数据
        urlList.forEach(url -> {
            try {

                // 执行HTTP GET请求并获取结果
                String result = HttpUtil.get(url);

                String title = ReUtil.findAll("(<title>|<TITLE>)(.*?)(</title>|</TITLE>)", result, 1).toString();
                // 将结果转换为InputStream流
                InputStream inputStream = new ByteArrayInputStream(result.getBytes());
                // 生成文件ID
                String fileId = SecureUtil.md5(inputStream);
                // 上传文件
                String filePath = uploadFile(fileId, inputStream, null);
                // 每次上传成功都执行保存 防止中间上传失败

                // 获取size
                long size;

                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    size = byteArrayOutputStream.toByteArray().length;

                } catch (Exception e) {
                    size = 0;
                }

                // 保存上传记录
                Long storageId = setStorageData(title, filePath, size, "text/html", "html");
                // 保存源数据信息 返回数据 id
                Long sourceDataId = datasetSourceDataService.createDatasetSourceData(datasetId, storageId, title, (long) result.length());

                sourceDataUploadRespDTO sourceDataUploadRespDTO = new sourceDataUploadRespDTO();
                sourceDataUploadRespDTO.setStorageId(storageId);
                sourceDataUploadRespDTO.setSourceId(sourceDataId);
                sourceDataUploadRespDTO.setStorageAddress(filePath);
                sourceDataUploadRespDTO.setDataType("html");
                source.add(sourceDataUploadRespDTO);
            } catch (Exception e) {
                log.info("");
            }

        });

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setSource(source);

        // 遍历-异步发送信息到队列 处理数据
        source.forEach(data -> {
                    sendMQMessage(datasetId, String.valueOf(data.getSourceId()), splitRule, data.getStorageAddress());
                }
        );


        // 返回数据
        return sourceDataUrlUploadDTO;
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


    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param fileId     文件 ID
     * @param fileStream 文件流
     * @param path       文件 path 可以为空
     * @return 文件路径
     */
    private String uploadFile(String fileId, InputStream fileStream, String path) {

        if (StrUtil.isBlank(path)) {
            path = fileId + "." + FileTypeUtil.getType(fileStream);
        }
        return fileApi.createFile(path, IoUtil.readBytes(fileStream));
    }


    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param datasetId    数据集 ID
     * @param dataSourceId 源数据 ID
     * @param splitRule    分割规则
     * @param filepath     文件路径
     */
    @Async
    protected void sendMQMessage(String datasetId, String dataSourceId, SplitRule splitRule, String filepath) {

        dataSetProducer.sendCleanDatasetsSendMessage(datasetId, dataSourceId, splitRule, filepath);
    }
}
