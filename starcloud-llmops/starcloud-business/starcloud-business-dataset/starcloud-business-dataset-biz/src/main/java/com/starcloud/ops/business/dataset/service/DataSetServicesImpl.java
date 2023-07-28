package com.starcloud.ops.business.dataset.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadRespDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUrlUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 数据集 流程处理
 */
@Deprecated
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
    public SourceDataUrlUploadDTO uploadWechatURLSourceData(List<String> urlList, SplitRule splitRule) {
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = new SourceDataUrlUploadDTO();

        // 上传的数据校验
        if (CollUtil.isEmpty(urlList)) {
            // 抛出异常 Url；列表不可以为空
            throw exception(111);
        }
        List<Boolean> source = new ArrayList<>();

        // 创建数据 存在则返回数据集 ID
        String datasetId = datasetsService.createWechatDatasets();


        // 遍历处理数据
        urlList.forEach(url -> {
            try {

                // 连接到URL并获取网页内容
                Document doc = Jsoup.connect(url).get();
                // 获取网页的title
                String title = getUrlTitle(doc);
                String result = doc.toString();

                // 将结果转换为InputStream流
                ByteArrayInputStream inputStream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
                // 生成文件ID - 使用 URL
                String fileId = SecureUtil.md5(url);
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

                SourceDataUploadRespDTO sourceDataUploadRespDTO = new SourceDataUploadRespDTO();
                sourceDataUploadRespDTO.setStatus(true);

                source.add(true);
            } catch (Exception e) {
                log.info("");
            }

        });

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setStatus(source);

        // // 遍历-异步发送信息到队列 处理数据
        // source.forEach(data -> {
        //             sendMQMessage(datasetId, String.valueOf(data.getSourceId()), splitRule, data.getStorageAddress());
        //         }
        // );


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

        String fileType;
        try {
            fileType = FileTypeUtil.getType(fileStream);
            if (fileType == null || "null".equals(fileType)) {
                fileType = "txt";
            }

        } catch (Exception e) {
            fileType = "txt";
        }

        if (StrUtil.isBlank(path)) {
            path = fileId + "." + fileType;
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


    public static String getUrlTitle(Document doc) {
        // 获取网页的meta标签
        Element meta = doc.select("meta[http-equiv=Content-Type], meta[charset]").first();
        String charset = meta != null ? meta.attr("charset") : null;

        // 如果charset为空，则默认使用UTF-8
        if (charset == null || charset.isEmpty()) {
            charset = CharsetUtil.UTF_8;
        }

        // 获取网页的title，使用实际编码进行解析
        return new String(doc.title().getBytes(StandardCharsets.UTF_8), Charset.forName(charset));
    }


}
