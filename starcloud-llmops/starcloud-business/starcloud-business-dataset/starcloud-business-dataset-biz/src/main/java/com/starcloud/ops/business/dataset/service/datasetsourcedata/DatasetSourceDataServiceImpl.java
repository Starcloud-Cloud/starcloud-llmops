package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_SOURCE_DATA_NOT_EXISTS;


/**
 * 数据集源数据 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DatasetSourceDataServiceImpl implements DatasetSourceDataService {

    @Resource
    private DatasetSourceDataMapper datasetSourceDataMapper;

    @Override
    public Long createDatasetSourceData(DatasetSourceDataCreateReqVO createReqVO) {
        //获取当前用户信息
        //获取当前租户下配置
        //校验源数据是否符合租户下配置
        //文件上传
        //插入
        DatasetSourceDataDO datasetSourceData = DatasetSourceDataConvert.INSTANCE.convert(createReqVO);
        datasetSourceDataMapper.insert(datasetSourceData);
        // 返回
        return datasetSourceData.getId();
    }

    @Override
    public void updateDatasetSourceData(DatasetSourceDataUpdateReqVO updateReqVO) {
        // 校验存在
        validateDatasetSourceDataExists(updateReqVO.getId());
        // 更新
        DatasetSourceDataDO updateObj = DatasetSourceDataConvert.INSTANCE.convert(updateReqVO);
        datasetSourceDataMapper.updateById(updateObj);
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


}