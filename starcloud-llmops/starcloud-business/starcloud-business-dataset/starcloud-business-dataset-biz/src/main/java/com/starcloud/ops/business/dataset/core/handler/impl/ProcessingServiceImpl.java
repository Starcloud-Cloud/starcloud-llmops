package com.starcloud.ops.business.dataset.core.handler.impl;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadFileRespDTO;
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
import org.springframework.scheduling.annotation.Async;
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
    public Boolean fileProcessing(MultipartFile file, byte[] fileContent, SplitRule splitRule, String datasetId, String batch, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传文件,分割规则为{}", datasetId, splitRule);
        validate(splitRule, datasetId);
        fileUploadStrategy.setFileData(file, fileContent);

        UploadFileRespDTO process = fileUploadStrategy.process(getUserId(datasetId));

        // 执行通用逻辑并且返回
        return commonProcess(process, datasetId, splitRule, batch, dataModel, dataType);
    }

    @Override
    public Boolean urlProcessing(String url, SplitRule splitRule, String datasetId, String batch, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传URL,分割规则为{}", datasetId, splitRule);
        validate(splitRule, datasetId);
        urlUploadStrategy.setUrl(url);
        UploadFileRespDTO process = urlUploadStrategy.process(getUserId(datasetId));
        // 执行通用逻辑并且返回
        return commonProcess(process, datasetId, splitRule, batch, dataModel, dataType);
    }

    @Override
    public Boolean urlProcessing(UploadUrlReqVO urlReqVO, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传URL,分割规则为{}", urlReqVO.getDatasetId(), urlReqVO.getSplitRule());
        validate(urlReqVO.getSplitRule(), urlReqVO.getDatasetId());
        urlUploadStrategy.setUrl(urlReqVO.getUrl());
        UploadFileRespDTO process = urlUploadStrategy.process(getUserId(urlReqVO.getDatasetId()));
        // 执行通用逻辑并且返回
        return commonProcess(process, urlReqVO, dataModel, dataType);
    }

    @Override
    public Boolean stringProcessing(String title, String context, SplitRule splitRule, String datasetId, String batch, Integer dataModel, String dataType) {
        log.info("====> 数据集{}开始上传字符串,分割规则为{}", datasetId, splitRule);
        validate(splitRule, datasetId);
        stringUploadStrategy.setData(title, context);
        UploadFileRespDTO process = stringUploadStrategy.process(getUserId(datasetId));
        // 执行通用逻辑并且返回
        return commonProcess(process, datasetId, splitRule, batch, dataModel, dataType);
    }


    @Deprecated
    private Boolean commonProcess(UploadFileRespDTO process, String datasetId, SplitRule splitRule, String batch, Integer dataModel, String dataType) {
        if (!process.getStatus()) {
            return false;
        }
        log.info("====> 数据上传成功,开始保存数据");
        // 保存上传记录
        Long storageId = saveStorageData(process);
        log.info("====> 上传记录保存成功,开始保存源数据 ");
        // 保存源数据
        Long sourceDataId = this.saveSourceData(process, storageId, datasetId, batch, dataModel, dataType);
        log.info("====> 源数据保存成功,开始异步发送队列信息 ");
        // 异步发送队列信息
        //sendMQMessage(datasetId, sourceDataId, splitRule, getLoginUserId(), false);

        dataSetProducer.sendCleanDatasetsSendMessage(datasetId, sourceDataId, splitRule, getLoginUserId());

        log.info("====> 返回数据上传信息");

        return true;
    }

    private Boolean commonProcess(UploadFileRespDTO process, UploadUrlReqVO urlReqVO, Integer dataModel, String dataType) {
        if (!process.getStatus()) {
            return false;
        }
        log.info("====> 数据上传成功,开始保存数据");
        // 保存上传记录
        Long storageId = saveStorageData(process);
        log.info("====> 上传记录保存成功,开始保存源数据 ");
        // 保存源数据
        Long sourceDataId = this.saveSourceData(process, storageId, urlReqVO.getDatasetId(), urlReqVO.getBatch(), dataModel, dataType);
        log.info("====> 源数据保存成功,开始异步发送队列信息 ");
        // 异步发送队列信息
        //sendMQMessage(datasetId, sourceDataId, splitRule, getLoginUserId(), false);

        DatasetSourceDataCleanSendMessage dataCleanSendMessage = new DatasetSourceDataCleanSendMessage();

        dataCleanSendMessage.setDataSourceId(sourceDataId);
        dataCleanSendMessage.setDatasetId(urlReqVO.getDatasetId());
        dataCleanSendMessage.setSplitRule(urlReqVO.getSplitRule());

        //@todo 应该是直接使用 当前数据集的creator
        dataCleanSendMessage.setUserId(getLoginUserId());

        if (urlReqVO.getSync()) {
            dataSetProducer.sendMessage(dataCleanSendMessage);
        } else {
            dataSetProducer.asyncSendMessage(dataCleanSendMessage);
        }


        log.info("====> 返回数据上传信息");

        return true;
    }

    /**
     * 参数校验 分割规则不可以为空，数据集必须存在
     *
     * @param splitRule
     * @param datasetId
     */
    @Override
    @TenantIgnore
    public void validate(SplitRule splitRule, String datasetId) {
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


    @Deprecated
    protected void sendMQMessage(String datasetId, Long dataSourceId, SplitRule splitRule, Long userID, Boolean sync) {
        dataSetProducer.sendCleanDatasetsSendMessage(datasetId, dataSourceId, splitRule, userID);
    }


    private Long saveStorageData(UploadFileRespDTO process) {
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

    private Long saveSourceData(UploadFileRespDTO process, Long storageId, String datasetId, String batch, Integer dataModel, String dataType) {
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
        return dataDO.getId();
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
