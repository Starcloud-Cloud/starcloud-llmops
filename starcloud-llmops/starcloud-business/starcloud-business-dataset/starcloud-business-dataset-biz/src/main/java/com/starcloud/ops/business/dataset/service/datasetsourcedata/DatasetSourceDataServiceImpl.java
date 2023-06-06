package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.enums.SourceDataCreateEnum;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_SOURCE_DATA_NOT_EXISTS;


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

    @Override
    public void createDatasetSourceData(DatasetSourceDataCreateReqVO createReqVO) {
        //根据文件ID获取文件信息
        DatasetStorageUpLoadRespVO datasetStorageUpLoadRespVO = datasetStorageService.getDatasetStorageByUID(createReqVO.getFiled());

        //获取字符数
        //int wordCount = getFileCharacterCountFromURL(datasetStorageUpLoadRespVO.getStorageKey());
        long wordCount;
        Tika tika = new Tika();
        try {
            wordCount = tika.parseToString(new URL(datasetStorageUpLoadRespVO.getStorageKey())).codePoints().count();
        } catch (Exception e) {
            log.info("split forecast error:", e);
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
        //封装查询条件
        LambdaQueryWrapper<DatasetSourceDataDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetSourceDataDO::getUid, createReqVO.getDatasetId());
        wrapper.eq(DatasetSourceDataDO::getTenantId, getLoginUser().getId());
        //获取当前文件位置
        Long position = datasetSourceDataMapper.selectCount(wrapper) +1;

        //异步分段和创建索引
        executeSplitAndIndex(createReqVO.getSplitRule(),createReqVO.getFiled(),createReqVO.getDatasetId(),datasetStorageUpLoadRespVO.getStorageKey());
        //插入
        DatasetSourceDataDO.DatasetSourceDataDOBuilder datasetSourceDataDO = DatasetSourceDataDO.builder();

        datasetSourceDataDO.uid(DatasetUID.getDatasetUID() );
        datasetSourceDataDO.name( datasetStorageUpLoadRespVO.getName() );
        datasetSourceDataDO.storageId( createReqVO.getFiled() );
        datasetSourceDataDO.position( position.intValue() );
        datasetSourceDataDO.createdFrom(SourceDataCreateEnum.BROWSER_INTERFACE.name());
        datasetSourceDataDO.wordCount( Long.valueOf(wordCount) );
        datasetSourceDataDO.datasetId(createReqVO.getDatasetId() );
        datasetSourceDataMapper.insert(datasetSourceDataDO.build());
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
    public void deleteDatasetSourceData(Long id) {
        // 校验存在
        validateDatasetSourceDataExists(id);
        // 删除
        datasetSourceDataMapper.deleteById(id);
    }

    private void validateDatasetSourceDataExists(Long id) {
        if (datasetSourceDataMapper.selectById(id) == null) {
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }
    }

    @Override
    public DatasetSourceDataDO getDatasetSourceData(Long id) {
        return datasetSourceDataMapper.selectById(id);
    }

    @Override
    public List<DatasetSourceDataDO> getDatasetSourceDataList(Collection<Long> ids) {
        return datasetSourceDataMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<DatasetSourceDataDO> getDatasetSourceDataPage(DatasetSourceDataPageReqVO pageReqVO) {
        return datasetSourceDataMapper.selectPage(pageReqVO);
    }


    /**
     * 异步分段和创建索引
     *
     * @param
     */
    @Async
    public void executeSplitAndIndex(SplitRule splitRule, String datasetId, String fileId, String url) {
        documentSegmentsService.splitAndIndex(splitRule,datasetId,fileId,url);
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