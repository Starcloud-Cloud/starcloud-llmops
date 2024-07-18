package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CreativeMaterialManager {

    @Resource
    private MaterialLibraryService materialLibraryService;

    /**
     * 查询表头 分组配置
     *
     * @param materialLibraryJsonVariable 变量 LIBRARY_QUERY 配置
     */
    public List<MaterialFieldConfigDTO> getHeader(String materialLibraryJsonVariable) {

        if (StringUtils.isBlank(materialLibraryJsonVariable)) {
            return Collections.emptyList();
        }
        List<MaterialLibrarySliceAppReqVO> queryParam = JSONUtil.parseArray(materialLibraryJsonVariable).toList(MaterialLibrarySliceAppReqVO.class);
        if (CollectionUtil.isEmpty(queryParam)) {
            return Collections.emptyList();
        }
        MaterialLibraryRespVO materialLibraryByUid = materialLibraryService.getMaterialLibraryByUid(queryParam.get(0).getLibraryUid());
        if (Objects.isNull(materialLibraryByUid) || CollectionUtil.isEmpty(materialLibraryByUid.getTableMeta())) {
            return Collections.emptyList();
        }
        List<MaterialFieldConfigDTO> result = new ArrayList<>();
        for (MaterialLibraryTableColumnRespVO tableColumnRespVO : materialLibraryByUid.getTableMeta()) {
            MaterialFieldConfigDTO fieldConfigDTO = new MaterialFieldConfigDTO();
            fieldConfigDTO.setFieldName(tableColumnRespVO.getColumnCode());
            fieldConfigDTO.setDesc(tableColumnRespVO.getColumnName());
            fieldConfigDTO.setIsGroupField(tableColumnRespVO.getIsGroupColumn());
            result.add(fieldConfigDTO);
        }
        return result;
    }

    /**
     * 判断素材内容显示类型 true显示图片 false显示列表
     */
    public Boolean judgePicture(AppMarketRespVO appRespVO) {
        WorkflowStepWrapperRespVO materialWrapper = appRespVO.getStepByHandler(MaterialActionHandler.class.getSimpleName());
        if (Objects.isNull(materialWrapper)) {
            return false;
        }
        String materialLibraryJsonVariable = materialWrapper.getVariableToString(CreativeConstants.LIBRARY_QUERY);
        if (StringUtils.isBlank(materialLibraryJsonVariable)) {
            return false;
        }
        List<MaterialLibrarySliceAppReqVO> queryParam = JSONUtil.parseArray(materialLibraryJsonVariable).toList(MaterialLibrarySliceAppReqVO.class);
        if (CollectionUtil.isEmpty(queryParam)) {
            return false;
        }
        MaterialLibraryRespVO materialLibraryByUid = materialLibraryService.getMaterialLibraryByUid(queryParam.get(0).getLibraryUid());
        return !Objects.isNull(materialLibraryByUid)
                && !CollectionUtil.isEmpty(materialLibraryByUid.getTableMeta())
                && materialLibraryByUid.getTableMeta().size() == 1
                && ColumnTypeEnum.IMAGE.getCode().equals(materialLibraryByUid.getTableMeta().get(0).getColumnType());
    }


    /**
     * 新建空素材库
     *
     * @param appName
     * @return 素材库查询json
     */
    public String createEmptyLibrary(String appName) {
        long start = System.currentTimeMillis();
        String libraryUid = materialLibraryService.createMaterialLibraryByApp(appName);
        long end = System.currentTimeMillis();
        log.info("create empty material library {}", end - start);
        MaterialLibrarySliceAppReqVO materialLibrarySliceAppReqVO = new MaterialLibrarySliceAppReqVO();
        materialLibrarySliceAppReqVO.setLibraryUid(libraryUid);
        return JsonUtils.toJsonString(Collections.singletonList(materialLibrarySliceAppReqVO));
    }

    /**
     * 迁移旧素材数据
     *
     * @param materialHandler 素材步骤
     * @param materialList    旧素材
     */
    public void migrate(String appName, WorkflowStepWrapperRespVO materialHandler, List<Map<String, Object>> materialList) {
        log.info("start migrate material, appName={}", appName);
        String materialDefine = materialHandler.getVariableToString(CreativeConstants.MATERIAL_DEFINE);
        if (StringUtils.isBlank(materialDefine)) {
            return;
        }
        List<MaterialFieldConfigDTO> materialFieldConfigDTOList = MaterialDefineUtil.parseConfig(materialDefine);
        if (CollectionUtil.isEmpty(materialFieldConfigDTOList)) {
            return;
        }

        List<MaterialLibraryTableColumnSaveReqVO> tableColumnConfigList = new ArrayList<>(materialFieldConfigDTOList.size());

        for (MaterialFieldConfigDTO materialFieldConfigDTO : materialFieldConfigDTOList) {
            MaterialLibraryTableColumnSaveReqVO saveReqVO = convertMaterialLibraryTableColumn(materialFieldConfigDTO);
            tableColumnConfigList.add(saveReqVO);
        }

        long start = System.currentTimeMillis();
        // TODO 接口参数改了
        String libraryUid = null;
        // materialLibraryService.materialLibraryDataMigration(appName, tableColumnConfigList, materialList);
        long end = System.currentTimeMillis();
        log.info("material library migrate, {}", end - start);
        MaterialLibrarySliceAppReqVO librarySlice = new MaterialLibrarySliceAppReqVO();
        librarySlice.setLibraryUid(libraryUid);
        materialHandler.putVariable(CreativeConstants.LIBRARY_QUERY, JsonUtils.toJsonString(Collections.singletonList(librarySlice)));
    }

    /**
     * 就表头结构转成新表头结构
     */
    private static MaterialLibraryTableColumnSaveReqVO convertMaterialLibraryTableColumn(MaterialFieldConfigDTO materialFieldConfigDTO) {
        MaterialLibraryTableColumnSaveReqVO saveReqVO = new MaterialLibraryTableColumnSaveReqVO();
        saveReqVO.setColumnCode(materialFieldConfigDTO.getFieldName());
        saveReqVO.setColumnName(materialFieldConfigDTO.getDesc());
        String fieldConfigType = materialFieldConfigDTO.getType();
        if (MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.IMAGE.getCode());
        } else if (MaterialFieldTypeEnum.string.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        } else if (MaterialFieldTypeEnum.textBox.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        } else if (MaterialFieldTypeEnum.document.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.DOCUMENT.getCode());
        } else {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        }
        saveReqVO.setIsRequired(materialFieldConfigDTO.isRequired());
        saveReqVO.setSequence((long) materialFieldConfigDTO.getOrder());
        saveReqVO.setIsGroupColumn(materialFieldConfigDTO.getIsGroupField());
        return saveReqVO;
    }

    /**
     * 复制应用市场中的素材库 绑定新的素材库
     */
    public void upgradeMaterialLibrary(AppMarketRespVO latestAppMarket) {
        WorkflowStepWrapperRespVO materialWrapper = latestAppMarket.getStepByHandler(MaterialActionHandler.class.getSimpleName());
        if (Objects.isNull(materialWrapper)) {
            return;
        }
        String materialLibraryJsonVariable = materialWrapper.getVariableToString(CreativeConstants.LIBRARY_QUERY);
        List<MaterialLibrarySliceAppReqVO> queryParam = copyLibraray(materialLibraryJsonVariable);
        materialWrapper.putVariable(CreativeConstants.LIBRARY_QUERY, JsonUtils.toJsonString(queryParam));
    }

    /**
     * 复制我的应用中的素材库 绑定新的素材库
     */
    public void upgradeMaterialLibrary(AppDO app) {
        WorkflowConfigEntity workflowConfigEntity = JsonUtils.parseObject(app.getConfig(), WorkflowConfigEntity.class);
        if (Objects.isNull(workflowConfigEntity)) {
            return;
        }

        String materialLibraryJsonVariable = Optional.ofNullable(workflowConfigEntity.getStepWrapperWithoutError(MaterialActionHandler.class))
                .map(workflowStepWrapper -> workflowStepWrapper.getVariablesValue(CreativeConstants.LIBRARY_QUERY))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);

        if (StringUtils.isBlank(materialLibraryJsonVariable)) {
            return;
        }

        List<MaterialLibrarySliceAppReqVO> queryParam = copyLibraray(materialLibraryJsonVariable);
        workflowConfigEntity.putVariable(MaterialActionHandler.class, CreativeConstants.LIBRARY_QUERY, JsonUtils.toJsonString(queryParam));
        app.setConfig(JsonUtils.toJsonString(workflowConfigEntity));
    }

    /**
     * 根据素材库配置查询素材列表
     */
    public List<Map<String, Object>> getMaterialList(AppMarketRespVO appMarketVO) {

        WorkflowStepWrapperRespVO materialStepWrapper = CreativeUtils.getMaterialStepWrapper(appMarketVO);
        // 查询素材库数据
        String materialLibraryJsonVariable = Optional.ofNullable(materialStepWrapper)
                .map(workflowStepWrapperRespVO -> workflowStepWrapperRespVO.getVariableToString(CreativeConstants.LIBRARY_QUERY))
                .orElse(StringUtils.EMPTY);

        if (StringUtils.isBlank(materialLibraryJsonVariable)) {
            throw ServiceExceptionUtil.invalidParamException("执行失败！获取素材列表失败：查询条件为空！");
        }

        // 获取查询条件
        List<MaterialLibrarySliceAppReqVO> queryParam = JsonUtils.parseArray(materialLibraryJsonVariable, MaterialLibrarySliceAppReqVO.class);
        if (CollectionUtil.isEmpty(queryParam)) {
            throw ServiceExceptionUtil.invalidParamException("执行失败！获取素材列表失败：查询条件为空！");
        }

        // 获取到素材使用模式
        MaterialUsageModel materialUsageModel = CreativeUtils.getMaterialUsageModelByStepWrapper(materialStepWrapper);

        List<MaterialLibrarySliceUseRespVO> materialLibrarySliceList;
        // 使用模式为过滤使用，按照使用次数升序排除
        if (MaterialUsageModel.FILTER_USAGE.equals(materialUsageModel)) {
            queryParam = queryParam.stream()
                    .peek(item -> {
                        SortingField sortingField = new SortingField();
                        sortingField.setOrder(SortingField.ORDER_ASC);
                        sortingField.setField(MaterialLibrarySliceAppReqVO.SORT_FIELD_USED_COUNT);
                        item.setSortingField(sortingField);
                    }).collect(Collectors.toList());
        }
        // 查询素材库数据
        materialLibrarySliceList = queryLibrary(queryParam);
        if (CollectionUtil.isEmpty(materialLibrarySliceList)) {
            throw ServiceExceptionUtil.invalidParamException("执行失败！获取素材列表失败：素材列表数据为空！");
        }

        return convert(materialLibrarySliceList);
    }

    /**
     * 复制素材库
     */
    private List<MaterialLibrarySliceAppReqVO> copyLibraray(String materialLibraryJsonVariable) {
        log.info("start material library copy,params={}", materialLibraryJsonVariable);
        long start = System.currentTimeMillis();
        List<MaterialLibrarySliceAppReqVO> request = JSONUtil.parseArray(materialLibraryJsonVariable).toList(MaterialLibrarySliceAppReqVO.class);
        List<String> libraryUids = materialLibraryService.materialLibraryCopy(request);
        long end = System.currentTimeMillis();
        log.info("material library copy, {}", end - start);
        List<MaterialLibrarySliceAppReqVO> queryParam = new ArrayList<>();
        for (String libraryUid : libraryUids) {
            MaterialLibrarySliceAppReqVO librarySlice = new MaterialLibrarySliceAppReqVO();
            librarySlice.setLibraryUid(libraryUid);
            queryParam.add(librarySlice);
        }
        return queryParam;
    }

    /**
     * 查询素材库数据
     * 暂时只取第一个素材库数据 jsonschema只生成第一个素材库表头
     */
    private List<MaterialLibrarySliceUseRespVO> queryLibrary(List<MaterialLibrarySliceAppReqVO> queryParam) {
        log.info("start material library query, params={}", JsonUtils.toJsonString(queryParam));
        if (CollectionUtil.isEmpty(queryParam)) {
            return Collections.emptyList();
        }
        long start = System.currentTimeMillis();
        List<MaterialLibrarySliceUseRespVO> materialLibrarySliceList = materialLibraryService.getMaterialLibrarySliceList(Collections.singletonList(queryParam.get(0)));
        long end = System.currentTimeMillis();
        log.info("material library query, {}", end - start);
        return materialLibrarySliceList;
    }

    /**
     * 素材库数据转成 Map
     */
    private List<Map<String, Object>> convert(List<MaterialLibrarySliceUseRespVO> librarySliceList) {
        if (CollectionUtil.isEmpty(librarySliceList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (MaterialLibrarySliceUseRespVO librarySlice : librarySliceList) {
            List<MaterialLibrarySliceRespVO> sliceRespList = librarySlice.getSliceRespVOS();
            List<MaterialLibraryTableColumnRespVO> tableMeta = librarySlice.getTableMeta();
            Map<String, Integer> columnCodeType = tableMeta.stream().collect(Collectors.toMap(MaterialLibraryTableColumnRespVO::getColumnCode, MaterialLibraryTableColumnRespVO::getColumnType, (a, b) -> a));


            if (CollectionUtil.isEmpty(librarySliceList)) {
                continue;
            }
            for (MaterialLibrarySliceRespVO sliceRespVO : sliceRespList) {
                List<MaterialLibrarySliceRespVO.TableContent> tableContentList = sliceRespVO.getContent();
                if (CollectionUtil.isEmpty(tableContentList)) {
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                for (MaterialLibrarySliceRespVO.TableContent tableContent : tableContentList) {
                    if (Objects.isNull(tableContent)) {
                        continue;
                    }

                    row.put("__id__", sliceRespVO.getId());
                    row.put("__usageCount__", sliceRespVO.getUsedCount());
                    row.put(tableContent.getColumnCode(), tableContent.getValue());
                    Integer typeCode = columnCodeType.get(tableContent.getColumnCode());
                    String extend = tableContent.getExtend();

                    if (ColumnTypeEnum.IMAGE.getCode().equals(typeCode) && StringUtils.isNotBlank(extend)) {
                        Map<String, Object> map = JSONObject.parseObject(extend, new TypeReference<Map<String, Object>>() {
                        });
                        map.put("content", tableContent.getDescription());
                        if (CollectionUtil.isNotEmpty(tableContent.getTags())) {
                            map.put("tag", tableContent.getTags().stream().collect(Collectors.joining(",")));
                        }
                        row.put(tableContent.getColumnCode() + "_ext", map);
                    }
                }
                result.add(row);
            }
        }
        return result;
    }
}
