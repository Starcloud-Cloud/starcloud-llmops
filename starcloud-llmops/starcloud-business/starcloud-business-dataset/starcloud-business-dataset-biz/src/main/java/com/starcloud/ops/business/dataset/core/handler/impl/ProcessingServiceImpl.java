package com.starcloud.ops.business.dataset.core.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadContentDTO;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;
import com.starcloud.ops.business.dataset.core.handler.strategy.FileUploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.strategy.StringUploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.strategy.UrlUploadStrategy;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_SOURCE_UPLOAD_DATA_FAIL_APPID;

/**
 * 数据源数据上传逻辑 - 支持 HTML、文件、字符串、siteMap 上传
 *
 * @author Alan Cusack
 */
@Slf4j
@Service
public class ProcessingServiceImpl implements ProcessingService {

    private final FileUploadStrategy fileUploadStrategy;
    private final UrlUploadStrategy urlUploadStrategy;
    private final StringUploadStrategy stringUploadStrategy;

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetDataHandleRulesService datasetDataHandleRulesService;
    @Resource
    @Lazy
    private DatasetSourceDataCleanProducer dataSetProducer;

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;


    @Autowired
    public ProcessingServiceImpl(FileUploadStrategy fileUploadStrategy, UrlUploadStrategy urlUploadStrategy, StringUploadStrategy stringUploadStrategy) {
        this.fileUploadStrategy = fileUploadStrategy;
        this.urlUploadStrategy = urlUploadStrategy;
        this.stringUploadStrategy = stringUploadStrategy;
    }

    @Override
    public UploadResult fileProcessing(UploadFileReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        // 根据应用 ID 获取数据集信息
        DatasetsDO datasetInfo = validateDatasets(reqVO);

        log.info("====> 数据集{}开始上传文件", datasetInfo.getId());
        fileUploadStrategy.setFileData(reqVO.getFile(), reqVO.getFileContent());
        UploadContentDTO process = fileUploadStrategy.process(baseDBHandleDTO.getCreator());

        process.setBatch(reqVO.getBatch());
        process.setDatasetId(datasetInfo.getId());
        process.setDataModel(reqVO.getDataModel());
        process.setDataType(reqVO.getDataType());

        process.setCleanSync(reqVO.getCleanSync());
        process.setSplitSync(reqVO.getSplitSync());
        process.setIndexSync(reqVO.getIndexSync());
        // 执行通用逻辑并且返回
        return commonProcess(process, baseDBHandleDTO);
    }


    @Override
    public UploadResult urlProcessing(UploadUrlReqVO reqVO, UserBaseDTO baseDBHandleDTO) {

        // 根据应用 ID 获取数据集信息
        DatasetsDO datasetInfo = validateDatasets(reqVO);
        log.info("====> 数据集{}开始上传URL", datasetInfo.getId());

        urlUploadStrategy.setUrl(reqVO.getUrls().get(0), getLanguageRule(datasetInfo.getId(), reqVO.getUrls().get(0)));
        UploadContentDTO process = urlUploadStrategy.process(baseDBHandleDTO.getCreator());
        process.setInitAddress(reqVO.getUrls().get(0));

        process.setBatch(reqVO.getBatch());
        process.setDatasetId(datasetInfo.getId());
        process.setDataModel(reqVO.getDataModel());
        process.setDataType(reqVO.getDataType());

        process.setCleanSync(reqVO.getCleanSync());
        process.setSplitSync(reqVO.getSplitSync());
        process.setIndexSync(reqVO.getIndexSync());
        // 执行通用逻辑并且返回
        return commonProcess(process, baseDBHandleDTO);
    }

    @Override
    public UploadResult stringProcessing(UploadCharacterReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        // 根据应用 ID 获取数据集信息
        DatasetsDO datasetInfo = validateDatasets(reqVO);
        log.info("====> 数据集{}开始上传字符串", datasetInfo.getId());
        stringUploadStrategy.setData(reqVO.getCharacterVOS().get(0).getTitle(), reqVO.getCharacterVOS().get(0).getContext());
        UploadContentDTO process = stringUploadStrategy.process(baseDBHandleDTO.getCreator());

        process.setBatch(reqVO.getBatch());
        process.setDatasetId(datasetInfo.getId());
        process.setDataModel(reqVO.getDataModel());
        process.setDataType(reqVO.getDataType());

        process.setCleanSync(reqVO.getCleanSync());
        process.setSplitSync(reqVO.getSplitSync());
        process.setIndexSync(reqVO.getIndexSync());

        // 执行通用逻辑并且返回
        return commonProcess(process, baseDBHandleDTO);
    }


    private UploadResult commonProcess(UploadContentDTO process, UserBaseDTO baseDBHandleDTO) {
        UploadResult uploadResult = new UploadResult();

        uploadResult.setErrMsg(process.getErrMsg());
        uploadResult.setStatus(process.getStatus());

        if (!process.getStatus()) {
            // 如果上传或者解析出错 则保留数据记录
            saveErrorSourceData(process, baseDBHandleDTO);
            return uploadResult;
        }
        log.info("====> 数据上传操作执行完毕,开始保存数据");

        // 保存上传记录
        Long storageId = saveStorageData(process, baseDBHandleDTO);
        log.info("====> 上传记录保存成功,开始保存源数据 ");
        // 保存源数据
        DatasetSourceDataDO sourceDataDO = this.saveSourceData(process, storageId, baseDBHandleDTO);
        log.info("====> 源数据保存成功,开始异步发送队列信息 ");
        // 异步发送队列信息

        DatasetSourceDataCleanSendMessage dataCleanSendMessage = new DatasetSourceDataCleanSendMessage();

        dataCleanSendMessage.setDatasetId(process.getDatasetId());
        dataCleanSendMessage.setDataSourceId(sourceDataDO.getId());

        dataCleanSendMessage.setUserId(baseDBHandleDTO.getCreator());
        dataCleanSendMessage.setTenantId(baseDBHandleDTO.getTenantId());

        dataCleanSendMessage.setCleanSync(process.getCleanSync());
        dataCleanSendMessage.setSplitSync(process.getSplitSync());
        dataCleanSendMessage.setIndexSync(process.getIndexSync());

        if (process.getCleanSync()) {
            dataSetProducer.sendMessage(dataCleanSendMessage);
        } else {
            dataSetProducer.asyncSendMessage(dataCleanSendMessage);
        }
        log.info("====> 返回数据上传信息");

        uploadResult.setSourceDataId(sourceDataDO.getId());
        uploadResult.setSourceDataUid(sourceDataDO.getUid());

        return uploadResult;
    }


    public String getLanguageRule(Long datasetId, String url) {
        return datasetDataHandleRulesService.getHtmlLanguageRule(datasetId, url);
    }


    private Long saveStorageData(UploadContentDTO process, UserBaseDTO baseDBHandleDTO) {
        DatasetStorageDO datasetStorageDO = new DatasetStorageDO();
        datasetStorageDO.setUid(DatasetUID.createStorageUID());
        datasetStorageDO.setName(process.getName());
        datasetStorageDO.setStorageKey(process.getFilepath());
        datasetStorageDO.setType(process.getExtension());
        datasetStorageDO.setSize(process.getSize());
        datasetStorageDO.setMimeType(process.getMimeType().toUpperCase());
        datasetStorageDO.setUsed(false);

        datasetStorageDO.setCreator(String.valueOf(baseDBHandleDTO.getUpdater()));
        datasetStorageDO.setUpdater(String.valueOf(baseDBHandleDTO.getUpdater()));
        datasetStorageDO.setTenantId(baseDBHandleDTO.getTenantId());

        datasetStorageMapper.insert(datasetStorageDO);
        return datasetStorageDO.getId();
    }

    /**
     * 保存上传的数据信息
     *
     * @param process   数据执行信息
     * @param storageId 数据保存ID
     * @return DatasetSourceDataDO
     */
    private DatasetSourceDataDO saveSourceData(UploadContentDTO process, Long storageId, UserBaseDTO baseDBHandleDTO) {
        // 封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetSourceDataDO::getDatasetId, process.getDatasetId());
        wrapper.eq(DatasetSourceDataDO::getTenantId, baseDBHandleDTO.getTenantId());
        // 获取当前文件位置
        long position = datasetSourceDataMapper.selectCount(wrapper) + 1;

        DataSourceInfoDTO dataSourceInfoDTO = new DataSourceInfoDTO().setInitAddress(process.getInitAddress());

        DatasetSourceDataDO dataDO = new DatasetSourceDataDO();
        dataDO.setUid(DatasetUID.createSourceDataUID());
        dataDO.setName(process.getName());
        dataDO.setStorageId(storageId);
        dataDO.setPosition(position);
        dataDO.setBatch(process.getBatch());
        dataDO.setDescription(process.getDescription());
        dataDO.setDataModel(process.getDataModel());
        dataDO.setDataType(process.getDataType());
        dataDO.setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
        dataDO.setWordCount(process.getCharacterCount());
        dataDO.setDatasetId(process.getDatasetId());
        dataDO.setStatus(DataSetSourceDataStatusEnum.UPLOAD_COMPLETED.getStatus());
        dataDO.setDataSourceInfo(JSONObject.toJSONString(dataSourceInfoDTO));

        dataDO.setCreator(String.valueOf(baseDBHandleDTO.getUpdater()));
        dataDO.setUpdater(String.valueOf(baseDBHandleDTO.getUpdater()));
        dataDO.setTenantId(baseDBHandleDTO.getTenantId());
        datasetSourceDataMapper.insert(dataDO);
        return dataDO;
    }

    private void saveErrorSourceData(UploadContentDTO process, UserBaseDTO baseDBHandleDTO) {
        // 封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();

        wrapper.eq(DatasetSourceDataDO::getDatasetId, process.getDatasetId());
        // 获取当前文件位置
        long position = datasetSourceDataMapper.selectCount(wrapper) + 1;

        DataSourceInfoDTO dataSourceInfoDTO = new DataSourceInfoDTO().setInitAddress(process.getInitAddress());
        DatasetSourceDataDO sourceDataDO = new DatasetSourceDataDO();
        sourceDataDO.setUid(DatasetUID.createSourceDataUID())
                .setName(process.getName())
                .setStorageId(null)
                .setPosition(position)
                .setBatch(process.getBatch())
                .setDataModel(process.getDataModel())
                .setDescription(process.getDescription())
                .setDataType(process.getDataType())
                .setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name())
                .setWordCount(process.getCharacterCount())
                .setDatasetId(process.getDatasetId())
                .setStatus(DataSetSourceDataStatusEnum.ANALYSIS_ERROR.getStatus())
                .setDataSourceInfo(JSONObject.toJSONString(dataSourceInfoDTO))
                .setEndUser(baseDBHandleDTO.getEndUser())
                .setErrorCode(process.getErrCode())
                .setCreator(String.valueOf(baseDBHandleDTO.getCreator()))
                .setUpdater(String.valueOf(baseDBHandleDTO.getCreator()));
        if (DataSourceDataTypeEnum.HTML.name().equals(process.getDataType())) {
            sourceDataDO.setDataSourceInfo(JSONObject.toJSONString(dataSourceInfoDTO.setInitAddress(process.getName())));
        }
        datasetSourceDataMapper.insert(sourceDataDO);
    }


    /**
     * 数据集验证
     *
     * @param reqVO 上传的 VO
     * @return 数据集 DO
     */
    private DatasetsDO validateDatasets(UploadReqVO reqVO) {
        if (StrUtil.isBlank(reqVO.getAppId())) {
            throw exception(DATASET_SOURCE_UPLOAD_DATA_FAIL_APPID);
        }
        if (StrUtil.isBlank(reqVO.getSessionId())) {
            return datasetsService.getDatasetInfoByAppId(reqVO.getAppId());
        }
        return datasetsService.getDatasetInfoBySession(reqVO.getAppId(), reqVO.getSessionId());
    }


}
