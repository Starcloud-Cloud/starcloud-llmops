package com.starcloud.ops.business.job.biz.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceBatchSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginExecuteReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import com.starcloud.ops.business.job.biz.processor.dto.ProcessResultDTO;
import com.starcloud.ops.business.job.biz.processor.dto.TaskContextDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.*;

@Slf4j
@Component
public class CozeStandaloneProcessor extends StandaloneBasicProcessor {

    @Resource
    private PluginsService pluginsService;

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryTableColumnService tableColumnService;

    @Override
    ProcessResultDTO actualProcess(TaskContextDTO taskContextDTO) {
        BusinessJobDO businessJobDO = taskContextDTO.getBusinessJobDO();

        String config = businessJobDO.getConfig();
        if (StringUtils.isBlank(config)) {
            throw exception(JOB_CONFIG_ERROR, businessJobDO.getId(), "任务配置为空");
        }

        PluginDetailVO pluginConfigVO = JSONUtil.toBean(config, PluginDetailVO.class);
        String pluginUid = pluginConfigVO.getPluginUid();
        String fieldMapJson = pluginConfigVO.getFieldMap();
        if (!JSONUtil.isTypeJSONObject(fieldMapJson)) {
            throw exception(JOB_CONFIG_ERROR, businessJobDO.getId(), "字段映射：" + fieldMapJson);
        }

        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
        };
        Map<String, String> fieldMap = JSON.parseObject(fieldMapJson, typeReference.getType());
        if (CollectionUtil.isEmpty(fieldMap)) {
            throw exception(JOB_CONFIG_ERROR, businessJobDO.getId(), "字段映射未配置");
        }

        String executeParams = pluginConfigVO.getExecuteParams();
        PluginExecuteReqVO executeReqVO = new PluginExecuteReqVO();
        executeReqVO.setUuid(pluginUid);
        executeReqVO.setInputParams(executeParams);
        Object content = pluginsService.syncExecute(executeReqVO);

        // 字段映射落库
        String libraryUid = pluginConfigVO.getLibraryUid();
        savaLibrary(libraryUid, content, fieldMap, taskContextDTO.getOmsLogger());
        return new ProcessResultDTO(true, content);
    }

    /**
     * 保存素材
     *
     * @param omsLogger powerjob线上日志
     */
    private void savaLibrary(String libraryUid, Object content, Map<String, String> fieldMap, OmsLogger omsLogger) {
        MaterialLibraryRespVO library = materialLibraryService.getMaterialLibraryByUid(libraryUid);

        List<MaterialLibraryTableColumnDO> tableColumnList = tableColumnService.getMaterialLibraryTableColumnByLibrary(library.getId());
        if (CollectionUtils.isEmpty(tableColumnList)) {
            throw exception(LIBRARY_COLUMN_ERROR, library.getId());
        }

        MaterialLibrarySliceBatchSaveReqVO createReqVO = new MaterialLibrarySliceBatchSaveReqVO();
        List<MaterialLibrarySliceSaveReqVO> saveReqVOS = new ArrayList<>();

        if (content instanceof Map) {
            Map<String, Object> objectMap = (Map<String, Object>) content;
            if (CollectionUtil.isEmpty(objectMap)) {
                throw exception(COZE_RESULT_ERROR, JSONUtil.toJsonPrettyStr(objectMap));
            }
            MaterialLibrarySliceSaveReqVO saveReqVO = buildSlice(library, objectMap, fieldMap, tableColumnList);
            saveReqVOS.add(saveReqVO);
        } else if (content instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) content;
            for (Map<String, Object> map : list) {
                if (CollectionUtil.isEmpty(map)) {
                    continue;
                }
                MaterialLibrarySliceSaveReqVO saveReqVO = buildSlice(library, map, fieldMap, tableColumnList);
                saveReqVOS.add(saveReqVO);
            }
        } else {
            throw exception(COZE_RESULT_ERROR, JSONUtil.toJsonPrettyStr(content));
        }

        if (CollectionUtils.isEmpty(saveReqVOS)) {
            logInfo(omsLogger, "未匹配到素材库字段，libraryId={} \n content={} \n fieldMap={}",
                    library.getId(), JSONUtil.toJsonPrettyStr(content), JSONUtil.toJsonPrettyStr(fieldMap));
            return;
        }
        createReqVO.setSaveReqVOS(saveReqVOS);

        materialLibrarySliceService.createBatchMaterialLibrarySlice(createReqVO);
    }


    /**
     * 构造素材库数据
     */
    private MaterialLibrarySliceSaveReqVO buildSlice(MaterialLibraryRespVO library,
                                                     Map<String, Object> content,
                                                     Map<String, String> fieldMap,
                                                     List<MaterialLibraryTableColumnDO> tableColumnList) {
        MaterialLibrarySliceSaveReqVO saveReqVO = new MaterialLibrarySliceSaveReqVO();
        saveReqVO.setLibraryId(library.getId());
        List<MaterialLibrarySliceSaveReqVO.TableContent> tableContents = new ArrayList<>(tableColumnList.size());
        Map<String, String> swap = MapUtils.swap(fieldMap, (a, b) -> a);
        for (MaterialLibraryTableColumnDO materialLibraryTableColumnDO : tableColumnList) {
            MaterialLibrarySliceSaveReqVO.TableContent tableContent = new MaterialLibrarySliceSaveReqVO.TableContent();
            tableContent.setColumnCode(materialLibraryTableColumnDO.getColumnCode());
            String contentKey = swap.get(materialLibraryTableColumnDO.getColumnCode());
            String value = content.get(contentKey) == null ? null : content.get(contentKey).toString();
            tableContent.setValue(value);
            tableContents.add(tableContent);
        }
        saveReqVO.setContent(tableContents);
        return saveReqVO;
    }

}
