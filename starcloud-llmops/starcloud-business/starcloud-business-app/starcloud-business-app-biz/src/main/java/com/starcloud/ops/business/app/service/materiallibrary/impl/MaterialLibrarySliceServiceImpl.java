package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibrarySliceMapper;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.MATERIAL_IMAGE_REDIS_PREFIX;

/**
 * 素材知识库数据 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibrarySliceServiceImpl implements MaterialLibrarySliceService {

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibrarySliceMapper materialLibrarySliceMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Long createMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO createReqVO) {
        // 插入
        MaterialLibrarySliceDO sliceDO = BeanUtils.toBean(createReqVO, MaterialLibrarySliceDO.class);

        long nextSequence = 1L;
        // 设置数据最新的序号
        MaterialLibrarySliceDO lastSequenceSliceDO = materialLibrarySliceMapper.selectLastSequence(sliceDO.getLibraryId());

        if (lastSequenceSliceDO != null) {
            nextSequence = nextSequence + 1;
        }
        sliceDO.setSequence(nextSequence);

        materialLibrarySliceMapper.insert(sliceDO);
        // 返回
        return sliceDO.getId();
    }

    /**
     * 批量创建素材知识库数据
     *
     * @param createReqVO 批量创建VO
     */
    @Override
    public void createBatchMaterialLibrarySlice(MaterialLibrarySliceBatchSaveReqVO createReqVO) {

        if (createReqVO.getSaveReqVOS().isEmpty()) {
            return;
        }
        List<Long> libraryIds = createReqVO.getSaveReqVOS().stream()
                .map(MaterialLibrarySliceSaveReqVO::getLibraryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryIds.get(0));

        // 批量添加时 对空数据做填充
        List<MaterialLibrarySliceSaveReqVO> saveReqVOS = createReqVO.getSaveReqVOS();

        saveReqVOS.forEach(saveReq -> {
            if (saveReq.getContent() == null || saveReq.getContent().size() != tableColumnDOList.size()) {
                throw exception(MATERIAL_LIBRARY_SLICE_DATA_MISSING);
            }
        });


        this.saveBatchData(BeanUtils.toBean(createReqVO.getSaveReqVOS(), MaterialLibrarySliceDO.class));
    }

    @Override
    public void updateMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialLibrarySliceExists(updateReqVO.getId());
        // 更新
        MaterialLibrarySliceDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.updateById(updateObj);
    }

    /**
     * 批量更新素材知识库数据
     *
     * @param updateReqVO 批量更新 VO
     */
    @Override
    public void updateBatchMaterialLibrarySlice(MaterialLibrarySliceBatchSaveReqVO updateReqVO) {
        materialLibrarySliceMapper.updateBatch(BeanUtils.toBean(updateReqVO.getSaveReqVOS(), MaterialLibrarySliceDO.class));
    }

    @Override
    public void deleteMaterialLibrarySlice(Long id) {
        // 校验存在
        validateMaterialLibrarySliceExists(id);

        MaterialLibrarySliceDO sliceDO = materialLibrarySliceMapper.selectById(id);
        if (sliceDO == null) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
        // 删除
        materialLibrarySliceMapper.deleteById(id);

    }


    @Override
    public MaterialLibrarySliceDO getMaterialLibrarySlice(Long id) {
        return materialLibrarySliceMapper.selectById(id);
    }

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @return 素材知识库数据
     */
    @Override
    public List<MaterialLibrarySliceDO> getMaterialLibrarySliceByLibraryId(Long libraryId) {
        return materialLibrarySliceMapper.selectListByLibraryId(libraryId);
    }

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param slices    素材编号
     * @return 素材知识库数据
     */
    @Override
    public List<MaterialLibrarySliceDO> getMaterialLibrarySlice(Long libraryId, List<Long> slices) {
        return materialLibrarySliceMapper.selectList(libraryId, slices);
    }

    @Override
    public PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePage(MaterialLibrarySlicePageReqVO pageReqVO) {
        if (Objects.isNull(pageReqVO.getLibraryId())) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }
        materialLibraryService.validateMaterialLibraryExists(pageReqVO.getLibraryId());

        return materialLibrarySliceMapper.selectPage(pageReqVO);
    }


    /**
     * 根据素材库 ID 获取素材数量
     *
     * @param libraryId 素材库 编号
     * @return 共享数据列表
     */
    @Override
    public Long getSliceDataCountByLibraryId(Long libraryId) {
        return materialLibrarySliceMapper.selectSliceDataCountByLibraryId(libraryId);
    }

    /**
     * 根据素材库编号 删除素材库数据
     *
     * @param libraryId 素材库编号
     */
    @Override
    public void deleteMaterialLibrarySliceByLibraryId(Long libraryId) {
        materialLibrarySliceMapper.deleteSliceByLibraryId(libraryId);
        // materialLibraryService.updateMaterialLibraryFileCount(libraryId);
    }

    /**
     * 批量删除
     *
     * @param ids 素材编列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        MaterialLibrarySliceDO materialLibrarySliceDO = materialLibrarySliceMapper.selectById(ids.get(0));
        materialLibrarySliceMapper.deleteBatchIds(ids);
    }

    /**
     * @param libraryId         素材库编号
     * @param sliceIdList       选定的素材编号
     * @param removeSliceIdList 需要移除的素材列表
     * @param sortingField      排序字段
     */
    @Override
    public List<MaterialLibrarySliceRespVO> selectSliceBySortingField(Long libraryId, List<Long> sliceIdList, List<Long> removeSliceIdList, SortingField sortingField) {
        List<MaterialLibrarySliceDO> sliceDOList = materialLibrarySliceMapper.selectSliceListByUserLibraryId(libraryId, sliceIdList, removeSliceIdList, sortingField);

        return BeanUtils.toBean(sliceDOList, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param pageReqVO 分页 VO
     * @return page
     */
    @Override
    public PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePageByLibraryUid(MaterialLibrarySlicePageReqVO pageReqVO) {

        if (StrUtil.isBlank(pageReqVO.getLibraryUid())) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }
        MaterialLibraryDO materialLibraryDO = materialLibraryService.validateMaterialLibraryExists(pageReqVO.getLibraryUid());

        return materialLibrarySliceMapper.selectPage(pageReqVO.setLibraryId(materialLibraryDO.getId()));

    }

    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param appUid 应用 编号
     * @return Page
     */
    @Override
    public List<MaterialLibrarySliceRespVO> getMaterialLibrarySliceListByAppUid(String appUid) {
        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appUid);

        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        materialLibraryService.validateMaterialLibraryExists(bind.getLibraryId());

        List<MaterialLibrarySliceDO> sliceDOList = this.getMaterialLibrarySliceByLibraryId(bind.getLibraryId());

        return BeanUtils.toBean(sliceDOList, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 通过应用 UID 获取素材数据
     *
     * @param appPageReqVO 应用
     * @return Page
     */
    @Override
    public PageResult<MaterialLibrarySliceRespVO> getMaterialLibrarySlicePageByApp(MaterialLibrarySliceAppPageReqVO appPageReqVO) {

        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appPageReqVO.getAppUid());

        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        materialLibraryService.validateMaterialLibraryExists(bind.getLibraryId());

        PageResult<MaterialLibrarySliceDO> pageResult = materialLibrarySliceMapper.selectPage2(bind.getLibraryId(), appPageReqVO);

        return BeanUtils.toBean(pageResult, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 更新素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param sliceId   素材编号
     * @param usedCount 使用次数
     */
    @Override
    public void updateSliceUsedCount(Long libraryId, Long sliceId, Integer usedCount) {
        log.info("updateSliceUsedCount : libraryId:{},sliceId:{},usedCount:{}", libraryId, sliceId, usedCount);
        MaterialLibrarySliceDO slice = materialLibrarySliceMapper.selectById(sliceId);

        if (slice == null) {
            return;
        }
        materialLibrarySliceMapper.updateById(new MaterialLibrarySliceDO().setId(slice.getId()).setUsedCount(slice.getUsedCount() + usedCount));
    }

    /**
     * 仅仅复制一个新的素材库数据（不做数据操作）templateApp -> appReqVO
     *
     * @param templateLibraryId 新应用
     * @param libraryId         老应用
     */
    @Override
    public void materialLibrarySliceCopy(Long templateLibraryId, Long libraryId) {
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryId);
        if (tableColumnDOList.isEmpty()) {
            return;
        }

        MaterialLibrarySlicePageReqVO pageReqVO = new MaterialLibrarySlicePageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(100);
        pageReqVO.setLibraryId(templateLibraryId);


        AtomicReference<List<MaterialLibrarySliceDO>> templateSliceDOList = new AtomicReference<>();

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        DataPermissionUtils.executeIgnore(() -> {

            templateSliceDOList.set(getMaterialLibrarySlicePage(pageReqVO).getList());

        });


        if (templateSliceDOList.get().isEmpty()) {
            return;
        }

        templateSliceDOList.get().forEach(sliceData -> {
            sliceData.setId(null);
            sliceData.setCreator(null);
            sliceData.setUpdater(null);
            sliceData.setLibraryId(libraryId);
            List<MaterialLibrarySliceDO.TableContent> datasList = sliceData.getContent();
            if (datasList != null) {
                datasList.forEach(datas -> {
                    if (datas != null && datas.getColumnCode() != null) {
                        MaterialLibraryTableColumnDO newColumnDO = findColumnDOByCode(tableColumnDOList, datas.getColumnCode());
                        if (newColumnDO != null) {
                            datas.setColumnId(newColumnDO.getId());
                        }
                    }
                });
            }
        });


        saveBatchData(templateSliceDOList.get());

    }

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @return 素材知识库数据
     */
    @Override
    public Long getMaterialLibrarySliceCountByLibraryId(Long libraryId) {
        return materialLibrarySliceMapper.selectCountByLibraryId(libraryId);
    }

    /**
     * @param saveReqVOS    需要保存的数据
     * @param otherFileKeys 需要处理的文件
     */
    @Async
    @Override
    public void batchSaveDataAndExecuteOtherFile(List<MaterialLibrarySliceSaveReqVO> saveReqVOS, List<String> otherFileKeys) {

        Long libraryId = saveReqVOS.stream().map(MaterialLibrarySliceSaveReqVO::getLibraryId).findFirst().orElse(null);
        if (libraryId == null) {
            log.error("素材数据存储失败，数据为{}", JSONObject.toJSONString(saveReqVOS));
            return;
        }
        validateUploadIsSuccess(buildRedisKey(otherFileKeys, libraryId));

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryId);

        Map<Integer, List<String>> imageOrDocumentColumn = getImageOrDocumentColumn(tableColumnDOList);
        if (imageOrDocumentColumn.isEmpty()) {
            List<MaterialLibrarySliceDO> bean = BeanUtils.toBean(saveReqVOS, MaterialLibrarySliceDO.class);
            if (getLoginUserId() == null) {
                bean.stream().forEach(material -> {
                    material.setCreator(tableColumnDOList.get(0).getCreator());
                    material.setUpdater(tableColumnDOList.get(0).getCreator());
                });
            }
            materialLibrarySliceMapper.insertBatch(bean);
            return;
        }

        List<String> imageColumn = imageOrDocumentColumn.get(ColumnTypeEnum.IMAGE.getCode());

        if (CollUtil.isNotEmpty(imageColumn)) {

            saveReqVOS.forEach(saveReqVO -> {
                saveReqVO.getContent().stream()
                        .filter(content -> imageColumn.contains(content.getColumnCode()) && StrUtil.isNotBlank(content.getValue()))
                        .forEach(content -> content.setValue(redisTemplate.boundValueOps(getRedisKey(content.getValue(), libraryId)).get()));
            });
        }

        List<String> documentColumn = imageOrDocumentColumn.get(ColumnTypeEnum.DOCUMENT.getCode());


        if (CollUtil.isNotEmpty(documentColumn)) {
            saveReqVOS.forEach(data -> {
                data.getContent().stream()
                        .filter(content -> documentColumn.contains(content.getColumnCode()) && StrUtil.isNotBlank(content.getValue()))
                        .forEach(content -> {
                            String key = getRedisKey(content.getValue(), libraryId);
                            String urlsString = redisTemplate.boundValueOps(key).get();

                            List<String> urls = null;
                            if (StrUtil.isNotBlank(urlsString)) {
                                urls = new ArrayList<>(Arrays.asList(urlsString.split(",")));
                            }

                            if (urls != null) {
                                ListIterator<String> iterator = urls.listIterator();

                                String value = content.getValue();
                                // 如果列类型为图片且值为空或为NULL，则设置新的图片值
                                if (imageColumn.contains(content.getColumnCode())
                                        && (StrUtil.isBlank(value) || StrUtil.NULL.equalsIgnoreCase(value))
                                        && iterator.hasNext()) {
                                    content.setValue(iterator.next());
                                }
                            }
                        });
            });

        }
        List<MaterialLibrarySliceDO> bean = BeanUtils.toBean(saveReqVOS, MaterialLibrarySliceDO.class);
        if (getLoginUserId() == null) {
            bean.stream().forEach(material -> {
                material.setCreator(tableColumnDOList.get(0).getCreator());
                material.setUpdater(tableColumnDOList.get(0).getCreator());
            });
        }
        materialLibrarySliceMapper.insertBatch(bean);
    }

    /**
     * 列 删除后 删除数据内的列
     *
     * @param columnCodes 列编码
     * @param libraryId   素材库编号
     */
    @Override
    @Async
    public void asyncUpdateSliceByColumnCodeDelete(List<String> columnCodes, Long libraryId) {
        ArrayList<String> columnCodesToDelete = CollUtil.distinct(columnCodes);
        List<MaterialLibrarySliceDO> sliceDOList = this.getMaterialLibrarySliceByLibraryId(libraryId);
        if (CollUtil.isEmpty(sliceDOList)){
            return;
        }

        sliceDOList.forEach(data -> {
            Optional.ofNullable(data.getContent())
                    .ifPresent(contentList -> {
                        if (!contentList.isEmpty()) { // 使用 isEmpty() 替换 CollUtil
                            contentList.removeIf(content -> columnCodesToDelete.contains(content.getColumnCode()));
                        }
                    });
        });
        materialLibrarySliceMapper.updateBatch(sliceDOList);
    }

    private void validateUploadIsSuccess(List<String> otherFileKeys) {

        ArrayList<String> distinct = CollUtil.distinct(otherFileKeys);
        final int MAX_RETRIES = 30;
        final long RETRY_DELAY_SECONDS = 3L;

        // 循环尝试获取文件状态，最多尝试 MAX_RETRIES 次
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                // 计算其他文件键在 Redis 中的实际存在数量
                Long existingCount = redisTemplate.countExistingKeys(distinct);

                // 如果所有文件都已存在，则认为上传成功，直接返回
                if (existingCount != null && existingCount.equals((long) distinct.size())) {
                    System.out.println("All files have been successfully uploaded.");
                    return;
                }

                // 等待 RETRY_DELAY_SECONDS 秒后再次尝试
                TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                System.err.println("Thread was interrupted, Failed to complete operation");
                return;
            } catch (Exception e) {
                // 处理其他可能的异常
                System.err.println("An error occurred while checking file status: " + e.getMessage());
                return;
            }
        }

        // 如果经过多次尝试仍然没有所有文件，则认为上传失败
        System.err.println("Not all files were successfully uploaded after multiple retries.");


    }


    private MaterialLibraryTableColumnDO findColumnDOByCode(List<MaterialLibraryTableColumnDO> tableColumnDOList, String columnCode) {
        for (MaterialLibraryTableColumnDO tableColumnDO : tableColumnDOList) {
            if (tableColumnDO.getColumnCode().equals(columnCode)) {
                return tableColumnDO; // 找到匹配的ColumnDO，返回之
            }
        }
        return null; // 如果没有找到匹配的ColumnDO，返回null
    }

    /**
     * 校验数据是否存在
     *
     * @param id 数据编号
     */
    private void validateMaterialLibrarySliceExists(Long id) {
        if (materialLibrarySliceMapper.selectById(id) == null) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
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

        List<MaterialLibrarySliceDO> bean = BeanUtils.toBean(list, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.insertBatch(bean);
        return list.size();

    }

    private static Map<Integer, List<String>> getImageOrDocumentColumn(List<MaterialLibraryTableColumnDO> materialConfigList) {
        return materialConfigList.stream()
                .filter(column -> Objects.equals(column.getColumnType(), ColumnTypeEnum.IMAGE.getCode())
                        || Objects.equals(column.getColumnType(), ColumnTypeEnum.DOCUMENT.getCode()))
                .sorted(Comparator.comparing(MaterialLibraryTableColumnDO::getSequence)
                        .thenComparing(MaterialLibraryTableColumnDO::getId))
                .collect(Collectors.groupingBy(
                        MaterialLibraryTableColumnDO::getColumnType, // 分组依据
                        Collectors.mapping(MaterialLibraryTableColumnDO::getColumnCode, Collectors.toList())));
    }

    private String getRedisKey(String data, Long libraryId) {
        return String.format(MATERIAL_IMAGE_REDIS_PREFIX, libraryId, data);
    }

    private List<String> buildRedisKey(List<String> keys, Long libraryId) {
        return CollUtil.distinct(keys).stream()
                .map(key -> getRedisKey(key, libraryId))
                .collect(Collectors.toList());
    }


}