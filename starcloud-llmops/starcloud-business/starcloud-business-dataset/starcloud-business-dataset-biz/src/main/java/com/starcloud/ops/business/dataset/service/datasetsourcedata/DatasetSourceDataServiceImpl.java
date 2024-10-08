package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppApi;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.core.handler.ProcessingService;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
    @Lazy
    private AppApi appApi;

    @Resource
    private DatasetDataHandleRulesService handleRulesService;

    @Resource
    private DatasetStorageService datasetStorageService;

    @Resource
    private DocumentSegmentsService documentSegmentsService;

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    /**
     * 根据主键 ID 获取数据
     *
     * @param id 数据集源数据ID
     */
    @Override
    public DatasetSourceDataDO selectDataById(Long id) {
        return datasetSourceDataMapper.selectById(id);
    }

    /**
     * 上传文件-支持批量上传
     *
     * @param reqVO 上传的 VO
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadFilesSourceData(UploadFileReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        if (!validateDataset(reqVO.getAppId(), reqVO.getSessionId())) {

            // 不存在则创建数据集
            createDataset(reqVO.getAppId(), reqVO.getSessionId(), baseDBHandleDTO);
        }
        // 根据应用或者会话设置用户信息
        baseDBHandleDTO = setBaseDbHandleInfo(reqVO.getAppId(), reqVO.getSessionId(), baseDBHandleDTO);

        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();
        sourceDataUrlUploadDTO.setAppId(reqVO.getAppId());
        sourceDataUrlUploadDTO.setBatch(reqVO.getBatch());

        String[] allowedTypes = {"pdf", "doc", "docx", "text", "txt", "md", "csv"};
        String extName = getFileExtension(Objects.requireNonNull(reqVO.getFile().getOriginalFilename()));

        boolean isValidFileType = false;
        try {
            // String mimeType = tika.detect(reqVO.getFile().getOriginalFilename());
            // 检查是否是允许的文件类型
            for (String allowedType : allowedTypes) {
                if (extName.equals(allowedType)) {
                    isValidFileType = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("获取文件类型失败");
        }

        if (!isValidFileType) {
            sourceDataUrlUploadDTO.setStatus(false);
            sourceDataUrlUploadDTO.setErrMsg("文件格式暂时无法适配，我们紧急处理中！");
            return sourceDataUrlUploadDTO;
        }


        // 读取文件内容到字节数组中
        byte[] fileContent;
        try {
            fileContent = IOUtils.toByteArray(reqVO.getFile().getInputStream());

        } catch (IOException e) {
            sourceDataUrlUploadDTO.setStatus(false);
            sourceDataUrlUploadDTO.setErrMsg("文件读取失败，请上传正确的文件");
            return sourceDataUrlUploadDTO;
        }

        String hash;
        try {
            hash = HashUtil.calculateHash(reqVO.getFile().getInputStream());
        } catch (IOException e) {
            sourceDataUrlUploadDTO.setStatus(false);
            sourceDataUrlUploadDTO.setErrMsg("文件读取失败，请上传正确的文件");
            return sourceDataUrlUploadDTO;
        }

        DatasetSourceDataDO sameHashData = getSameHashData(hash, reqVO.getAppId(), reqVO.getSessionId());
        if (sameHashData == null) {
            reqVO.setHash(hash);
            reqVO.setFileContent(fileContent);

            UploadResult result = processingService.fileProcessing(reqVO, baseDBHandleDTO);

            if (!result.getStatus()) {
                sourceDataUrlUploadDTO.setStatus(false);
                sourceDataUrlUploadDTO.setSourceDataUid(result.getSourceDataUid());
                sourceDataUrlUploadDTO.setSourceDataId(result.getSourceDataId());
            } else {
                sourceDataUrlUploadDTO.setStatus(true);
                sourceDataUrlUploadDTO.setSourceDataUid(result.getSourceDataUid());
                sourceDataUrlUploadDTO.setSourceDataId(result.getSourceDataId());
            }

        } else {
            sourceDataUrlUploadDTO.setStatus(true);
            sourceDataUrlUploadDTO.setSourceDataUid(sameHashData.getUid());
            sourceDataUrlUploadDTO.setSourceDataId(sameHashData.getId());
            return sourceDataUrlUploadDTO;
        }
        return sourceDataUrlUploadDTO;

    }


    /**
     * 上传文件-支持批量上传
     *
     * @param reqVO Url上传VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadUrlsSourceData(UploadUrlReqVO reqVO, UserBaseDTO baseDBHandleDTO) {

        // 验证数据集是否存在
        if (!validateDataset(reqVO.getAppId(), reqVO.getSessionId())) {
            // 不存在则创建数据集
            createDataset(reqVO.getAppId(), reqVO.getSessionId(), baseDBHandleDTO);
        }
        if (reqVO.getDataType() == null) {
            reqVO.setDataType(DataSourceDataTypeEnum.HTML.name());
        }

        // 根据应用或者会话设置用户信息
        baseDBHandleDTO = setBaseDbHandleInfo(reqVO.getAppId(), reqVO.getSessionId(), baseDBHandleDTO);


        List<SourceDataUploadDTO> resultDTOs = new ArrayList<>();
        for (String url : reqVO.getUrls()) {

            SourceDataUploadDTO sourceDataUploadDTO = new SourceDataUploadDTO();
            sourceDataUploadDTO.setAppId(reqVO.getAppId());
            sourceDataUploadDTO.setBatch(reqVO.getBatch());

            if (!isValidUrl(url)) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setErrMsg(String.format("你的链接%s失效，请输入正确的链接", url));
                break;
            }
            String hash;
            try {
                hash = HashUtil.calculateHash(new ByteArrayInputStream(url.getBytes()));
            } catch (IOException e) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setErrMsg(String.format("你的链接%s失效", url));
                break;
            }

            DatasetSourceDataDO sameHashData = getSameHashData(hash, reqVO.getAppId(), reqVO.getSessionId());
            if (sameHashData == null) {
                // 复制之前的值到新的请求对象中
                UploadUrlReqVO singleReqVO = new UploadUrlReqVO();
                singleReqVO.setAppId(reqVO.getAppId());
                singleReqVO.setSessionId(reqVO.getSessionId());
                singleReqVO.setBatch(reqVO.getBatch());

                singleReqVO.setDataModel(reqVO.getDataModel());
                singleReqVO.setDataType(reqVO.getDataType());
                singleReqVO.setUrls(Collections.singletonList(url));

                singleReqVO.setCleanSync(reqVO.getCleanSync());
                singleReqVO.setSplitSync(reqVO.getSplitSync());
                singleReqVO.setIndexSync(reqVO.getIndexSync());

                singleReqVO.setEnableSummary(reqVO.getEnableSummary());
                singleReqVO.setSummaryContentMaxNums(reqVO.getSummaryContentMaxNums());
                singleReqVO.setHash(hash);

                ListenableFuture<UploadResult> executed = this.executeAsyncWithUrl(singleReqVO, baseDBHandleDTO);

                try {
                    UploadResult result = executed.get();
                    if (!result.getStatus()) {
                        sourceDataUploadDTO.setStatus(false);
                        sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                        sourceDataUploadDTO.setSourceDataUid(result.getSourceDataUid());
                    } else {
                        sourceDataUploadDTO.setStatus(true);
                        sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                        sourceDataUploadDTO.setSourceDataUid(result.getSourceDataUid());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    sourceDataUploadDTO.setStatus(false);
                    sourceDataUploadDTO.setSourceDataUid("系统异常");
                }
            }else {
                sourceDataUploadDTO.setStatus(true);
                sourceDataUploadDTO.setSourceDataId(sameHashData.getId());
                sourceDataUploadDTO.setSourceDataUid(sameHashData.getUid());
            }

            resultDTOs.add(sourceDataUploadDTO);


        }

        return resultDTOs;
    }

    /**
     * 异步执行URL上传逻辑 等待返回结果
     *
     * @param reqVO 上传的 VO
     * @return 上传结果
     */
    @Async
    public ListenableFuture<UploadResult> executeAsyncWithUrl(UploadUrlReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        return AsyncResult.forValue(processingService.urlProcessing(reqVO, baseDBHandleDTO));
    }

    /**
     * 上传自定义文本-支持批量上传
     *
     * @param reqVOS 自定义文本 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadCharactersSourceData(UploadCharacterReqVO reqVOS, UserBaseDTO baseDBHandleDTO) {
        // 验证数据集是否存在
        if (!validateDataset(reqVOS.getAppId(), reqVOS.getSessionId())) {
            // 不存在则创建数据集
            createDataset(reqVOS.getAppId(), reqVOS.getSessionId(), baseDBHandleDTO);
        }

        // 根据应用或者会话设置用户信息
        baseDBHandleDTO = setBaseDbHandleInfo(reqVOS.getAppId(), reqVOS.getSessionId(), baseDBHandleDTO);

        List<SourceDataUploadDTO> resultDTOs = new ArrayList<>();
        // FIXME: 2023/9/13 顺序返回结果
        for (CharacterDTO reqVO : reqVOS.getCharacterVOS()) {
            SourceDataUploadDTO sourceDataUploadDTO = new SourceDataUploadDTO();
            sourceDataUploadDTO.setAppId(reqVOS.getAppId());
            sourceDataUploadDTO.setBatch(reqVOS.getBatch());

            String hash;
            try {
                hash = HashUtil.calculateHash(new ByteArrayInputStream((reqVO.getTitle()+reqVO.getContext()).getBytes()));
            } catch (IOException e) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setErrMsg("自定义文本异常");
                break;
            }

            DatasetSourceDataDO sameHashData = getSameHashData(hash, reqVOS.getAppId(), reqVOS.getSessionId());

            UploadCharacterReqVO singleReqVO = new UploadCharacterReqVO();

            if (sameHashData == null) {
                // 复制之前的值到新的请求对象中
                singleReqVO.setAppId(reqVOS.getAppId());
                singleReqVO.setSessionId(reqVOS.getSessionId());
                singleReqVO.setBatch(reqVOS.getBatch());
                singleReqVO.setDataModel(reqVOS.getDataModel());
                singleReqVO.setDataType(reqVOS.getDataType());

                singleReqVO.setCleanSync(reqVOS.getCleanSync());
                singleReqVO.setSplitSync(reqVOS.getSplitSync());
                singleReqVO.setIndexSync(reqVOS.getIndexSync());
                singleReqVO.setEnableSummary(reqVOS.getEnableSummary());
                singleReqVO.setSummaryContentMaxNums(reqVOS.getSummaryContentMaxNums());

                singleReqVO.setCharacterVOS(Collections.singletonList(reqVO));
                ListenableFuture<UploadResult> executed = this.executeAsyncWithCharacters(singleReqVO, baseDBHandleDTO);

                try {
                    UploadResult result = executed.get();
                    if (!result.getStatus()) {
                        sourceDataUploadDTO.setStatus(false);
                        sourceDataUploadDTO.setSourceDataUid(result.getSourceDataUid());
                        sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                    } else {
                        sourceDataUploadDTO.setStatus(true);
                        sourceDataUploadDTO.setSourceDataUid(result.getSourceDataUid());
                        sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                    }

                } catch (InterruptedException | ExecutionException e) {
                    sourceDataUploadDTO.setStatus(false);
                    sourceDataUploadDTO.setSourceDataUid("系统异常");
                }

            }else {
                sourceDataUploadDTO.setSourceDataUid(sameHashData.getUid());
                sourceDataUploadDTO.setSourceDataId(sameHashData.getId());
            }
            resultDTOs.add(sourceDataUploadDTO);
        }

        return resultDTOs;
    }


    /**
     * 异步上传自定义文本 等待返回结果
     *
     * @param reqVO 上传的 VO
     * @return 上传结果
     */
    @Async
    public ListenableFuture<UploadResult> executeAsyncWithCharacters(UploadCharacterReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        return AsyncResult.forValue(processingService.stringProcessing(reqVO, baseDBHandleDTO));
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
    @Transactional
    public void deleteDatasetSourceData(String uid) {

        // 校验存在
        validateDatasetSourceDataExists(uid);

        DatasetSourceDataDO dataDO = getSourceDataByUID(uid, null);

        LocalDateTime now = LocalDateTimeUtil.now();
        if (CollUtil.contains(Arrays.asList(
                DataSetSourceDataStatusEnum.CLEANING_ERROR.getStatus(),
                DataSetSourceDataStatusEnum.SPLIT_ERROR.getStatus(),
                DataSetSourceDataStatusEnum.INDEX_ERROR.getStatus(),
                DataSetSourceDataStatusEnum.COMPLETED.getStatus()), dataDO.getStatus()) || (dataDO.getCreateTime().isBefore(now) && dataDO.getCreateTime().isBefore(now.plusHours(1)))) {

            // 删除索引 和 删除分块数据
            documentSegmentsService.deleteSegment(String.valueOf(dataDO.getDatasetId()), String.valueOf(dataDO.getId()));

            // 删除
            datasetSourceDataMapper.delete(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid));

        } else {
            throw exception(DATASET_SOURCE_DELETE_FAIL);
        }


    }

    /**
     * 删除知识库下所有的数据
     *
     * @param datasetId 知识库  ID
     */
    @Override
    @Transactional
    public void deleteAllDataByDatasetId(Long datasetId) {
        List<DatasetSourceDataDO> datasetSourceDataDOS = datasetSourceDataMapper.selectList(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getDatasetId, datasetId));
        if (CollUtil.isNotEmpty(datasetSourceDataDOS)) {
            log.info("准备删除知识库下的数据，当前知识库下的数据包含{}条", datasetSourceDataDOS.size());
            // 根据知识库 获取数据 ID
            datasetSourceDataDOS.forEach(dataDO -> deleteDatasetSourceData(dataDO.getUid()));
        }
    }

    /**
     * 删除应用下所有的数据
     *
     * @param appId 应用 ID
     */
    @Override
    public void deleteAllDataByAppId(String appId) {
        Assert.notBlank(appId, "删除数据失败，应用 ID为空");
        List<DatasetsDO> datasetsDOS = datasetsService.getAllDatasetInfoByAppId(appId);

        log.info("准备删除应用下的数据，当前知识库下的数据包含{}条", datasetsDOS.size());
        // 根据知识库 获取数据 ID
        datasetsDOS.forEach(datasetsDO -> deleteAllDataByDatasetId(datasetsDO.getId()));
    }

    /**
     * 禁用源数据
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public void disable(String uid) {

        DatasetSourceDataDO dataDO = getSourceDataByUID(uid, null);
        if (dataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
        if (dataDO.getEnabled()) {
            documentSegmentsService.updateEnable(dataDO.getId(), true);
            dataDO.setEnabled(false);
            datasetSourceDataMapper.updateById(dataDO);
        } else {
            throw exception(DATASET_SOURCE_DATA_ENABLE_STATUS_FAIL);
        }
    }

    /**
     * 启用源数据
     *
     * @param uid 数据集源数据编号
     */
    @Override
    public void enable(String uid) {
        DatasetSourceDataDO dataDO = getSourceDataByUID(uid, null);
        if (dataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
        if (!dataDO.getEnabled()) {
            documentSegmentsService.updateEnable(dataDO.getId(), false);
            dataDO.setEnabled(true);
            datasetSourceDataMapper.updateById(dataDO);
        } else {
            throw exception(DATASET_SOURCE_DATA_ENABLE_STATUS_FAIL);
        }
    }

    private void validateDatasetSourceDataExists(String uid) {
        if (datasetSourceDataMapper.selectOne(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid)) == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            // "文件名无扩展名，无法确定文件类型"
            throw exception(1);
        }
        // 不包含点，所以使用dotIndex + 1
        return fileName.substring(dotIndex + 1);
    }


    private void validateDatasetSourceDataExists(String filedId, String datasetID) {
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                .eq(DatasetSourceDataDO::getDatasetId, datasetID).eq(DatasetSourceDataDO::getStorageId, filedId);
        if (datasetSourceDataMapper.selectCount(wrapper) > 0) {
            throw exception(DATASET_SOURCE_DATA_EXISTS);
        }
    }


    @Override
    public PageResult<DatasetSourceDataDO> getDatasetSourceDataPage(DatasetSourceDataPageReqVO pageReqVO) {
        return datasetSourceDataMapper.selectPage(pageReqVO);
    }


    @Override
    public List<ListDatasetSourceDataRespVO> getDatasetSourceDataList(Long datasetId, Integer dataModel) {

        List<DatasetSourceDataDO> datasetSourceDataDOS = datasetSourceDataMapper.selectByDatasetId(datasetId, dataModel);

        return datasetSourceDataDOS.stream().map(dataDO -> {
                    ListDatasetSourceDataRespVO dataRespVO = new ListDatasetSourceDataRespVO();
                    dataRespVO.setUid(dataDO.getUid());
                    dataRespVO.setName(dataDO.getName());
                    dataRespVO.setDataModel(dataDO.getDataModel());
                    dataRespVO.setDataType(dataDO.getDataType());
                    dataRespVO.setBatch(dataDO.getBatch());
                    dataRespVO.setStatus(String.valueOf(dataDO.getStatus()));
                    dataRespVO.setWordCount(dataDO.getWordCount());
                    dataRespVO.setDescription(dataDO.getDescription());
                    dataRespVO.setSummary(dataDO.getSummary());
                    dataRespVO.setUpdateTime(dataDO.getUpdateTime());
                    dataRespVO.setType(datasetStorageService.selectDataById(dataDO.getStorageId()).getType());
                    return dataRespVO;
                }
        ).collect(Collectors.toList());

    }


    /**
     * 获取数据源详情
     *
     * @param uid 数据集源数据编号
     */
    @TenantIgnore
    @Override
    public DatasetSourceDataDetailsInfoVO getSourceDataByUid(String uid, Boolean enable) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, uid));

        if (sourceDataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
        return getSourceData(sourceDataDO, enable);
    }

    /**
     * 获取数据源详情
     *
     * @param id 数据集源数据编号
     */
    @TenantIgnore
    @Override
    public DatasetSourceDataDetailsInfoVO getSourceDataById(Long id, Boolean enable) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectById(id);
        if (sourceDataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
        return getSourceData(sourceDataDO, enable);
    }

    /**
     * 根据 DO 获取指定数据
     *
     * @param sourceDataDO
     * @param enable
     * @return
     */
    private DatasetSourceDataDetailsInfoVO getSourceData(DatasetSourceDataDO sourceDataDO, Boolean enable) {

        DatasetSourceDataDetailsInfoVO datasetSourceDataDetailsInfoVO = BeanUtil.copyProperties(sourceDataDO, DatasetSourceDataDetailsInfoVO.class);

        if (DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus() <= sourceDataDO.getStatus()) {
            // 设置清洗后内容
            DatasetStorageDO cleanDatasetDO = datasetStorageService.selectDataById(sourceDataDO.getCleanStorageId());

            String content = null;
            if (DataSourceDataTypeEnum.HTML.name().equals(sourceDataDO.getDataType())) {
                DataSourceInfoDTO dataSourceInfoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceInfoDTO.class);
                content = dataSourceInfoDTO.getInitAddress();

            } else {
                // 获取原始内容
                DatasetStorageDO contentDo = datasetStorageService.selectDataById(sourceDataDO.getCleanStorageId());
                content = contentDo.getStorageKey();
            }

            // true 返回
            if (enable) {
                byte[] bytes = HttpUtil.downloadBytes(cleanDatasetDO.getStorageKey());
                String result = new String(bytes);
                datasetSourceDataDetailsInfoVO.setCleanContent(result);

            } else {
                datasetSourceDataDetailsInfoVO.setContent(content);
                datasetSourceDataDetailsInfoVO.setCleanContent(cleanDatasetDO.getStorageKey());
            }
        }
        return datasetSourceDataDetailsInfoVO;
    }

    /**
     * 获取数据源详情
     *
     * @param sourceDataIds 数据集源数据ID
     */
    @TenantIgnore
    @Override
    public List<DatasetSourceDataBasicInfoVO> getSourceDataListData(List<Long> sourceDataIds) {

        List<DatasetSourceDataBasicInfoVO> basicInfoVOS = new ArrayList<>();
        // 查询ID集合非空判断判
        if (CollUtil.isEmpty(sourceDataIds)) {
            return basicInfoVOS;
        }

        // 根据 ID 集合查询数据
        List<DatasetSourceDataDO> datasetSourceDataDOS = datasetSourceDataMapper.selectList(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .in(DatasetSourceDataDO::getId, sourceDataIds));

        // 集合非空判断 集合为空直接返回
        if (CollUtil.isEmpty(datasetSourceDataDOS)) {
            return basicInfoVOS;
        }
        // 基础数据转换
        List<DatasetSourceDataBasicInfoVO> datasetSourceDataBasicInfoVOS = DatasetSourceDataConvert.INSTANCE.convertBasicInfoList(datasetSourceDataDOS);

        datasetSourceDataBasicInfoVOS.forEach(data -> {
            if (!DataSourceDataTypeEnum.HTML.name().equals(data.getDataType())) {
                // 获取原始内容
                DatasetStorageDO contentDo = datasetStorageService.selectDataById(data.getCleanId());
                data.setAddress(contentDo.getStorageKey());
            }
        });

        return datasetSourceDataBasicInfoVOS;
    }

    /**
     * 根据数据 UID 获取数据详情
     *
     * @param UID             源数据 UID
     * @param getCleanContent 是否详细内容数据
     * @return 数据集源数据列表
     */
    @Override
    public DatasetSourceDataDetailRespVO getSourceDataDetailByUID(String UID, Boolean getCleanContent) {

        DatasetSourceDataDO sourceDataDO = getSourceDataByUID(UID, null);
        // 数据非空判断
        if (sourceDataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
        DatasetSourceDataDetailRespVO respVO = DatasetSourceDataConvert.INSTANCE.convertDetailRespVO(sourceDataDO);
        // 设置存储信息
        respVO.setStorageVO(datasetStorageService.selectBaseDataById(respVO.getStorageId()));
        // 设置数据规则信息
        if (Objects.nonNull(respVO.getRuleId())) {
            // 防止用户删除用过的规则
            try {
                respVO.setRuleVO(handleRulesService.getRuleById(respVO.getRuleId()));
            } catch (Exception e) {
                respVO.setRuleVO(null);
            }

        }
        if (getCleanContent) {
            if (DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus() < sourceDataDO.getStatus()) {
                byte[] bytes = HttpUtil.downloadBytes(respVO.getStorageVO().getStorageKey());
                String result = new String(bytes);
                respVO.setCleanContent(result);

            } else {
                respVO.setCleanContent("数据未清洗完成，请稍后再试");
            }
        }
        return respVO;
    }

    private List<DatasetSourceDataDetailRespVO> getSourceDataDetailByDatasetId(Long datasetId, Integer dataModel, Boolean getContent) {

        // 查询数据
        List<DatasetSourceDataDO> datasetSourceDataDOS = datasetSourceDataMapper.selectByDatasetId(datasetId, dataModel);
        if (CollUtil.isNotEmpty(datasetSourceDataDOS)) {

            List<DatasetSourceDataDetailRespVO> datasetSourceDataDetailRespVOS = DatasetSourceDataConvert.INSTANCE.convertDetailRespVOS(datasetSourceDataDOS);

            datasetSourceDataDetailRespVOS.forEach(respVO -> {
                        respVO.setStorageVO(datasetStorageService.selectBaseDataById(respVO.getStorageId()));
                        if (Objects.nonNull(respVO.getRuleId())) {
                            // 防止用户删除用过的规则
                            try {
                                respVO.setRuleVO(handleRulesService.getRuleById(respVO.getRuleId()));
                            } catch (Exception e) {
                                respVO.setRuleVO(null);
                            }
                        }
                        respVO.setContent(null);
                        respVO.setCleanContent(null);
                    }
            );
            return datasetSourceDataDetailRespVOS;
        }
        return new ArrayList<DatasetSourceDataDetailRespVO>();
    }

    /**
     * 获得应用下 数据集源数据列表
     *
     * @param appId     应用 ID
     * @param dataModel 数据模型
     * @return 数据集源数据列表
     */
    @Override
    public List<DatasetSourceDataDetailRespVO> getApplicationSourceDataList(String appId, Integer dataModel, Boolean getContent) {
        if (validateDataset(appId, null)) {
            DatasetsDO dataset = getDataset(appId, null);
            return getSourceDataDetailByDatasetId(dataset.getId(), dataModel, getContent);
        }
        return new ArrayList<>();
    }

    /**
     * 获得会话下 数据集源数据列表
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID
     * @param dataModel 数据模型
     * @return 数据集源数据列表
     */
    @Override
    public List<DatasetSourceDataDetailRespVO> getSessionSourceDataList(String appId, String sessionId, Integer dataModel, Boolean getContent) {
        if (dataModel == null || dataModel < 0) {
            dataModel = DataSourceDataModelEnum.DOCUMENT.getStatus();
        }
        if (validateDataset(appId, sessionId)) {
            Long datasetId = getDataset(appId, sessionId).getId();
            // 查询数据
            return getSourceDataDetailByDatasetId(datasetId, dataModel, getContent);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 通过会话上传文件-支持批量上传
     *
     * @param reqVO 文件上传的 VO
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadFilesSourceDataBySession(UploadFileReqVO reqVO, UserBaseDTO baseDBHandleDTO) {

        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.DOCUMENT.name());

        if (StrUtil.isBlank(reqVO.getBatch())) {
            reqVO.setBatch(IdUtil.fastUUID());
        }
        return this.uploadFilesSourceData(reqVO, baseDBHandleDTO);
    }

    /**
     * 通过会话上传URL-支持批量上传
     *
     * @param reqVO URL 上传的 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadUrlsSourceDataBySession(UploadUrlReqVO reqVO, UserBaseDTO baseDBHandleDTO) {
        long startTime = System.currentTimeMillis();
        log.info("开始上传文件，开始时间为{}", startTime);
        if (baseDBHandleDTO == null) {
            throw exception(DATASETS_CONVERSATION_USER_SESSION_NOT_EXISTS);
        }

        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.HTML.name());
        if (StrUtil.isBlank(reqVO.getBatch())) {
            reqVO.setBatch(IdUtil.fastUUID());
        }

        List<SourceDataUploadDTO> sourceDataUploadDTOS = this.uploadUrlsSourceData(reqVO, baseDBHandleDTO);
        long endTime = System.currentTimeMillis();
        log.info("上传结束，结束时间为{}，总耗时{}", startTime, (endTime - startTime) / 1000);
        return sourceDataUploadDTOS;
    }

    /**
     * 通过会话上传自定义文本-支持批量上传
     *
     * @param reqVOS 自定义文本 上传的 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadCharactersSourceDataBySession(UploadCharacterReqVO reqVOS, UserBaseDTO baseDBHandleDTO) {

        reqVOS.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVOS.setDataType(DataSourceDataTypeEnum.CHARACTERS.name());
        if (StrUtil.isBlank(reqVOS.getBatch())) {
            reqVOS.setBatch(IdUtil.fastUUID());
        }
        return this.uploadCharactersSourceData(reqVOS, baseDBHandleDTO);
    }


    /**
     * 获取分块内容
     *
     * @param reqVO 分块请求 VO
     * @return 分页内容
     */
    @Override
    public PageResult<DatasetSourceDataSplitPageRespVO> getSplitDetails(DatasetSourceDataSplitPageReqVO reqVO) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, reqVO.getUid()));

        if (sourceDataDO != null) {
            SegmentPageQuery segmentPageQuery = BeanUtil.copyProperties(reqVO, SegmentPageQuery.class);

            segmentPageQuery.setDocumentUid(String.valueOf(sourceDataDO.getId()));
            segmentPageQuery.setDatasetUid(String.valueOf(sourceDataDO.getDatasetId()));

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
     * @param status 数据状态
     */
    @Override
    public void updateStatusById(Long id, Integer status, Integer errorCode, String message) {
        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getId, id);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
        wrapper.set(errorCode != null && errorCode > 0, DatasetSourceDataDO::getErrorCode, errorCode);
        wrapper.set(StrUtil.isNotBlank(message), DatasetSourceDataDO::getErrorMessage, message);
        datasetSourceDataMapper.update(null, wrapper);
    }


    /**
     * 更新数据集状态
     *
     * @param dataDO 源数据DO
     */
    @Override
    public void updateDatasourceById(DatasetSourceDataDO dataDO) {
        // 更新数据
        datasetSourceDataMapper.updateById(dataDO);

    }

    /**
     * 存档
     *
     * @param uid
     * @return
     */
    private Boolean archivedStatus(String uid) {
        DatasetSourceDataDO datasetSourceDataDO = datasetSourceDataMapper.selectOne(Wrappers.lambdaQuery(DatasetSourceDataDO.class).eq(DatasetSourceDataDO::getUid, uid));
        return datasetSourceDataDO.getArchived();
    }


    /**
     * Url 有效验证
     *
     * @param urlString Url 字符串
     * @return boolean
     */
    private static boolean isValidUrl(String urlString) {
        URI uri = null;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        if (uri.getHost() == null) {
            return false;
        }
        return uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https");
    }


    /**
     * 根据数据的 UID 获取数据
     *
     * @param UID    数据 UID
     * @param enable 数据启用状态 不查询则为 null
     * @return
     */
    private DatasetSourceDataDO getSourceDataByUID(String UID, Boolean enable) {
        return datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, UID)
                        .eq(enable != null, DatasetSourceDataDO::getEnabled, enable));
    }


    /**
     * 校验应用或者会话下是否存在数据集
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID 可以为空 为空根据应用查询
     * @return Boolean
     */
    private Boolean validateDataset(String appId, String sessionId) {
        // 判断是否存在会话
        if (StrUtil.isBlank(sessionId)) {
            return datasetsService.validateAppDatasetsExists(appId);
        } else {
            try {
                TenantContextHolder.getRequiredTenantId();

                // WebFrameworkUtils.getLoginUserId()
                // UserContextHolder.getUserId()
            } catch (Exception e) {
                AppRespVO appRespVO = appApi.getSimple(appId);
                TenantContextHolder.setTenantId(appRespVO.getTenantId());
                TenantContextHolder.setIgnore(false);
                UserContextHolder.setUserId(Long.valueOf(appRespVO.getCreator()));
            }
            return datasetsService.validateSessionDatasetsExists(appId, sessionId);
        }
    }

    /**
     * 根据应用或者会话创建数据集
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID 可以为空 为空根据应用查询
     * @return Long 数据集主键 ID
     */
    private DatasetsDO createDataset(String appId, String sessionId, UserBaseDTO baseDBHandleDTO) {
        // 判断是否存在会话
        if (StrUtil.isBlank(sessionId)) {
            return datasetsService.createDatasetsByApp(appId);
        } else {
            AppRespVO appRespVO = appApi.getSimple(appId);
            if (baseDBHandleDTO == null) {
                throw exception(DATASETS_CONVERSATION_USER_SESSION_NOT_EXISTS);
            }
            if (baseDBHandleDTO.getCreator() != null) {
                baseDBHandleDTO.setCreator(baseDBHandleDTO.getCreator());
            } else {
                baseDBHandleDTO.setCreator(Long.valueOf(appRespVO.getCreator()));
            }
            baseDBHandleDTO.setUpdater(Long.valueOf(appRespVO.getCreator()));
            baseDBHandleDTO.setTenantId(appRespVO.getTenantId());
            return datasetsService.createDatasetsBySession(appId, sessionId, baseDBHandleDTO);
        }
    }

    /**
     * 获取应用或者会话下的数据集
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID 可以为空 为空根据应用查询
     * @return Long 数据集主键 ID
     */
    private DatasetsDO getDataset(String appId, String sessionId) {
        // 判断是否存在会话
        if (StrUtil.isBlank(sessionId)) {
            return datasetsService.getDatasetInfoByAppId(appId);
        } else {
            try {
                TenantContextHolder.getRequiredTenantId();
            } catch (Exception e) {
                AppRespVO appRespVO = appApi.getSimple(appId);
                TenantContextHolder.setTenantId(appRespVO.getTenantId());
                TenantContextHolder.setIgnore(false);

                UserContextHolder.setUserId(Long.valueOf(appRespVO.getCreator()));
            }
            return datasetsService.getDatasetInfoBySession(appId, sessionId);
        }
    }

    private UserBaseDTO setBaseDbHandleInfo(String appId, String sessionId, UserBaseDTO baseDBHandleDTO) {
        DatasetsDO datasetsDO;
        if (StrUtil.isBlank(sessionId)) {
            datasetsDO = datasetsService.getDatasetInfoByAppId(appId);
        } else {
            datasetsDO = datasetsService.getDatasetInfoBySession(appId, sessionId);
        }
        if (baseDBHandleDTO == null) {
            baseDBHandleDTO = new UserBaseDTO();
        }
        if (baseDBHandleDTO.getCreator() == null) {
            baseDBHandleDTO.setCreator(Long.valueOf(datasetsDO.getCreator()));
        }
        if (baseDBHandleDTO.getUpdater() == null || baseDBHandleDTO.getTenantId() == null) {
            baseDBHandleDTO.setUpdater(Long.valueOf(datasetsDO.getUpdater()));
            baseDBHandleDTO.setTenantId(datasetsDO.getTenantId());
        }

        return baseDBHandleDTO;
    }


    /**
     * 获取同知识库下 hash 相同的数据
     *
     * @param dataHash
     * @param appId
     * @param sessionId
     * @return
     */
    private DatasetSourceDataDO getSameHashData(String dataHash, String appId, String sessionId) {
        DatasetsDO datasetsDO;
        if (StrUtil.isBlank(sessionId)) {
            datasetsDO = datasetsService.getDatasetInfoByAppId(appId);
        } else {
            datasetsDO = datasetsService.getDatasetInfoBySession(appId, sessionId);
        }
        LocalDateTime now = LocalDateTimeUtil.now();
        return datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getDatasetId, datasetsDO.getId())
                        .eq(DatasetSourceDataDO::getHash, dataHash)
                        .between(DatasetSourceDataDO::getCreateTime, LocalDateTimeUtil.beginOfDay(now), LocalDateTimeUtil.endOfDay(now)));
    }

}