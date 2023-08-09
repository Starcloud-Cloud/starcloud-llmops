package com.starcloud.ops.business.dataset.core.handler.impl;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResultDTO;
import com.starcloud.ops.business.dataset.core.handler.strategy.FileUploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.strategy.StringUploadStrategy;
import com.starcloud.ops.business.dataset.core.handler.strategy.UrlUploadStrategy;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasets.DatasetsMapper;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASETS_NOT_EXISTS;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.SOURCE_DATA_UPLOAD_SPLIT_RULE_EMPTY;

/**
 * 数据源数据上传逻辑 - 支持 URL、文件、字符串、siteMap 上传
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
    @Lazy
    private DatasetSourceDataCleanProducer dataSetProducer;

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Resource
    private DatasetsMapper datasetsMapper;


    @Autowired
    public ProcessingServiceImpl(FileUploadStrategy fileUploadStrategy, UrlUploadStrategy urlUploadStrategy, StringUploadStrategy stringUploadStrategy) {
        this.fileUploadStrategy = fileUploadStrategy;
        this.urlUploadStrategy = urlUploadStrategy;
        this.stringUploadStrategy = stringUploadStrategy;
    }

    @Override
    public String fileProcessing(MultipartFile file, byte[] fileContent, UploadFileReqVO reqVO, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传文件,分割规则为{}", reqVO.getDatasetId(), reqVO.getSplitRule());
        validate(reqVO.getDatasetId(), reqVO.getSplitRule());
        fileUploadStrategy.setFileData(file, fileContent);

        UploadResultDTO process = fileUploadStrategy.process(getUserId(reqVO.getDatasetId()));
        process.setSync(reqVO.getSync());
        process.setBatch(reqVO.getBatch());
        process.setSplitRule(reqVO.getSplitRule());
        process.setDatasetId(reqVO.getDatasetId());
        process.setDataModel(dataModel);
        process.setDataType(dataType);
        // 执行通用逻辑并且返回
        return commonProcess(process);
    }

    @Override
    @Deprecated
    public String urlProcessing(String url, SplitRule splitRule, String datasetId, String batch, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传URL,分割规则为{}", datasetId, splitRule);
        validate(datasetId, splitRule);
        urlUploadStrategy.setUrl(url);
        UploadResultDTO process = urlUploadStrategy.process(getUserId(datasetId));
        process.setDataModel(dataModel);
        process.setDataType(dataType);
        // 执行通用逻辑并且返回
        return commonProcess(process);
    }

    @Override
    public String urlProcessing(String url, UploadUrlReqVO reqVO, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传URL,分割规则为{}", reqVO.getDatasetId(), reqVO.getSplitRule());
        validate(reqVO.getDatasetId(), reqVO.getSplitRule());
        urlUploadStrategy.setUrl(url);
        UploadResultDTO process = urlUploadStrategy.process(getUserId(reqVO.getDatasetId()));
        process.setSync(reqVO.getSync());
        process.setBatch(reqVO.getBatch());
        process.setSplitRule(reqVO.getSplitRule());
        process.setDatasetId(reqVO.getDatasetId());
        process.setDataModel(dataModel);
        process.setDataType(dataType);

        // 执行通用逻辑并且返回
        return commonProcess(process);
    }

    @Override
    public String stringProcessing(UploadCharacterReqVO reqVO, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传字符串,分割规则为{}", reqVO.getDatasetId(), reqVO.getSplitRule());
        validate(reqVO.getDatasetId(), reqVO.getSplitRule());
        stringUploadStrategy.setData(reqVO.getTitle(), reqVO.getContext());
        UploadResultDTO process = stringUploadStrategy.process(getUserId(reqVO.getDatasetId()));
        process.setSync(reqVO.getSync());
        process.setBatch(reqVO.getBatch());
        process.setSplitRule(reqVO.getSplitRule());
        process.setDatasetId(reqVO.getDatasetId());
        process.setDataModel(dataModel);
        process.setDataType(dataType);
        // 执行通用逻辑并且返回
        return commonProcess(process);
    }


    // @Deprecated
    // private Boolean commonProcess(UploadResultDTO process, String datasetId, SplitRule splitRule, String batch, Integer dataModel, String dataType) {
    //     if (!process.getStatus()) {
    //         return false;
    //     }
    //     log.info("====> 数据上传成功,开始保存数据");
    //     // 保存上传记录
    //     Long storageId = saveStorageData(process);
    //     log.info("====> 上传记录保存成功,开始保存源数据 ");
    //     // 保存源数据
    //     Long sourceDataId = this.saveSourceData(process, storageId, datasetId, batch, dataModel, dataType);
    //     log.info("====> 源数据保存成功,开始异步发送队列信息 ");
    //     // 异步发送队列信息
    //     dataSetProducer.sendCleanDatasetsSendMessage(datasetId, sourceDataId, splitRule, getLoginUserId(),true);
    //
    //     log.info("====> 返回数据上传信息");
    //
    //     return true;
    // }

    private String commonProcess(UploadResultDTO process) {
        if (!process.getStatus()) {
            return null;
        }
        log.info("====> 数据上传成功,开始保存数据");
        // 保存上传记录
        Long storageId = saveStorageData(process);
        log.info("====> 上传记录保存成功,开始保存源数据 ");
        // 保存源数据
        DatasetSourceDataDO sourceDataDO = this.saveSourceData(process, storageId, process.getDatasetId(), process.getBatch(), process.getDataModel(), process.getDataType());
        log.info("====> 源数据保存成功,开始异步发送队列信息 ");
        // 异步发送队列信息
        // sendMQMessage(datasetId, sourceDataId, splitRule, getLoginUserId(), false);

        DatasetSourceDataCleanSendMessage dataCleanSendMessage = new DatasetSourceDataCleanSendMessage();

        dataCleanSendMessage.setDatasetId(process.getDatasetId());
        dataCleanSendMessage.setDataSourceId(sourceDataDO.getId());
        dataCleanSendMessage.setSplitRule(process.getSplitRule());
        dataCleanSendMessage.setUserId(getUserId(process.getDatasetId()));

        if (process.getSync()) {
            dataSetProducer.sendMessage(dataCleanSendMessage);
        } else {
            dataSetProducer.asyncSendMessage(dataCleanSendMessage);
        }


        log.info("====> 返回数据上传信息");

        return sourceDataDO.getUid();
    }

    /**
     * 参数校验 分割规则不可以为空，数据集必须存在
     *
     * @param splitRule
     * @param datasetId
     */
    @Override
    @TenantIgnore
    public void validate(String datasetId, SplitRule splitRule) {
        log.info("====> 验证数据集和分割规则");
        if (splitRule == null) {
            throw exception(SOURCE_DATA_UPLOAD_SPLIT_RULE_EMPTY);
        }
        log.info("====> 分割规则验证通过");
        if (datasetsMapper.selectOne(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, datasetId)) == null) {
            throw exception(DATASETS_NOT_EXISTS);
        }
        log.info("====> 数据集则验证通过");
        log.info("====> 验证通过，执行上传逻辑");
    }


    private Long saveStorageData(UploadResultDTO process) {
        DatasetStorageCreateReqVO createReqVO = new DatasetStorageCreateReqVO();
        createReqVO.setUid(DatasetUID.createStorageUID());
        createReqVO.setName(process.getName());
        createReqVO.setStorageKey(process.getFilepath());
        createReqVO.setType(process.getExtension());
        createReqVO.setSize(process.getSize());
        createReqVO.setMimeType(process.getMimeType().toUpperCase());
        createReqVO.setUsed(false);

        DatasetStorageDO datasetStorageDO = DatasetStorageConvert.INSTANCE.convert(createReqVO);
        datasetStorageMapper.insert(datasetStorageDO);
        return datasetStorageDO.getId();
    }

    private DatasetSourceDataDO saveSourceData(UploadResultDTO process, Long storageId, String datasetId, String batch, Integer dataModel, String dataType) {
        // 封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();

        wrapper.eq(DatasetSourceDataDO::getDatasetId, datasetId);
        // 获取当前文件位置
        long position = datasetSourceDataMapper.selectCount(wrapper) + 1;

        DatasetSourceDataDO dataDO = new DatasetSourceDataDO();
        dataDO.setUid(DatasetUID.createSourceDataUID());
        dataDO.setName(process.getName());
        dataDO.setStorageId(storageId);
        dataDO.setPosition(position);
        dataDO.setBatch(batch);
        dataDO.setDataModel(dataModel);
        dataDO.setDataType(dataType);
        dataDO.setCreatedFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
        dataDO.setWordCount(process.getCharacterCount());
        dataDO.setDatasetId(datasetId);
        dataDO.setStatus(DataSetSourceDataStatusEnum.UPLOAD_COMPLETED.getStatus());
        datasetSourceDataMapper.insert(dataDO);
        return dataDO;
    }

    private Long getUserId(String datasetId) {
        Long loginUserId = getLoginUserId();
        if (loginUserId == null) {
            String creator = datasetsMapper.selectOne(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, datasetId)).getCreator();
            loginUserId = Long.valueOf(creator);
        }
        return loginUserId;
    }


}
