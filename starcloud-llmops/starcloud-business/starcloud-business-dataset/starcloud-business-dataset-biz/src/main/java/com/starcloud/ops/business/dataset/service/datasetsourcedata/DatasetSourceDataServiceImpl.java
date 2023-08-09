package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceIndoDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;


/**
 * 数据集源数据 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class DatasetSourceDataServiceImpl implements DatasetSourceDataService {

    @Resource
    private ProcessingService processingService;

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetStorageService datasetStorageService;

    @Autowired
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    /**
     * 更新数据集状态
     *
     * @param id 数据集源数据ID
     */
    @Override
    public DatasetSourceDataDO selectDataById(Long id) {
        return datasetSourceDataMapper.selectById(id);
    }

    @Override
    public Long createDatasetSourceData(String datasetId, Long storageId, String sourceName, Long wordCount) {
        // 封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();

        wrapper.eq(DatasetSourceDataDO::getDatasetId, datasetId);
        // 获取当前文件位置
        long position = datasetSourceDataMapper.selectCount(wrapper) + 1;

        DatasetSourceDataDO dataDO = new DatasetSourceDataDO();
        dataDO.setUid(DatasetUID.createSourceDataUID());
        dataDO.setName(sourceName);
        dataDO.setStorageId(storageId);
        dataDO.setPosition(position);
        dataDO.setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
        dataDO.setWordCount(wordCount);
        dataDO.setDatasetId(datasetId);

        datasetSourceDataMapper.insert(dataDO);
        return dataDO.getId();
    }

    /**
     * 上传文件-支持批量上传
     *
     * @param file
     * @param reqVO
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadFilesSourceData(MultipartFile file, UploadFileReqVO reqVO) {

        validateSplitRule(reqVO, DataSourceDataTypeEnum.URL.name());
        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();

        // 读取文件内容到字节数组中
        byte[] fileContent;
        try {
            fileContent = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = processingService.fileProcessing(file, fileContent, reqVO, DataSourceDataModelEnum.DOCUMENT.getStatus(), DataSourceDataTypeEnum.DOCUMENT.name());

        sourceDataUrlUploadDTO.setDatasetId(reqVO.getDatasetId());
        sourceDataUrlUploadDTO.setBatch(reqVO.getBatch());

        if (result == null) {
            sourceDataUrlUploadDTO.setStatus(false);
            sourceDataUrlUploadDTO.setSourceDataId(null);
        } else {
            sourceDataUrlUploadDTO.setStatus(true);
            sourceDataUrlUploadDTO.setSourceDataId(result);
        }


        return sourceDataUrlUploadDTO;
    }


    /**
     * 上传文件-支持批量上传
     *
     * @param reqVO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadUrlsSourceData(UploadUrlReqVO reqVO) {

        // UploadReqVO uploadReqVO = validateSplitRule(reqVO, DataSourceDataTypeEnum.URL.name());
        //
        // UploadUrlReqVO uploadUrlReqVO = (UploadUrlReqVO) uploadReqVO;
        // uploadUrlReqVO.setUrls(reqVO.getUrls());
        // reqVO = uploadUrlReqVO;
        validateSplitRule(reqVO, DataSourceDataTypeEnum.URL.name());


        // 异步处理
        UploadUrlReqVO finalReqVO = reqVO;

        List<SourceDataUploadDTO> collect = reqVO.getUrls().stream()
                .map(url -> {
                    SourceDataUploadDTO sourceDataUploadDTO = new SourceDataUploadDTO();
                    sourceDataUploadDTO.setDatasetId(finalReqVO.getDatasetId());
                    sourceDataUploadDTO.setBatch(finalReqVO.getBatch());

                    ListenableFuture<String> executed = this.executeAsyncWithUrl(url, finalReqVO);

                    try {
                        String result = executed.get();
                        if (result == null) {
                            sourceDataUploadDTO.setStatus(false);
                            sourceDataUploadDTO.setSourceDataId(null);
                        } else {
                            sourceDataUploadDTO.setStatus(true);
                            sourceDataUploadDTO.setSourceDataId(result);
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                    return sourceDataUploadDTO;
                }).collect(Collectors.toList());


        return collect;
    }

    /**
     * 上传URL -
     *
     * @param reqVO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadUrlsAndCreateDataset(UploadUrlReqVO reqVO) {
        // 判断数据集是否存在，不存在则创建数据集
        try {
            datasetsService.validateDatasetsExists(reqVO.getDatasetId());
        } catch (Exception e) {
            log.info("应用{}不存在数据集，开始创建数据集，数据集 UID 为应用 ID", reqVO.getDatasetId());
            String datasetName = String.format("应用%s的数据集", reqVO.getDatasetId());
            datasetsService.createDatasetsByApplication(reqVO.getDatasetId(), datasetName);
        }
        return uploadUrlsSourceData(reqVO);
    }


    @Async
    public ListenableFuture<String> executeAsyncWithUrl(String url, UploadUrlReqVO reqVO) {
        return AsyncResult.forValue(processingService.urlProcessing(url, reqVO, DataSourceDataModelEnum.DOCUMENT.getStatus(), DataSourceDataTypeEnum.URL.name()));
    }


    /**
     * 上传文件-支持批量上传
     *
     * @param reqVOS
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadCharactersSourceData(List<UploadCharacterReqVO> reqVOS) {

        return reqVOS.stream().map(reqVO -> {
            SourceDataUploadDTO sourceDataUploadDTO = new SourceDataUploadDTO();
            sourceDataUploadDTO.setDatasetId(reqVO.getDatasetId());
            sourceDataUploadDTO.setBatch(reqVO.getBatch());

            String result = processingService.stringProcessing(reqVO, DataSourceDataModelEnum.DOCUMENT.getStatus(), DataSourceDataTypeEnum.CHARACTERS.name());

            if (result == null) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setSourceDataId(null);
            } else {
                sourceDataUploadDTO.setStatus(true);
                sourceDataUploadDTO.setSourceDataId(result);
            }
            return sourceDataUploadDTO;
        }).collect(Collectors.toList());
    }


    @Override
    public List<Long> batchCreateDatasetSourceData(String datasetId, List<SourceDataBatchCreateReqVO> batchCreateReqVOS) {

        // 封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();

        wrapper.eq(DatasetSourceDataDO::getDatasetId, datasetId);
        // 获取当前文件位置
        AtomicLong position = new AtomicLong(datasetSourceDataMapper.selectCount(wrapper) + 1);

        // 批量封装数据
        List<DatasetSourceDataDO> datasetSourceDataDOList = batchCreateReqVOS.stream()
                .map(reqVO -> {
                            DatasetSourceDataDO dataDO = new DatasetSourceDataDO();
                            dataDO.setUid(DatasetUID.createSourceDataUID());
                            dataDO.setName(reqVO.getSourceName());
                            dataDO.setStorageId(reqVO.getStorageId());
                            dataDO.setPosition(position.getAndIncrement());
                            dataDO.setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
                            dataDO.setWordCount(reqVO.getWordCount());
                            dataDO.setDatasetId(datasetId);
                            return dataDO;
                        }
                )
                .collect(Collectors.toList());
        datasetSourceDataMapper.insertBatch(datasetSourceDataDOList);
        return datasetSourceDataDOList.stream().map(DatasetSourceDataDO::getId).collect(Collectors.toList());
    }

    /**
     * 更新数据集源数据
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateDatasetSourceData(DatasetSourceDataUpdateReqVO updateReqVO) {

    }


    @Override
    public void deleteDatasetSourceData(String uid) {
        // 校验存在
        validateDatasetSourceDataExists(uid);
        // 删除
        datasetSourceDataMapper.delete(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid));
    }

    private void validateDatasetSourceDataExists(String uid) {
        if (datasetSourceDataMapper.selectOne(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid)) == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
    }

    private UploadReqVO validateSplitRule(UploadReqVO reqVO, String dataType) {
        if (reqVO.getSplitRule() == null) {
            DataSourceDataTypeEnum dataTypeEnum = DataSourceDataTypeEnum.valueOf(dataType);
            SplitRule splitRule = new SplitRule();
            splitRule.setAutomatic(true);
            splitRule.setRemoveExtraSpaces(true);
            splitRule.setSeparator(null);
            switch (dataTypeEnum) {
                case URL:
                    splitRule.setRemoveUrlsEmails(false);
                    splitRule.setChunkSize(2000);
                    break;
                case DOCUMENT:
                case CHARACTERS:
                    splitRule.setRemoveUrlsEmails(true);
                    splitRule.setChunkSize(3000);
                    break;
            }
            reqVO.setSplitRule(splitRule);
        }
        return reqVO;
    }


    private void validateDatasetSourceDataExists(String filedId, String datasetID) {
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                .eq(DatasetSourceDataDO::getDatasetId, datasetID).eq(DatasetSourceDataDO::getStorageId, filedId);
        Long aLong = datasetSourceDataMapper.selectCount(wrapper);
        if (datasetSourceDataMapper.selectCount(wrapper) > 0) {
            throw exception(DATASET_SOURCE_DATA_EXISTS);
        }
    }


    @Override
    public PageResult<DatasetSourceDataDO> getDatasetSourceDataPage(DatasetSourceDataPageReqVO pageReqVO) {
        return datasetSourceDataMapper.selectPage(pageReqVO);
    }


    @Override
    public List<DatasetSourceDataRespVO> getDatasetSourceDataList(String datasetId, Integer dataModel) {

        List<DatasetSourceDataDO> datasetSourceDataDOS = datasetSourceDataMapper.selectByDatasetId(datasetId, dataModel);

        return datasetSourceDataDOS.stream().map(dataDO -> {

                    DatasetSourceDataRespVO dataRespVO = new DatasetSourceDataRespVO();
                    dataRespVO.setId(dataDO.getId());
                    dataRespVO.setUid(dataDO.getUid());
                    dataRespVO.setName(dataDO.getName());
                    dataRespVO.setDataModel(dataDO.getDataModel());
                    dataRespVO.setDataType(dataDO.getDataType());
                    dataRespVO.setBatch(dataDO.getBatch());
                    dataRespVO.setStatus(String.valueOf(dataDO.getStatus()));
                    dataRespVO.setWordCount(dataDO.getWordCount());
                    if (dataDO.getDataSourceInfo() != null) {
                        DataSourceIndoDTO dataSourceIndoDTO = JSONObject.parseObject(dataDO.getDataSourceInfo(), DataSourceIndoDTO.class);
                        if (dataSourceIndoDTO.getSummaryContent() != null) {
                            // 设置总结内容
                            dataRespVO.setSummaryContent(dataSourceIndoDTO.getSummaryContent());
                        }
                    }

                    return dataRespVO;
                }
        ).collect(Collectors.toList());

    }

    /**
     * 获取数据源详情
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public DatasetSourceDataDetailsInfoVO getSourceDataDetailsInfo(String uid, Boolean enable) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, uid));

        if (sourceDataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }

        DatasetSourceDataDetailsInfoVO datasetSourceDataDetailsInfoVO = BeanUtil.copyProperties(sourceDataDO, DatasetSourceDataDetailsInfoVO.class);

        if (DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus() < sourceDataDO.getStatus() && sourceDataDO.getDataSourceInfo() != null) {
            DataSourceIndoDTO dataSourceIndoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceIndoDTO.class);

            if (dataSourceIndoDTO.getSummaryContent() != null) {
                // 设置总结内容
                datasetSourceDataDetailsInfoVO.setSummaryContent(dataSourceIndoDTO.getSummaryContent());
            }

            if (enable && dataSourceIndoDTO.getCleanId() > 0) {
                // 设置清洗内容
                Long cleanId = dataSourceIndoDTO.getCleanId();

                DatasetStorageDO cleanDatasetDO = datasetStorageService.selectDataById(cleanId);
                if (cleanDatasetDO != null) {
                    byte[] bytes = HttpUtil.downloadBytes(cleanDatasetDO.getStorageKey());
                    String result = new String(bytes);
                    datasetSourceDataDetailsInfoVO.setCleanContent(result);
                }
            }
        }


        return datasetSourceDataDetailsInfoVO;
    }

    /**
     * @param reqVO
     * @return
     */
    @Override
    public PageResult<DatasetSourceDataSplitPageRespVO> getSplitDetails(DatasetSourceDataSplitPageReqVO reqVO) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, reqVO.getUid()));

        if (sourceDataDO != null) {
            SegmentPageQuery segmentPageQuery = BeanUtil.copyProperties(reqVO, SegmentPageQuery.class);

            segmentPageQuery.setDocumentUid(String.valueOf(sourceDataDO.getId()));

            PageResult<DocumentSegmentDO> documentSegmentDOPageResult = documentSegmentsService.segmentDetail(segmentPageQuery);

            return DatasetSourceDataConvert.INSTANCE.convertSplitPage(documentSegmentDOPageResult);
        } else {
            PageResult<DatasetSourceDataSplitPageRespVO> pageResult = new PageResult<DatasetSourceDataSplitPageRespVO>();
            pageResult.setList(null);
            pageResult.setTotal(0L);
            return pageResult;
        }


    }


    /**
     * 归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public void archivedDatasetSourceData(String uid) {
        // 判断当前源数据状态
        if (archivedStatus(uid)) {
            // 更新数据
            LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
            wrapper.eq(DatasetSourceDataDO::getUid, uid);
            wrapper.set(DatasetSourceDataDO::getArchived, true);
            datasetSourceDataMapper.update(null, wrapper);
        } else {
            throw exception(DATASET_SOURCE_DATA_ARCHIVED);
        }

    }

    /**
     * 取消归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public void unArchivedDatasetSourceData(String uid) {
        // 判断当前源数据状态
        if (archivedStatus(uid)) {
            // 更新数据
            LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
            wrapper.eq(DatasetSourceDataDO::getUid, uid);
            wrapper.set(DatasetSourceDataDO::getArchived, true);
            datasetSourceDataMapper.update(null, wrapper);
        } else {
            throw exception(DATASET_SOURCE_DATA_UNARCHIVED);
        }
    }

    /**
     * 更新据集源数据状态
     *
     * @param id     数据集源数据编号
     * @param status
     */
    @Override
    public void updateDatasourceStatusAndMessage(Long id, Integer status, String message) {
        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getId, id);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
        wrapper.set(DatasetSourceDataDO::getErrorMessage, message);
        datasetSourceDataMapper.update(null, wrapper);
    }


    /**
     * 更新数据集状态
     *
     * @param id 数据集源数据编号
     */
    @Override
    public void updateDatasourceAndSourceInfo(Long id, Integer status, String dataSourceInfo, Long userId) {

        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getId, id);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
        wrapper.set(DatasetSourceDataDO::getUpdater, userId);
        wrapper.set(DatasetSourceDataDO::getDataSourceInfo, dataSourceInfo);
        datasetSourceDataMapper.update(null, wrapper);

    }

    private Boolean archivedStatus(String uid) {
        DatasetSourceDataDO datasetSourceDataDO = datasetSourceDataMapper.selectOne(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid));
        return datasetSourceDataDO.getArchived();
    }


    public static int getFileCharacterCountFromURL(String fileUrl) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(fileUrl).openStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString().length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}