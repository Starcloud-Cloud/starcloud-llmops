package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.SourceDataBatchCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadFileRespDTO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadRespDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUrlUploadDTO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private DatasetSourceDataMapper datasetSourceDataMapper;

    @Override
    public Long createDatasetSourceData(String datasetId, Long storageId, String sourceName, Long wordCount) {

        // TODO 校验数据集是否存在

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
     * @param files
     * @param splitRule
     * @param datasetId
     * @return 编号
     */
    @Override
    public SourceDataUrlUploadDTO uploadFilesSourceData(MultipartFile[] files, SplitRule splitRule, String datasetId) {


        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = new SourceDataUrlUploadDTO();

        ArrayList<Boolean> arrayList = new ArrayList<>();
        // 生成批次
        String batch = IdUtil.getSnowflakeNextIdStr();
        List<Future<Boolean>> completableFutures = Stream.of(files)
                .map(file -> processFileUploadAsync(file, splitRule, datasetId))
                .collect(Collectors.toList());

        List<Boolean> source = completableFutures.stream()
                .map(Future::isDone) // 获取任务结果，如果有异常，join 会抛出 ExecutionException
                .collect(Collectors.toList());

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setBatch(batch);
        sourceDataUrlUploadDTO.setStatus(source);
        return sourceDataUrlUploadDTO;
    }


    @Async
    public Future<Boolean> processFileUploadAsync(MultipartFile file, SplitRule splitRule, String datasetId) {
        // 读取文件内容到字节数组中
        byte[] fileContent;
        try {
            fileContent = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return AsyncResult.forValue(processingService.fileProcessing(file, fileContent, splitRule, datasetId));
    }

    /**
     * 上传文件-支持批量上传
     *
     * @param urls
     * @param splitRule
     * @param datasetId
     * @return 编号
     */
    @Override
    public SourceDataUrlUploadDTO uploadUrlsSourceData(List<String> urls, SplitRule splitRule, String datasetId) {
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = new SourceDataUrlUploadDTO();

        // 生成批次
        String batch = IdUtil.getSnowflakeNextIdStr();

        List<CompletableFuture<Boolean>> completableFutures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() ->{
                            TenantContextHolder.setIgnore(false);
                            TenantContextHolder.setTenantId(tenantId);
                            RequestContextHolder.setRequestAttributes(requestAttributes);
                            UserContextHolder.setUserId(userId);
                            processingService.urlProcessing(url, splitRule, datasetId);
                        }
                      )
                .collect(Collectors.toList());

        // 等待所有异步任务完成
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();

        // 处理每个任务的结果，并收集结果到 source 列表中
        List<Boolean> source = completableFutures.stream()
                .map(CompletableFuture::join) // 获取任务结果，如果有异常，join 会抛出 ExecutionException
                .collect(Collectors.toList());

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setStatus(source);
        return sourceDataUrlUploadDTO;
    }

    public Future<Boolean> processUrlUploadAsync(String url, SplitRule splitRule, String datasetId) {
        // 读取文件内容到字节数组中
        byte[] fileContent;
        try {
            fileContent = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return AsyncResult.forValue(processingService.fileProcessing(file, fileContent, splitRule, datasetId));
    }


    /**
     * 上传文件-支持批量上传
     *
     * @param characters
     * @param splitRule
     * @param datasetId
     * @return 编号
     */
    @Override
    public SourceDataUrlUploadDTO uploadCharactersSourceData(List<String> characters, SplitRule splitRule, String datasetId) {
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = new SourceDataUrlUploadDTO();

        // 生成批次
        String batch = IdUtil.getSnowflakeNextIdStr();

        List<CompletableFuture<Boolean>> completableFutures = characters.stream()
                .map(character -> CompletableFuture.supplyAsync(() -> processingService.urlProcessing(character, splitRule, datasetId)))
                .collect(Collectors.toList());

        // 等待所有异步任务完成
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();

        // 处理每个任务的结果，并收集结果到 source 列表中
        List<Boolean> source = completableFutures.stream()
                .map(CompletableFuture::join) // 获取任务结果，如果有异常，join 会抛出 ExecutionException
                .collect(Collectors.toList());

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setStatus(source);
        return sourceDataUrlUploadDTO;
    }


    @Override
    public List<Long> batchCreateDatasetSourceData(String datasetId, List<SourceDataBatchCreateReqVO> batchCreateReqVOS) {

        // TODO 校验数据集是否存在

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
     * @param uid    数据集源数据编号
     * @param status
     */
    @Override
    public void updateDatasourceStatus(String uid, Integer status) {
        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getUid, uid);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
        datasetSourceDataMapper.update(null, wrapper);
    }

    /**
     * 更新数据集状态
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public void updateDatasourceAndSourceInfo(String uid, Integer status, String dataSourceInfo) {

        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getUid, uid);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
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