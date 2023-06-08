package com.starcloud.ops.business.dataset.convert.datasets;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import lombok.experimental.UtilityClass;

/**
 * @className    : DatasetsConvert
 * @description  : [数据集 Convert]
 * @author       : [AlanCusack]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 15:59]
 * @updateUser   : [AlanCusack]
 * @updateTime   : [2023/5/31 15:59]
 * @updateRemark : [暂无修改]
 */
@UtilityClass
public class DatasetsConvert {


    public DatasetsDO convert(DatasetsCreateReqVO bean, String UID) {
        DatasetsDO.DatasetsDOBuilder datasetsDO = DatasetsDO.builder();
        datasetsDO.name(bean.getName());
        datasetsDO.uid(UID);
        datasetsDO.description(bean.getDescription());
        datasetsDO.permission(bean.getPermission());
        datasetsDO.indexingModel(bean.getIndexingModel());

        return datasetsDO.build();
    }

    public DatasetsDO convert(DatasetsUpdateReqVO bean) {

        DatasetsDO.DatasetsDOBuilder datasetsDO = DatasetsDO.builder();
        datasetsDO.name(bean.getName());
        datasetsDO.description(bean.getDescription());
        datasetsDO.permission(bean.getPermission());
        datasetsDO.indexingModel(bean.getIndexingModel());
        return datasetsDO.build();
    }

    public DatasetsRespVO convert(DatasetsDO bean) {

        DatasetsRespVO datasetsRespVO = new DatasetsRespVO();

        datasetsRespVO.setName(bean.getName());
        datasetsRespVO.setDescription(bean.getDescription());
        datasetsRespVO.setPermission(bean.getPermission());
        datasetsRespVO.setIndexingModel(bean.getIndexingModel());
        datasetsRespVO.setId(bean.getId());
        datasetsRespVO.setCreateTime(bean.getCreateTime());

        return datasetsRespVO;
    }


    public List<DatasetsRespVO> convertList(List<DatasetsDO> list) {
        if (list == null) {
            return null;
        }

        List<DatasetsRespVO> list1 = new ArrayList<DatasetsRespVO>(list.size());
        for (DatasetsDO datasetsDO : list) {
            list1.add(convert(datasetsDO));
        }

        return list1;
    }

    public PageResult<DatasetsRespVO> convertPage(PageResult<DatasetsDO> page) {
        if (page == null) {
            return null;
        }

        PageResult<DatasetsRespVO> pageResult = new PageResult<DatasetsRespVO>();

        pageResult.setList(convertList(page.getList()));
        pageResult.setTotal(page.getTotal());

        return pageResult;
    }
}