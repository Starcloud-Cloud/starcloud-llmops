package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryTableColumnMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_TABLE_COLUMN_NOT_EXISTS;

/**
 * 素材知识库表格信息 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialLibraryTableColumnServiceImpl implements MaterialLibraryTableColumnService {

    @Resource
    private MaterialLibraryTableColumnMapper materialLibraryTableColumnMapper;

    @Override
    public Long createMaterialLibraryTableColumn(MaterialLibraryTableColumnSaveReqVO createReqVO) {
        // 插入
        MaterialLibraryTableColumnDO materialLibraryTableColumn = BeanUtils.toBean(createReqVO, MaterialLibraryTableColumnDO.class);
        materialLibraryTableColumnMapper.insert(materialLibraryTableColumn);
        // 返回
        return materialLibraryTableColumn.getId();
    }

    @Override
    public void updateMaterialLibraryTableColumn(MaterialLibraryTableColumnSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialLibraryTableColumnExists(updateReqVO.getId());
        // 更新
        MaterialLibraryTableColumnDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibraryTableColumnDO.class);
        materialLibraryTableColumnMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialLibraryTableColumn(Long id) {
        // 校验存在
        validateMaterialLibraryTableColumnExists(id);
        // 删除
        materialLibraryTableColumnMapper.deleteById(id);
    }

    /**
     * 删除素材知识库表格信息
     *
     * @param libraryId 素材库编号
     */
    @Override
    public void deleteMaterialLibraryTableColumnByLibraryId(Long libraryId) {

        // 删除
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(libraryId);
        if (CollUtil.isEmpty(tableColumnDOList)) {
            return;
        }
        List<Long> removeIds = tableColumnDOList.stream().map(MaterialLibraryTableColumnDO::getId).collect(Collectors.toList());
        materialLibraryTableColumnMapper.deleteBatchIds(removeIds);

    }

    private void validateMaterialLibraryTableColumnExists(Long id) {
        if (materialLibraryTableColumnMapper.selectById(id) == null) {
            throw exception(MATERIAL_LIBRARY_TABLE_COLUMN_NOT_EXISTS);
        }
    }

    @Override
    public MaterialLibraryTableColumnDO getMaterialLibraryTableColumn(Long id) {
        return materialLibraryTableColumnMapper.selectById(id);
    }

    @Override
    public PageResult<MaterialLibraryTableColumnDO> getMaterialLibraryTableColumnPage(MaterialLibraryTableColumnPageReqVO pageReqVO) {
        return materialLibraryTableColumnMapper.selectPage(pageReqVO);
    }

    /**
     * 根据素材库编号获得素材知识库表格信息
     *
     * @param libraryId 编号
     * @return 素材知识库表格信息列表
     */
    @Override
    public List<MaterialLibraryTableColumnDO> getMaterialLibraryTableColumnByLibrary(Long libraryId) {
        // 明确校验一下
        Assert.notNull(libraryId, "获取素材库表头数据失败，素材库编号为空");
        return materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(libraryId);
    }

}