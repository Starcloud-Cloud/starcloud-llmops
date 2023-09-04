package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    public SourceDataUploadDTO uploadFilesSourceData(UploadFileReqVO reqVO) {

        SourceDataUploadDTO sourceDataUrlUploadDTO = new SourceDataUploadDTO();
        sourceDataUrlUploadDTO.setAppId(reqVO.getAppId());
        sourceDataUrlUploadDTO.setBatch(reqVO.getBatch());

        // 使用Tika检测文件类型
        MediaType mediaType;
        String mediaTypeExtension;
        String extName = getFileExtension(Objects.requireNonNull(reqVO.getFile().getOriginalFilename()));
        try (TikaInputStream tis = TikaInputStream.get(reqVO.getFile().getInputStream())) {
            mediaType = TikaConfig.getDefaultConfig().getDetector().detect(tis, new Metadata());
            mediaTypeExtension = MimeTypes.getDefaultMimeTypes().forName(mediaType.toString()).getExtension();
            mediaTypeExtension = mediaTypeExtension.replace(".", "");
        } catch (IOException | MimeTypeException e) {
            throw new RuntimeException("Could not read file", e);
        }
        if (!mediaTypeExtension.equals(extName)) {
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
        reqVO.setFileContent(fileContent);

        UploadResult result = processingService.fileProcessing(reqVO);


        if (!result.getStatus()) {
            sourceDataUrlUploadDTO.setStatus(false);
            sourceDataUrlUploadDTO.setSourceDataId(result.getErrMsg());
        } else {
            sourceDataUrlUploadDTO.setStatus(true);
            sourceDataUrlUploadDTO.setSourceDataId(result.getSourceDataId());
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
    public List<SourceDataUploadDTO> uploadUrlsSourceData(UploadUrlReqVO reqVO) {

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
            ListenableFuture<UploadResult> executed = this.executeAsyncWithUrl(singleReqVO);

            try {
                UploadResult result = executed.get();
                if (!result.getStatus()) {
                    sourceDataUploadDTO.setStatus(false);
                    sourceDataUploadDTO.setSourceDataId(result.getErrMsg());
                } else {
                    sourceDataUploadDTO.setStatus(true);
                    sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                }
            } catch (InterruptedException | ExecutionException e) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setSourceDataId("系统异常");
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
    public ListenableFuture<UploadResult> executeAsyncWithUrl(UploadUrlReqVO reqVO) {
        return AsyncResult.forValue(processingService.urlProcessing(reqVO));
    }

    /**
     * 上传自定义文本-支持批量上传
     *
     * @param reqVOS 自定义文本 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadCharactersSourceData(UploadCharacterReqVO reqVOS) {
        List<SourceDataUploadDTO> resultDTOs = new ArrayList<>();

        for (CharacterDTO reqVO : reqVOS.getCharacterVOS()) {
            SourceDataUploadDTO sourceDataUploadDTO = new SourceDataUploadDTO();
            sourceDataUploadDTO.setAppId(reqVOS.getAppId());
            sourceDataUploadDTO.setBatch(reqVOS.getBatch());

            UploadCharacterReqVO singleReqVO = new UploadCharacterReqVO();
            // 复制之前的值到新的请求对象中
            singleReqVO.setAppId(reqVOS.getAppId());
            singleReqVO.setSessionId(reqVOS.getSessionId());
            singleReqVO.setBatch(reqVOS.getBatch());
            singleReqVO.setDataModel(reqVOS.getDataModel());
            singleReqVO.setDataType(reqVOS.getDataType());

            singleReqVO.setCleanSync(reqVOS.getCleanSync());
            singleReqVO.setSplitSync(reqVOS.getSplitSync());
            singleReqVO.setIndexSync(reqVOS.getIndexSync());

            singleReqVO.setCharacterVOS(Collections.singletonList(reqVO));
            ListenableFuture<UploadResult> executed = this.executeAsyncWithCharacters(singleReqVO);

            try {
                UploadResult result = executed.get();
                if (!result.getStatus()) {
                    sourceDataUploadDTO.setStatus(false);
                    sourceDataUploadDTO.setSourceDataId(result.getErrMsg());
                } else {
                    sourceDataUploadDTO.setStatus(true);
                    sourceDataUploadDTO.setSourceDataId(result.getSourceDataId());
                }

            } catch (InterruptedException | ExecutionException e) {
                sourceDataUploadDTO.setStatus(false);
                sourceDataUploadDTO.setSourceDataId("系统异常");
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
    public ListenableFuture<UploadResult> executeAsyncWithCharacters(UploadCharacterReqVO reqVO) {
        return AsyncResult.forValue(processingService.stringProcessing(reqVO));
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
    public DatasetSourceDataDetailsInfoVO getSourceDataListData(String uid, Boolean enable) {

        DatasetSourceDataDO sourceDataDO = datasetSourceDataMapper.selectOne(
                Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                        .eq(DatasetSourceDataDO::getUid, uid));

        if (sourceDataDO == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }

        DatasetSourceDataDetailsInfoVO datasetSourceDataDetailsInfoVO = BeanUtil.copyProperties(sourceDataDO, DatasetSourceDataDetailsInfoVO.class);

        if (DataSetSourceDataStatusEnum.CLEANING_COMPLETED.getStatus() < sourceDataDO.getStatus()) {
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
            respVO.setRuleVO(handleRulesService.getRuleById(respVO.getRuleId()));
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
                            respVO.setRuleVO(handleRulesService.getRuleById(respVO.getRuleId()));
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
        // 根据应用 ID 获取数据集信息
        Long datasetId = validateAppDatasets(appId);
        return getSourceDataDetailByDatasetId(datasetId, dataModel, getContent);
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
        // 根据应用 ID与会话 ID 获取数据集信息
        Long datasetId = validateSessionDatasets(appId, sessionId);
        // 查询数据
        return getSourceDataDetailByDatasetId(datasetId, dataModel, getContent);
    }

    /**
     * 通过会话上传文件-支持批量上传
     *
     * @param reqVO 文件上传的 VO
     * @return 编号
     */
    @Override
    public SourceDataUploadDTO uploadFilesSourceDataBySession(UploadFileReqVO reqVO) {

        this.validateSessionDatasets(reqVO.getAppId(), reqVO.getSessionId());
        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.DOCUMENT.name());
        return this.uploadFilesSourceData(reqVO);
    }

    /**
     * 通过会话上传URL-支持批量上传
     *
     * @param reqVO URL 上传的 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadUrlsSourceDataBySession(UploadUrlReqVO reqVO) {
        this.validateSessionDatasets(reqVO.getAppId(), reqVO.getSessionId());
        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.HTML.name());
        return this.uploadUrlsSourceData(reqVO);
    }

    /**
     * 通过会话上传自定义文本-支持批量上传
     *
     * @param reqVOS 自定义文本 上传的 VO
     * @return 编号
     */
    @Override
    public List<SourceDataUploadDTO> uploadCharactersSourceDataBySession(UploadCharacterReqVO reqVOS) {
        this.validateSessionDatasets(reqVOS.getAppId(), reqVOS.getSessionId());
        reqVOS.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVOS.setDataType(DataSourceDataTypeEnum.CHARACTERS.name());
        return this.uploadCharactersSourceData(reqVOS);
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
    public void updateStatusById(Long id, Integer status, String message) {
        // 更新数据
        LambdaUpdateWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class);
        wrapper.eq(DatasetSourceDataDO::getId, id);
        wrapper.set(DatasetSourceDataDO::getStatus, status);
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
        if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
            return true;
        }
        return false;
    }


    /**
     * 数据集校验 判断数据集是否存在 ，不存在则创建数据集 存在则返回主键 ID
     *
     * @param appId 应用 ID
     * @return
     */
    private Long validateAppDatasets(String appId) {

        if (datasetsService.validateAppDatasetsExists(appId)) {
            return datasetsService.getDatasetInfoByAppId(appId).getId();
        } else {
            log.info("应用{}不存在数据集，开始创建数据集，应用 ID 为", appId);
            return datasetsService.createDatasetsByApp(appId);
        }
    }

    /**
     * 数据集校验 判断数据集是否存在 ，不存在则创建数据集 存在则返回主键 ID
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID
     * @return Long
     */
    private Long validateSessionDatasets(String appId, String sessionId) {

        try {
            TenantContextHolder.getRequiredTenantId();
        } catch (Exception e) {
            AppRespVO appRespVO = appApi.getSimple(appId);
            TenantContextHolder.setTenantId(appRespVO.getTenantId());
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(Long.valueOf(appRespVO.getCreator()));
        }

        if (datasetsService.validateSessionDatasetsExists(appId, sessionId)) {
            return datasetsService.getDatasetInfoBySession(appId, sessionId).getId();
        } else {
            log.info("应用{}的会话{}不存在数据集，开始创建数据集", appId, sessionId);
            return datasetsService.createDatasetsBySession(appId, sessionId);
        }
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

}