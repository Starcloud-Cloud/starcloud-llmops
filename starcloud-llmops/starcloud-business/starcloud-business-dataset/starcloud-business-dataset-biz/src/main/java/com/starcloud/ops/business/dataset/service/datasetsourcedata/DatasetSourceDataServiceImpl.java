package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadCharacterReqDTO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
import java.util.ArrayList;
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
     * @param file
     * @param splitRule
     * @param datasetId
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadFilesSourceData(MultipartFile file,String batch, SplitRule splitRule, String datasetId) {

        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();

        ArrayList<Boolean> source = new ArrayList<>();
        // 读取文件内容到字节数组中
        byte[] fileContent;
        try {
            fileContent = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Boolean booleanFuture = processingService.fileProcessing(file, fileContent, splitRule, datasetId);
        source.add(booleanFuture);


        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setBatch(batch);
        sourceDataUrlUploadDTO.setStatus(source);

        return sourceDataUrlUploadDTO;
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
    public SourceDataUploadDTO uploadUrlsSourceData(List<UploadUrlReqVO> urls,String batch, SplitRule splitRule, String datasetId) {

        // 校验 URL 是否合法
        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setBatch(batch);
        // 异步处理文件
        List<Boolean> source = urls.stream()
                .map(url -> {
                    ListenableFuture<Boolean> executed = this.executeAsyncWithUrl(url, batch, splitRule, datasetId);
                    try {
                        return executed.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        sourceDataUrlUploadDTO.setStatus(source);


        return sourceDataUrlUploadDTO;
    }


    @Async
    public ListenableFuture<Boolean> executeAsyncWithUrl(UploadUrlReqVO url,String batch, SplitRule splitRule, String datasetId) {
        return AsyncResult.forValue(processingService.urlProcessing(url.getUrl(), splitRule, datasetId));
    }


    /**
     * 上传文件-支持批量上传
     *
     * @param reqVOS
     * @param splitRule
     * @param datasetId
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadCharactersSourceData(List<UploadCharacterReqVO> reqVOS,String batch, SplitRule splitRule, String datasetId) {
        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();


        ArrayList<Boolean> source = new ArrayList<>();

        for (UploadCharacterReqVO reqVO : reqVOS) {

            UploadCharacterReqDTO bean = BeanUtil.toBean(reqVO, UploadCharacterReqDTO.class);
            Boolean aBoolean = processingService.stringProcessing(bean.getTitle(),bean.getContext(), splitRule, datasetId);
            source.add(aBoolean);
        }

        sourceDataUrlUploadDTO.setDatasetId(datasetId);
        sourceDataUrlUploadDTO.setBatch(batch);
        sourceDataUrlUploadDTO.setStatus(source);

        // List<CompletableFuture<Boolean>> completableFutures = characters.stream()
        //         .map(character -> CompletableFuture.supplyAsync(() -> processingService.urlProcessing(character, splitRule, datasetId)))
        //         .collect(Collectors.toList());
        //
        // // 等待所有异步任务完成
        // CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        //
        // // 处理每个任务的结果，并收集结果到 source 列表中
        // List<Boolean> source = completableFutures.stream()
        //         .map(CompletableFuture::join) // 获取任务结果，如果有异常，join 会抛出 ExecutionException
        //         .collect(Collectors.toList());
        //
        // sourceDataUrlUploadDTO.setDatasetId(datasetId);
        // sourceDataUrlUploadDTO.setStatus(source);
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


    @Override
    public List<DatasetSourceDataDO> getDatasetSourceDataList(String datasetId) {

        return datasetSourceDataMapper.selectByDatasetId(datasetId);
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