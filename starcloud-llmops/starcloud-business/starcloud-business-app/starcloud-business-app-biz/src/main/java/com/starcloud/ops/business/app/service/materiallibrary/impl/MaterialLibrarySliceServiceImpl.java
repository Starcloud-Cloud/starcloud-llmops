package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_MS_PATTERN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.*;
import static com.starcloud.ops.business.app.service.materiallibrary.config.MaterialLibraryDataUploadJobConfiguration.LIBRARY_DATA_UPLOAD_THREAD_POOL_TASK_EXECUTOR;

/**
 * 素材知识库数据 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibrarySliceServiceImpl implements MaterialLibrarySliceService {

    // 定义常量
    private static final int EXPIRATION_DAYS = 3;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;


    @Resource(name = LIBRARY_DATA_UPLOAD_THREAD_POOL_TASK_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    @Lazy
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibrarySliceMapper materialLibrarySliceMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private DeptPermissionApi deptPermissionApi;

    @Override
    public Long createMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO createReqVO) {

        MaterialLibraryDO libraryInfo = getLibraryInfo(createReqVO.getLibraryId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_edit, Long.valueOf(libraryInfo.getCreator()));

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
    public List<Long> createBatchMaterialLibrarySlice(MaterialLibrarySliceBatchSaveReqVO createReqVO) {
        Optional<Long> libraryId = createReqVO.getSaveReqVOS().stream().map(MaterialLibrarySliceSaveReqVO::getLibraryId).findFirst();

        if (!libraryId.isPresent()) {
            throw exception(MATERIAL_LIBRARY_SLICE_LIBRARY_ID_MISSING);
        }
        MaterialLibraryDO libraryInfo = getLibraryInfo(libraryId.get());
        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_edit, Long.valueOf(libraryInfo.getCreator()));


        if (createReqVO.getSaveReqVOS().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> libraryIds = createReqVO.getSaveReqVOS().stream()
                .map(MaterialLibrarySliceSaveReqVO::getLibraryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryIds.get(0));

        List<MaterialLibrarySliceSaveReqVO> saveReqVOS = createReqVO.getSaveReqVOS();

        saveReqVOS.forEach(saveReq -> {
            if (saveReq.getContent() == null || saveReq.getContent().size() != tableColumnDOList.size()) {
                throw exception(MATERIAL_LIBRARY_SLICE_DATA_MISSING);
            }
        });

        handleImageValue(tableColumnDOList, saveReqVOS, libraryIds.get(0));

        return this.batchSaveDataDesc(createReqVO.getSaveReqVOS());
    }

    public void handleImageValue(List<MaterialLibraryTableColumnDO> tableColumnDOList,
                                 List<MaterialLibrarySliceSaveReqVO> saveReqVOS,
                                 Long libraryId) {

        // 第一步 取出列类型为图片的列 code 如果不存在 直接返回
        Set<String> imageColumnCodes = new HashSet<>();
        for (MaterialLibraryTableColumnDO tableColumnDO : tableColumnDOList) {
            if (ColumnTypeEnum.IMAGE.getCode().equals(tableColumnDO.getColumnType())) {
                String columnCode = tableColumnDO.getColumnCode();
                if (columnCode != null && !columnCode.isEmpty()) {
                    imageColumnCodes.add(columnCode);
                }
            }
        }

        if (imageColumnCodes.isEmpty()) {
            return; // 不存在图片列，直接返回
        }

        // 第二步 如果存图片列 code 则取出所有图片
        List<String> allImageContents = new ArrayList<>();
        for (MaterialLibrarySliceSaveReqVO saveReq : saveReqVOS) {
            List<MaterialLibrarySliceSaveReqVO.TableContent> content = saveReq.getContent();
            if (content == null) {
                throw exception(MATERIAL_LIBRARY_SLICE_DATA_MISSING);
            }

            for (MaterialLibrarySliceSaveReqVO.TableContent tc : content) {
                if (imageColumnCodes.contains(tc.getColumnCode())) {
                    if (StrUtil.isNotBlank(tc.getValue())) {
                        allImageContents.add(tc.getValue());
                    }

                }
            }
        }

        // 异步上传图片
        getSelf().asyncUploadImage(allImageContents, libraryId);

    }

    @Override
    public void updateMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO updateReqVO) {

        MaterialLibraryDO libraryInfo = getLibraryInfo(updateReqVO.getLibraryId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_edit, Long.valueOf(libraryInfo.getCreator()));

        // 校验存在
        MaterialLibrarySliceDO sliceDO = validateMaterialLibrarySliceExists(updateReqVO.getId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_edit, Long.valueOf(sliceDO.getCreator()));

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

        Optional<Long> libraryId = updateReqVO.getSaveReqVOS().stream().map(MaterialLibrarySliceSaveReqVO::getLibraryId).findFirst();

        if (!libraryId.isPresent()) {
            throw exception(MATERIAL_LIBRARY_SLICE_LIBRARY_ID_MISSING);
        }
        MaterialLibraryDO libraryInfo = getLibraryInfo(libraryId.get());
        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_edit, Long.valueOf(libraryInfo.getCreator()));


        materialLibrarySliceMapper.updateBatch(BeanUtils.toBean(updateReqVO.getSaveReqVOS(), MaterialLibrarySliceDO.class));
    }

    @Override
    public void deleteMaterialLibrarySlice(Long id) {
        // 校验存在
        MaterialLibrarySliceDO sliceDO = validateMaterialLibrarySliceExists(id);

        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_delete, Long.valueOf(sliceDO.getCreator()));

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
        // 校验存在
        MaterialLibraryDO libraryInfo = getLibraryInfo(libraryId);

        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_delete, Long.valueOf(libraryInfo.getCreator()));

        materialLibrarySliceMapper.deleteSliceByLibraryId(libraryId);
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
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery(MaterialLibrarySliceDO.class);

        wrapper.in(MaterialLibrarySliceDO::getId, ids);
        List<MaterialLibrarySliceDO> sliceDOList = materialLibrarySliceMapper.selectList(wrapper);
        List<Long> creators = sliceDOList.stream().map(MaterialLibrarySliceDO::getCreator).map(Long::valueOf).distinct().collect(Collectors.toList());

        deptPermissionApi.checkPermission(DeptPermissionEnum.material_library_slice_delete, creators);

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
        log.info("查询素材列表：素材库ID：{}，需要查询的数据ID列表: {}, 排除的素材库列表:{}, 排序规则: {}", libraryId, sliceIdList, removeSliceIdList, sortingField);
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
            log.error("当前应用未绑定素材库，{}", appUid);
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
            log.error("当前应用未绑定素材库，{}", appPageReqVO.getAppUid());
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
            sliceData.setDeptId(null);
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
    // @Async
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
        if (CollUtil.isEmpty(sliceDOList)) {
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

    @Override
    @Async
    public void executeAsyncUpload(Map<Integer, List<String>> columnData, File[] childrenDirs, String unzipDirPath, Long libraryId) {
        log.info("=========> 图片和文档数据 异步处理");
        String prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATETIME_MS_PATTERN) + RandomUtil.randomInt(1000, 9999);

        List<String> images = CollUtil.distinct(columnData.get(ColumnTypeEnum.IMAGE.getCode()));

        if (CollUtil.isNotEmpty(images)) {
            log.info("=========> 图片数量为{}", images.size());

            images.forEach(imageName -> {
                if (StringUtil.isPath(imageName)) {
                    List<File> filesInImagesFolder = findFilesInTargetFolder(childrenDirs, "images", imageName);

                    if (CollUtil.isEmpty(filesInImagesFolder)) {
                        setRedisValue(getRedisKey(imageName, libraryId), JSONUtil.toJsonStr(MATERIAL_LIBRARY_FILE_NO_FOUND));
                        return;
                    }

                    threadPoolTaskExecutor.execute(() -> {
                        File image = FileUtil.file(filesInImagesFolder.get(0));

                        try (InputStream is = Files.newInputStream(image.toPath())) {
                            byte[] bytes = IoUtil.readBytes(is);
                            String url = ImageUploadUtils.uploadImage(StrUtil.format("{}_{}", prefix, imageName), ImageUploadUtils.UPLOAD_PATH, bytes).getUrl();
                            setRedisValue(getRedisKey(imageName, libraryId), JSONUtil.toJsonStr(url));
                        } catch (IOException e) {
                            log.error("Failed to upload image: {}", imageName, e);
                            throw new RuntimeException("Failed to upload image: " + imageName, e);
                        }
                    });
                    return;
                }
                if (ImageUploadUtils.isImage(imageName)) {
                    // excel 中有内容 为http图片地址
                    threadPoolTaskExecutor.execute(() -> {
                        String relativePath = "material" + File.separator + prefix;
                        String ossUrl = ImageUploadUtils.dumpToOss(imageName, IdUtil.fastSimpleUUID(), relativePath);
                        setRedisValue(getRedisKey(imageName, libraryId), JSONUtil.toJsonStr(ossUrl));
                    });
                    return;
                }
                setRedisValue(getRedisKey(imageName, libraryId), JSONUtil.toJsonStr(MATERIAL_LIBRARY_FILE_TYPE_ERROR));


            });

        }


        List<String> documentNames = CollUtil.distinct(columnData.get(ColumnTypeEnum.DOCUMENT.getCode()));
        if (CollUtil.isNotEmpty(documentNames)) {
            log.info("=========> 文档数量为{}", images.size());
            documentNames.forEach(documentName -> {
                threadPoolTaskExecutor.execute(() -> {
                    List<String> urls = documentScreenshot(IdUtil.fastSimpleUUID(), documentName, unzipDirPath);
                    redisTemplate.boundValueOps(getRedisKey(documentName, libraryId)).set(String.join(",", urls), 3, TimeUnit.DAYS);

                });

            });

        }
        log.info("=========> 图片和文档数据 异步处理中");

    }

    /**
     * 批量保存数据-倒序
     * 内部会将list倒序
     *
     * @param saveReqVOS saveReqVOS
     */
    @Override
    public List<Long> batchSaveDataDesc(List<MaterialLibrarySliceSaveReqVO> saveReqVOS) {


        Long libraryId = saveReqVOS.stream().map(MaterialLibrarySliceSaveReqVO::getLibraryId).findFirst().orElse(null);
        if (libraryId == null) {
            throw exception(MATERIAL_LIBRARY_IMPORT_LIBRARY_EMPTY);
        }

        // 将数据倒序
        List<MaterialLibrarySliceSaveReqVO> descSaveReqVOS = IntStream.range(0, saveReqVOS.size())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .map(saveReqVOS::get)
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(descSaveReqVOS)) {
            throw exception(MATERIAL_LIBRARY_IMPORT_DATA_EMPTY);
        }

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryId);
        Map<Integer, List<String>> fileColumn = getImageOrDocumentColumn(tableColumnDOList);

        // 如果数据列中存在表格或者其他文件 则从缓存中获取数据
        if (MapUtil.isNotEmpty(fileColumn)) {
            // 获取列中的图片或者文档数据 key
            List<String> otherFileKeys = getOtherFileKeys(descSaveReqVOS, fileColumn);
            // 校验上传是否成功
            validateUploadIsSuccess(buildRedisKey(otherFileKeys, libraryId));
            // 填充图片或者文档数据
            fillFileData(descSaveReqVOS, fileColumn, libraryId);
        }
        List<MaterialLibrarySliceDO> bean = BeanUtil.copyToList(descSaveReqVOS, MaterialLibrarySliceDO.class);

        materialLibrarySliceMapper.insertBatch(bean);

        return bean.stream().map(MaterialLibrarySliceDO::getId).sorted(Comparator.reverseOrder()).collect(Collectors.toList());



    }

    /**
     * 复制数据
     *
     * @param sliceId 数据编号
     */
    @Override
    public Boolean copy(Long sliceId) {

        MaterialLibrarySliceDO sliceDO = materialLibrarySliceMapper.selectById(sliceId);
        sliceDO.setId(null);
        sliceDO.setUsedCount(0L);
        this.createMaterialLibrarySlice(BeanUtil.toBean(sliceDO,MaterialLibrarySliceSaveReqVO.class));
        return true;
    }

    private List<String> getOtherFileKeys(List<MaterialLibrarySliceSaveReqVO> descSaveReqVOS, Map<Integer, List<String>> fileColumn) {
        List<String> allColumnCodes = fileColumn.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return descSaveReqVOS.stream()
                .flatMap(saveReqVO -> saveReqVO.getContent().stream())
                .filter(content -> allColumnCodes.contains(content.getColumnCode()) && StrUtil.isNotBlank(content.getValue()))
                .map(MaterialLibrarySliceSaveReqVO.TableContent::getValue)
                .collect(Collectors.toList());

    }


    private void fillFileData(List<MaterialLibrarySliceSaveReqVO> descSaveReqVOS, Map<Integer, List<String>> fileColumn, Long libraryId) {
        List<String> imageColumn = fileColumn.get(ColumnTypeEnum.IMAGE.getCode());
        List<String> documentColumn = fileColumn.get(ColumnTypeEnum.DOCUMENT.getCode());

        if (CollUtil.isNotEmpty(imageColumn)) {
            descSaveReqVOS.forEach(saveReqVO -> {
                saveReqVO.getContent().stream()
                        .filter(content -> imageColumn.contains(content.getColumnCode()) && StrUtil.isNotBlank(content.getValue()))
                        .forEach(content -> content.setValue(redisTemplate.boundValueOps(getRedisKey(content.getValue(), libraryId)).get()));
            });
        }

        if (CollUtil.isNotEmpty(documentColumn)) {
            descSaveReqVOS.forEach(data -> {
                data.getContent().stream()
                        .filter(content -> documentColumn.contains(content.getColumnCode()) && StrUtil.isNotBlank(content.getValue()))
                        .forEach(content -> {
                            String key = getRedisKey(content.getValue(), libraryId);
                            String urlsString = redisTemplate.boundValueOps(key).get();

                            if (StrUtil.isNotBlank(urlsString)) {
                                List<String> urls = Arrays.asList(urlsString.split(","));
                                if (imageColumn.contains(content.getColumnCode()) && (StrUtil.isBlank(content.getValue()) || StrUtil.NULL.equalsIgnoreCase(content.getValue())) && !urls.isEmpty()) {
                                    content.setValue(urls.get(0));
                                }
                            }
                        });
            });
        }
    }


    @Async
    public void asyncUploadImage(List<String> imageUrls, Long libraryId) {
        String prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATETIME_MS_PATTERN) + RandomUtil.randomInt(1000, 9999);
        imageUrls.forEach(url -> {
            try {
                ImgUtil.read(new URL(url));
                // excel 中有内容 为http图片地址
                threadPoolTaskExecutor.execute(() -> {
                            String relativePath = "material" + File.separator + prefix;
                            String ossUrl = ImageUploadUtils.dumpToOss(url, IdUtil.fastSimpleUUID(), relativePath);
                            setRedisValue(getRedisKey(url, libraryId), JSONUtil.toJsonStr(ossUrl));
                        }
                );
            }catch (Exception e){
                log.error("图片上传失败",e);
                setRedisValue(getRedisKey(url, libraryId), JSONUtil.toJsonStr(MATERIAL_LIBRARY_IMAGE_TYPE_ERROR));
            }
        });

    }


    // 封装 Redis 操作
    private void setRedisValue(String key, String value) {
        redisTemplate.boundValueOps(key).set(value, EXPIRATION_DAYS, TIME_UNIT);
    }

    private List<String> documentScreenshot(String parseUid, String documentPath, String unzipDir) {
        HashMap<String, Object> paramMap = new HashMap<>();
        File document = Paths.get(unzipDir, documentPath).toFile();
        if (!document.exists()) {
            return Collections.emptyList();
        }
        paramMap.put("file", document);
        paramMap.put("parseUid", parseUid);
        String result = HttpUtil.post(getUrl(), paramMap, 1_0000);
        List<String> documentScreenshot = JSONUtil.parseArray(result).toList(String.class);
        if (documentScreenshot == null) {
            documentScreenshot = Collections.emptyList();
        }
        return documentScreenshot;
    }

    /**
     * 查找特定文件夹下的特定文件
     *
     * @param directoriesToSearch 文件列表
     * @param targetFolderName    目标文件夹名称
     * @param targetedFileName    目标文件名称
     * @return 查询到的文件
     */
    public List<File> findFilesInTargetFolder(File[] directoriesToSearch, String targetFolderName, String targetedFileName) {
        List<File> foundFiles = new ArrayList<>();
        for (File directory : directoriesToSearch) {
            if (directory.isDirectory()) {
                // 遍历目录下的所有文件和子目录
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory() && file.getName().equals(targetFolderName)) {
                            // 进入目标文件夹后，再次遍历查找指定文件
                            File[] targetFiles = file.listFiles(pathname -> pathname.getName().equals(targetedFileName));
                            if (targetFiles != null) {
                                for (File targetFile : targetFiles) {
                                    if (targetFile.isFile()) {
                                        foundFiles.add(targetFile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return foundFiles;
    }

    private void validateUploadIsSuccess(List<String> otherFileKeys) {

        ArrayList<String> distinct = CollUtil.distinct(otherFileKeys);
        final int MAX_RETRIES = 30;
        final long RETRY_DELAY_SECONDS = 3L;
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
                log.error("Thread was interrupted, Failed to complete operation:({})", e.getMessage());
                return;
            } catch (Exception e) {
                // 处理其他可能的异常
                log.error("An error occurred while checking file status:({})", e.getMessage());
                return;
            }
        }
        // 超过最大重试次数后，直接跳过，不抛出异常
        log.error("图片上传超过最大重试时间，上传异常");

    }

    private String getUrl() {
        DictDataService bean = SpringUtil.getBean(DictDataService.class);
        DictDataDO dictData = bean.parseDictData("playwright", "material_parse");
        return dictData.getValue();
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
    private MaterialLibrarySliceDO validateMaterialLibrarySliceExists(Long id) {
        MaterialLibrarySliceDO sliceDO = materialLibrarySliceMapper.selectById(id);
        if (sliceDO == null) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
        return sliceDO;
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


    private MaterialLibraryDO getLibraryInfo(Long libraryId) {
        MaterialLibraryDO materialLibraryDO = materialLibraryService.getMaterialLibrary(libraryId);
        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        return materialLibraryDO;
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private MaterialLibrarySliceServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}