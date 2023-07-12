package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
    private DatasetStorageService datasetStorageService;
    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    @Resource
    private DatasetSourceDataCleanProducer dataSetProducer;

    @Override
    public void createDatasetSourceData(DatasetSourceDataCreateReqVO createReqVO) {

        //根据文件ID获取文件信息
        DatasetStorageUpLoadRespVO datasetStorageUpLoadRespVO = datasetStorageService.getDatasetStorageByUID(createReqVO.getFiledId());
        //数据校验
        validateDatasetSourceDataExists(createReqVO.getFiledId(), createReqVO.getDatasetId());

        //获取字符数
        int wordCount = getFileCharacterCountFromURL(datasetStorageUpLoadRespVO.getStorageKey());
        //封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetSourceDataDO::getDatasetId, createReqVO.getDatasetId());
        //获取当前文件位置
        Long position = datasetSourceDataMapper.selectCount(wrapper) + 1;

        //插入
        DatasetSourceDataDO datasetSourceDataDO = new DatasetSourceDataDO();
        datasetSourceDataDO.setUid(DatasetUID.getDatasetUID());
        datasetSourceDataDO.setName(datasetStorageUpLoadRespVO.getName());
        datasetSourceDataDO.setStorageId(createReqVO.getFiledId());
        datasetSourceDataDO.setPosition(position.intValue());
        datasetSourceDataDO.setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
        datasetSourceDataDO.setWordCount(Long.valueOf(wordCount));
        datasetSourceDataDO.setDatasetId(createReqVO.getDatasetId());
        datasetSourceDataMapper.insert(datasetSourceDataDO);
        dataSetProducer.sendCleanDatasetsSendMessage( createReqVO.getDatasetId(),createReqVO.getFiledId(),createReqVO.getSplitRule(),datasetStorageUpLoadRespVO.getStorageKey());

        executeSplitAndIndex(createReqVO.getSplitRule(),createReqVO.getFiledId(),createReqVO.getDatasetId(),datasetStorageUpLoadRespVO.getStorageKey());


    }

    /**
     * 更新数据集源数据
     *
     * @param updateReqVO
     *         更新信息
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
        //判断当前源数据状态
        if (archivedStatus(uid)) {
            //更新数据
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
        //判断当前源数据状态
        if (archivedStatus(uid)) {
            //更新数据
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
        //更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getUid, uid);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
        datasetSourceDataMapper.update(null, wrapper);
    }

    private Boolean archivedStatus(String uid) {
        DatasetSourceDataDO datasetSourceDataDO = datasetSourceDataMapper.selectOne(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid));
        return datasetSourceDataDO.getArchived();
    }


    /**
     * 异步分段和创建索引
     *
     * @param
     */
    @Async
    public void executeSplitAndIndex(SplitRule splitRule, String datasetId, String fileId, String url) {
        documentSegmentsService.splitAndIndex(splitRule, datasetId, fileId, url);
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