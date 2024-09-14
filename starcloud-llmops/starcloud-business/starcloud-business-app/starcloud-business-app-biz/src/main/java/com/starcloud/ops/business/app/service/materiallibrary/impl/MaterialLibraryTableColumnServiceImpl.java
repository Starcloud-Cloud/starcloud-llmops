package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnBatchSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryTableColumnMapper;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.util.PinyinUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.util.RandomUtil.BASE_CHAR_NUMBER_LOWER;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

/**
 * 素材知识库表格信息 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibraryTableColumnServiceImpl implements MaterialLibraryTableColumnService {


    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibraryTableColumnMapper materialLibraryTableColumnMapper;

    @Resource
    @Lazy
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Override
    public Long createMaterialLibraryTableColumn(MaterialLibraryTableColumnSaveReqVO createReqVO) {
        MaterialLibraryDO materialLibraryDO = materialLibraryService.validateMaterialLibraryExists(createReqVO.getLibraryId());

        if (!MaterialFormatTypeEnum.isExcel(materialLibraryDO.getFormatType())) {
            throw exception(MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_NO_EXCEL);
        }
        validateMaterialLibraryTableColumnName(createReqVO.getLibraryId(), null, Collections.singletonList(createReqVO.getColumnName()));

        // 填充ColumnCode
        generateColumnCode(Collections.singletonList(createReqVO));

        validateMaterialLibraryTableColumnType(createReqVO);

        // 插入
        MaterialLibraryTableColumnDO materialLibraryTableColumn = BeanUtils.toBean(createReqVO, MaterialLibraryTableColumnDO.class);
        materialLibraryTableColumnMapper.insert(materialLibraryTableColumn);
        // 返回
        return materialLibraryTableColumn.getId();
    }


    @Override
    public void updateMaterialLibraryTableColumn(MaterialLibraryTableColumnSaveReqVO updateReqVO) {

        MaterialLibraryDO materialLibraryDO = materialLibraryService.validateMaterialLibraryExists(updateReqVO.getLibraryId());

        if (!MaterialFormatTypeEnum.isExcel(materialLibraryDO.getFormatType())) {
            throw exception(MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_NO_EXCEL);
        }
        // 校验存在
        validateMaterialLibraryTableColumnExists(updateReqVO.getId());
        // 校验名称
        validateMaterialLibraryTableColumnName(updateReqVO.getLibraryId(), updateReqVO.getId(), Collections.singletonList(updateReqVO.getColumnName()));
        // 填充ColumnCode
        // generateColumnCode(Collections.singletonList(updateReqVO));

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

        materialLibraryService.validateMaterialLibraryExists(libraryId);

        return materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(libraryId);
    }

    /**
     * 批量更新表格字段
     *
     * @param batchSaveReqVO 批量更新 VO
     */
    @Override
    public void updateBatchByLibraryId(MaterialLibraryTableColumnBatchSaveReqVO batchSaveReqVO) {
        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS = batchSaveReqVO.getTableColumnSaveReqVOList();
        List<MaterialLibraryTableColumnDO> newList = BeanUtils.toBean(saveReqVOS, MaterialLibraryTableColumnDO.class);
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<MaterialLibraryTableColumnDO> oldList = materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(batchSaveReqVO.getLibraryId());

        List<List<MaterialLibraryTableColumnDO>> diffList =
                diffList(oldList, newList, // id 不同，就认为是不同的记录
                        (oldVal, newVal) -> ObjectUtil.equal(oldVal.getId(), newVal.getId()));
        // 优先删除 避免同名的情况
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            materialLibraryTableColumnMapper.deleteBatchIds(convertList(diffList.get(2), MaterialLibraryTableColumnDO::getId));
            materialLibrarySliceService.asyncUpdateSliceByColumnCodeDelete(diffList.get(2).stream().map(MaterialLibraryTableColumnDO::getColumnCode).collect(Collectors.toList()), diffList.get(2).stream().map(MaterialLibraryTableColumnDO::getLibraryId).collect(Collectors.toList()).get(0));
        }

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = diffList.get(0);
            List<MaterialLibraryTableColumnSaveReqVO> saveReqVOList = BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnSaveReqVO.class);
            generateColumnCode(saveReqVOList);
            List<MaterialLibraryTableColumnDO> bean = BeanUtils.toBean(saveReqVOList, MaterialLibraryTableColumnDO.class);
            materialLibraryTableColumnMapper.insertBatch(bean);
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            materialLibraryTableColumnMapper.updateBatch(diffList.get(1));
        }
    }


    /**
     * 仅仅复制一个新的素材库表头templateLibraryId -> libraryId
     *
     * @param templateLibraryId 模板素材库编号
     * @param libraryId         素材库编号
     */
    @Override
    public void materialLibraryCopy(Long templateLibraryId, Long libraryId) {

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        AtomicReference<List<MaterialLibraryTableColumnDO>> templateTableColumnDOList = new AtomicReference<>();
        DataPermissionUtils.executeIgnore(() -> {

            templateTableColumnDOList.set(getMaterialLibraryTableColumnByLibrary(templateLibraryId));

        });


        if (CollUtil.isEmpty(templateTableColumnDOList.get())) {
            log.info("materialLibraryCopy:Skip replication if table header is empty");
            return;
        }
        List<MaterialLibraryTableColumnSaveReqVO> newTableColumnSaveList = BeanUtils.toBean(templateTableColumnDOList.get(), MaterialLibraryTableColumnSaveReqVO.class);
        newTableColumnSaveList.forEach(data -> {
            data.setLibraryId(libraryId);
            data.setId(null);
        });
        saveBatchData(newTableColumnSaveList);

    }

    /**
     * 切换绑定校验
     *
     * @param saveReqVO 换绑 VO
     */
    @Override
    public Boolean validateSwitchBind(MaterialLibraryAppBindSaveReqVO saveReqVO) {
        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(saveReqVO.getAppUid());
        materialLibraryService.validateMaterialLibraryExists(bind.getLibraryId());

        Set<String> existingColumnCodes = getMaterialLibraryTableColumnByLibrary(bind.getLibraryId()).stream()
                .map(MaterialLibraryTableColumnDO::getColumnCode)
                .filter(StringUtils::isNotBlank) // 防止空字符串
                .collect(Collectors.toSet());

        Set<String> newColumnCodes = getMaterialLibraryTableColumnByLibrary(saveReqVO.getLibraryId()).stream()
                .map(MaterialLibraryTableColumnDO::getColumnCode)
                .filter(StringUtils::isNotBlank) // 防止空字符串
                .collect(Collectors.toSet());

        return CollectionUtils.containsAll(newColumnCodes, existingColumnCodes);


    }

    /**
     * 批量保存数据
     *
     * @param list 要保存的数据
     * @return Integer 保存成功的条数
     */
    @Override
    public <T> Integer saveBatchData(List<T> list) {

        if (CollUtil.isEmpty(list)) {
            return 0;
        }

        List<MaterialLibraryTableColumnDO> bean = BeanUtils.toBean(list, MaterialLibraryTableColumnDO.class);

        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;
        try {
            saveReqVOS = BeanUtils.toBean(bean, MaterialLibraryTableColumnSaveReqVO.class);
        } catch (RuntimeException e) {
            throw exception(MATERIAL_LIBRARY_TABLE_COULMN_BATCH_ADD_FAIL);
        }
        List<Long> collect = saveReqVOS.stream().map(MaterialLibraryTableColumnSaveReqVO::getLibraryId).collect(Collectors.toList());
        List<String> columnNames = saveReqVOS.stream().map(MaterialLibraryTableColumnSaveReqVO::getColumnName).collect(Collectors.toList());
        validateMaterialLibraryTableColumnName(collect.get(0), null, columnNames);

        // 填充ColumnCode
        generateColumnCode(saveReqVOS);

        bean = BeanUtils.toBean(saveReqVOS, MaterialLibraryTableColumnDO.class);

        materialLibraryTableColumnMapper.insertBatch(bean);
        return list.size();
    }

    @Override
    public void updateColumn(String sourceUid, String targetUid) {
        MaterialLibraryAppBindDO sourceBind = materialLibraryAppBindService.getMaterialLibraryAppBind(sourceUid);
        if (Objects.isNull(sourceBind)) {
            throw exception(new ErrorCode(500, "更新素材标题 sourceUid=" + sourceUid));
        }
        List<MaterialLibraryTableColumnDO> sourceColumns = materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(sourceBind.getLibraryId());

        MaterialLibraryAppBindDO targetBind = materialLibraryAppBindService.getMaterialLibraryAppBind(targetUid);
        if (Objects.isNull(targetBind)) {
            throw exception(new ErrorCode(500, "更新素材标题 targetUid=" + targetUid));
        }

        Long targetLibraryId = targetBind.getLibraryId();
        List<MaterialLibraryTableColumnDO> targetColumns = materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(targetLibraryId);

        List<List<MaterialLibraryTableColumnDO>> diffList =
                diffList(targetColumns, sourceColumns,
                        (target, source) -> ObjectUtil.equal(target.getColumnCode(), source.getColumnCode()));

        // 删除列
        List<String> deleteColumns = diffList.get(2).stream().map(MaterialLibraryTableColumnDO::getColumnCode).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deleteColumns)) {
            // 删除表头
            materialLibraryTableColumnMapper.deleteByLibraryId(
                    targetLibraryId,
                    deleteColumns
            );
            // 删除列数据
            materialLibrarySliceService.asyncUpdateSliceByColumnCodeDelete(
                    deleteColumns,
                    targetLibraryId);
        }

        // 新增列
        List<MaterialLibraryTableColumnDO> addColumns = diffList.get(0);
        if (CollUtil.isNotEmpty(addColumns)) {
            List<MaterialLibraryTableColumnDO> columnDOList = new ArrayList<>(addColumns.size());
            for (MaterialLibraryTableColumnDO addColumn : addColumns) {
                MaterialLibraryTableColumnDO columnDO = BeanUtils.toBean(addColumn, MaterialLibraryTableColumnDO.class, "id", "libraryId");
                columnDO.setLibraryId(targetLibraryId);
                columnDOList.add(columnDO);
            }
            materialLibraryTableColumnMapper.insertBatch(columnDOList);
        }

        List<MaterialLibraryTableColumnDO> updateColumns = diffList.get(1);
        Map<String, MaterialLibraryTableColumnDO> targetColumnMaps = targetColumns.stream().collect(Collectors.toMap(MaterialLibraryTableColumnDO::getColumnCode, Function.identity(), (a, b) -> a));

        // 更新列
        if (CollUtil.isNotEmpty(updateColumns)) {
            List<MaterialLibraryTableColumnDO> columnDOList = new ArrayList<>(updateColumns.size());
            for (MaterialLibraryTableColumnDO updateColumn : updateColumns) {
                MaterialLibraryTableColumnDO columnDO = targetColumnMaps.get(updateColumn.getColumnCode());
                BeanUtil.copyProperties(updateColumn, columnDO, "id", "libraryId");
                columnDOList.add(columnDO);
            }
            materialLibraryTableColumnMapper.updateBatch(columnDOList);
        }
    }


    //  TODO 验证 文档类型的字段只有一个
    private void validateMaterialLibraryTableColumnType(MaterialLibraryTableColumnSaveReqVO createReqVO) {

        // if ()
        // if (materialLibraryTableColumnMapper.selectCountByName(libraryId, ignoreId, columnNames) > 0) {
        //     throw exception(MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_SAME_COULMN, columnNames);
        // }
    }

    /**
     * 生成 列code
     */
    private void generateColumnCode(List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS) {

        if (CollUtil.isEmpty(saveReqVOS)) {
            return;
        }

        Set<String> columnCodeExistList = new HashSet<>();

        List<Long> collect = saveReqVOS.stream().map(MaterialLibraryTableColumnSaveReqVO::getLibraryId).collect(Collectors.toList());

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnMapper.selectMaterialLibraryTableColumnByLibrary(collect.get(0));

        if (CollUtil.isNotEmpty(tableColumnDOList)) {
            columnCodeExistList.addAll(tableColumnDOList.stream().map(MaterialLibraryTableColumnDO::getColumnCode).collect(Collectors.toSet()));
        }

        for (MaterialLibraryTableColumnSaveReqVO saveReqVO : saveReqVOS) {
            // 已有ColumnCode的字段跳过
            if (StringUtils.isNoneBlank(saveReqVO.getColumnCode())) {
                columnCodeExistList.add(saveReqVO.getColumnCode());
                continue;
            }

            String columnName = saveReqVO.getColumnName();
            char[] nameChar = columnName.trim().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : nameChar) {
                sb.append(PinyinUtils.pinyinFirstChar(c));
            }
            String code = pinyinFirstCharUnique(sb.toString(), columnCodeExistList);
            columnCodeExistList.add(code);
            saveReqVO.setColumnCode(code);
        }
    }


    private void validateMaterialLibraryTableColumnExists(Long id) {
        if (materialLibraryTableColumnMapper.selectById(id) == null) {
            throw exception(MATERIAL_LIBRARY_TABLE_COLUMN_NOT_EXISTS);
        }
    }

    /**
     * 验证同名列
     *
     * @param libraryId 素材库编号
     */
    private void validateMaterialLibraryTableColumnName(Long libraryId, Long ignoreId, List<String> columnNames) {
        if (materialLibraryTableColumnMapper.selectCountByName(libraryId, ignoreId, columnNames) > 0) {
            throw exception(MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_SAME_COULMN, columnNames);
        }
    }


    /**
     * code重复拼接随机字符串
     *
     * @param columnCode          列 code
     * @param columnCodeExistList 已经存在的列 code
     * @return 列 code
     */
    private String pinyinFirstCharUnique(String columnCode, Set<String> columnCodeExistList) {
        if (columnCodeExistList.contains(columnCode)) {
            return pinyinFirstCharUnique(columnCode + RandomUtil.randomString(BASE_CHAR_NUMBER_LOWER, 1), columnCodeExistList);
        }
        return columnCode;
    }


}