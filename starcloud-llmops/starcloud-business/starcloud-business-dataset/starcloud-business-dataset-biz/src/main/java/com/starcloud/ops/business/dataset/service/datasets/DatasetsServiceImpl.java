package com.starcloud.ops.business.dataset.service.datasets;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsExportReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasets.DatasetsConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasets.DatasetsMapper;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;

/**
 * 数据集 Service 实现类
 *
 * @author AlanCusack
 */
@Service
@Validated
public class DatasetsServiceImpl implements DatasetsService {

    @Resource
    private DatasetsMapper datasetsMapper;

    @Override
    public String createDatasets(DatasetsCreateReqVO createReqVO) {
        //数据非空校验
        if (ObjectUtil.isEmpty(createReqVO)) {
            throw exception(DATASETS_PARAM_NULL);
        }
        // TODO 校验当前用户权限 是否可以创建数据集

        // 数据转换
        DatasetsDO datasets = DatasetsConvert.convert(createReqVO, DatasetUID.getDatasetUID());
        //数据插入
        datasetsMapper.insert(datasets);
        // 返回
        return datasets.getUid();
    }

    @Override
    public void updateDatasets(DatasetsUpdateReqVO updateReqVO) {
        // 校验存在
        validateDatasetsExists(updateReqVO.getId());
        // 更新
        DatasetsDO updateObj = DatasetsConvert.convert(updateReqVO);
        datasetsMapper.updateById(updateObj);
    }

    @Override
    public void deleteDatasets(Long id) {
        // 校验存在
        validateDatasetsExists(id);
        // 删除
        datasetsMapper.deleteById(id);
    }

    private void validateDatasetsExists(Long id) {
        if (datasetsMapper.selectById(id) == null) {
            throw exception(DATASETS_NOT_EXISTS);
        }
    }

    @Override
    public DatasetsDO getDatasets(String uid) {
        LambdaQueryWrapper<DatasetsDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetsDO::getUid, uid);
        DatasetsDO datasetsDO;
        try {
            datasetsDO = datasetsMapper.selectOne(wrapper);
        }catch (RuntimeException e){
            throw exception(DATASETS_ERROR_REPEAT,uid);
        }
       return datasetsDO;
    }

    @Override
    public List<DatasetsDO> getDatasetsList(Collection<Long> ids) {
        return datasetsMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<DatasetsDO> getDatasetsPage(DatasetsPageReqVO pageReqVO) {
        return datasetsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DatasetsDO> getDatasetsList(DatasetsExportReqVO exportReqVO) {
        return datasetsMapper.selectList(exportReqVO);
    }
}
