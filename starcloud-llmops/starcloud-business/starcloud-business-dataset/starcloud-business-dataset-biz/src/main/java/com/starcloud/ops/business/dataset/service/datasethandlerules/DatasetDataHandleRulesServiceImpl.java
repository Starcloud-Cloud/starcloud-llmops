package com.starcloud.ops.business.dataset.service.datasethandlerules;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.*;
import com.starcloud.ops.business.dataset.convert.datasethandlerules.DatasetHandleRulesConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasethandlerules.DatasetHandleRulesMapper;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.enums.HandleRuleFromSceneEnum;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;

/**
 * 数据集规则实现类
 *
 * @author Alan Cusack
 */

@Slf4j
@Service
public class DatasetDataHandleRulesServiceImpl implements DatasetDataHandleRulesService {

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetStorageService datasetStorageService;

    @Resource
    private DatasetHandleRulesMapper handleRulesMapper;
    private static final String CLEAN_PREFIX = "CLEAN";

    /**
     * 获得规则分页
     *
     * @param pageReqVO 分页查询
     * @return 规则分页
     */
    @Override
    public PageResult<DatasetHandleRulesRespVO> getRulePage(DatasetHandleRulesPageReqVO pageReqVO) {

        // 获取数据集信息
        DatasetsDO datasets = datasetsService.getDatasetInfoByAppId(pageReqVO.getAppId());

        PageResult<DatasetHandleRulesDO> datasetHandleRulesDOPageResult = handleRulesMapper.selectPage(pageReqVO, datasets.getId());

        // 数据转换
        return DatasetHandleRulesConvert.INSTANCE.convertPage(datasetHandleRulesDOPageResult);
    }

    /**
     * 创建规则
     *
     * @param createReqVO@return 编号
     */
    @Override
    public Boolean createRules(DatasetHandleRulesCreateReqVO createReqVO) {
        // 获取数据集信息
        DatasetsDO datasets = datasetsService.getDatasetInfoByAppId(createReqVO.getAppId());

        // 数据转换
        DatasetHandleRulesDO convert = DatasetHandleRulesConvert.INSTANCE.convert(createReqVO, datasets.getId());

        int result = handleRulesMapper.insert(convert);

        return BooleanUtil.isTrue(1 == result);
    }

    /**
     * 创建规则
     *
     * @param updateReqVO 创建信息
     * @return 编号
     */
    @Override
    public Boolean updateRules(DatasetHandleRulesUpdateReqVO updateReqVO) {

        validateExists(updateReqVO.getId());
        // 获取数据集信息
        DatasetsDO datasets = datasetsService.getDatasetInfoByAppId(updateReqVO.getAppId());

        DatasetHandleRulesDO updateObj = DatasetHandleRulesConvert.INSTANCE.convert(updateReqVO, datasets.getId());
        int result = handleRulesMapper.updateById(updateObj);

        return BooleanUtil.isTrue(1 == result);
    }

    /**
     * 通过 Id 获取规则信息
     *
     * @param id 创建信息
     * @return 编号
     */
    @Override
    public DatasetHandleRulesRespVO getRuleById(Long id) {

        DatasetHandleRulesDO datasetHandleRulesDO = handleRulesMapper.selectOne(
                Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                        .eq(DatasetHandleRulesDO::getId, id)
                        .eq(DatasetHandleRulesDO::getEnable, true));

        if (datasetHandleRulesDO == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
        return DatasetHandleRulesConvert.INSTANCE.convert(datasetHandleRulesDO);

    }

    /**
     * 通过 Id集合 获取多条数据
     *
     * @param Ids
     */
    @Override
    public List<DatasetHandleRulesDO> getRuleByIds(List<Long> Ids) {
        return handleRulesMapper.selectList(
                Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                        .in(DatasetHandleRulesDO::getId, Ids));
    }

    /**
     * 通过 Id 获取规则信息
     *
     * @param datasetId 创建信息
     * @return 编号
     */
    @Override
    public DatasetHandleRulesRespVO getRuleByDatasetId(Long datasetId) {

        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .eq(DatasetHandleRulesDO::getDatasetId, datasetId)
                .last("limit 1");
        DatasetHandleRulesDO handleRulesDO = handleRulesMapper.selectOne(wrapper);
        return DatasetHandleRulesConvert.INSTANCE.convert(handleRulesDO);

    }


    /**
     * 通过 Id 获取规则信息
     *
     * @param debugReqVO 创建信息
     * @return 编号
     */
    @Override
    public DatasetHandleRulesDebugRespVO debugRule(DatasetHandleRulesDebugReqVO debugReqVO) {
        DatasetHandleRulesDebugRespVO datasetHandleRulesDebugRespVO = new DatasetHandleRulesDebugRespVO();
        DatasetsDO datasets = datasetsService.getDatasetInfoByAppId(debugReqVO.getAppId());

        String filterData = debugReqVO.getUrl();
        if (!DataSourceDataTypeEnum.HTML.name().equals(debugReqVO.getDataType())) {
            filterData = debugReqVO.getTitle();
        }

        DatasetHandleRulesDO rulesDO = this.getFilteredRule(datasets.getId(), debugReqVO.getDataType(), filterData, null);


        CleanRuleVO cleanRuleVO = JSONUtil.toBean(rulesDO.getCleanRule(), CleanRuleVO.class);

        String cleanData;
        if (DataSourceDataTypeEnum.HTML.name().equals(debugReqVO.getDataType())) {
            String data = TextCleanAndSplitUtils.processHtmlRule(debugReqVO.getUrl(), cleanRuleVO.getHtmlCleanRule());
            cleanData = TextCleanAndSplitUtils.processCommonRule(data, cleanRuleVO.getCommonCleanRule());
        } else if (DataSourceDataTypeEnum.CHARACTERS.name().equals(debugReqVO.getDataType())) {
            cleanData = TextCleanAndSplitUtils.processCommonRule(debugReqVO.getContext(), cleanRuleVO.getCommonCleanRule());
        } else {
            throw exception(DATASET_HANDLE_RULE_TYPE_UNKNOWN);
        }
        datasetHandleRulesDebugRespVO.setRuleName(rulesDO.getRuleName());
        datasetHandleRulesDebugRespVO.setData(cleanData);
        return datasetHandleRulesDebugRespVO;
    }

    /**
     * 删除规则
     *
     * @param ruleId
     * @return
     */
    @Override
    public Boolean deleteRule(Long ruleId) {
        // 验证数据是否存在
        DatasetHandleRulesDO datasetHandleRulesDO = handleRulesMapper.selectById(ruleId);
        if (datasetHandleRulesDO == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
        // 删除数据
        int result = handleRulesMapper.deleteById(ruleId);
        // 返回结果
        return BooleanUtil.isTrue(1 == result);
    }

    /**
     * @return
     */
    @Override
    public List<HandleRuleTypeRespVO> getRuleType() {
        DataSourceDataTypeEnum[] values = DataSourceDataTypeEnum.values();
        List<HandleRuleTypeRespVO> handleRuleTypeRespVOS = new ArrayList<>();
        Arrays.stream(values).forEach(data -> {
            HandleRuleTypeRespVO handleRuleTypeRespVO = new HandleRuleTypeRespVO();
            handleRuleTypeRespVO.setType(data.name());
            handleRuleTypeRespVO.setTypeName(data.getName());
            handleRuleTypeRespVOS.add(handleRuleTypeRespVO);
        });
        return handleRuleTypeRespVOS;
    }

    /**
     * @return
     */
    @Override
    public List<HandleRuleTypeRespVO> getFormatType() {
        DataSourceDataFormatEnum[] values = DataSourceDataFormatEnum.values();
        List<HandleRuleTypeRespVO> handleRuleTypeRespVOS = new ArrayList<>();
        Arrays.stream(values).forEach(data -> {
            HandleRuleTypeRespVO handleRuleTypeRespVO = new HandleRuleTypeRespVO();
            handleRuleTypeRespVO.setType(data.name());
            handleRuleTypeRespVO.setTypeName(data.getName());
            handleRuleTypeRespVOS.add(handleRuleTypeRespVO);
        });
        return handleRuleTypeRespVOS;
    }

    /**
     * 执行数据清洗
     * 1.根据数据类型判断，获取需要清洗的数据
     * 2.根据数据获取清洗规则
     * 3.执行清洗流程，
     * 4.返回清洗数据
     *
     * @return 返回清洗后的数据
     */
    @Override
    public HandleRuleProcessResultRespVO processDataClean(DatasetSourceDataDO sourceDataDO) {
        HandleRuleProcessResultRespVO resultRespVO = new HandleRuleProcessResultRespVO();

        String ruleData = sourceDataDO.getName();
        if (DataSourceDataTypeEnum.HTML.name().equals(sourceDataDO.getDataType())) {
            DataSourceInfoDTO dataSourceInfoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceInfoDTO.class);
            ruleData = dataSourceInfoDTO.getInitAddress();
        }

        // 获取符合的规则
        DatasetHandleRulesDO rulesDO = getFilteredRule(sourceDataDO.getDatasetId(), sourceDataDO.getDataType(), ruleData, null);

        // 根据储存ID 获取存储地址
        DatasetStorageDO storageDO = datasetStorageService.selectDataById(sourceDataDO.getStorageId());
        if (storageDO == null) {
            log.error("清洗过程中，获取数据源失败，数据上传数据为空");
            throw exception(DATASET_SOURCE_DATA_NOT_EXISTS);
        }

        String cleanData = storageDO.getStorageKey();
        if (DataSourceDataTypeEnum.HTML.name().equals(sourceDataDO.getDataType())) {
            DataSourceInfoDTO dataSourceInfoDTO = JSONObject.parseObject(sourceDataDO.getDataSourceInfo(), DataSourceInfoDTO.class);
            cleanData = dataSourceInfoDTO.getInitAddress();
        }

        String cleanResult = processCleanRule(rulesDO, cleanData);
        resultRespVO.setRuleId(rulesDO.getId());
        resultRespVO.setSplitRule(JSONUtil.toBean(rulesDO.getSplitRule(), SplitRule.class));
        resultRespVO.setResult(cleanResult);

        DataSourceDataFormatEnum dataSourceDataFormatEnum = DataSourceDataFormatEnum.TXT;
        if (DataSourceDataTypeEnum.HTML.name().equals(sourceDataDO.getDataType())) {
            dataSourceDataFormatEnum = DataSourceDataFormatEnum.valueOf(JSONUtil.toBean(rulesDO.getCleanRule(), CleanRuleVO.class).getHtmlCleanRule().getConvertFormat());
        }

        resultRespVO.setConvertFormat(dataSourceDataFormatEnum.name());
        resultRespVO.setFormatSuffix(dataSourceDataFormatEnum.getSuffix());
        resultRespVO.setResultName(sourceDataDO.getId() + CLEAN_PREFIX + dataSourceDataFormatEnum.getSuffix());

        return resultRespVO;
    }

    /**
     * 根据规则获取网页预设语言
     *
     * @param datasetId 数据集 ID
     * @param url       URL
     * @return
     */
    @Override
    public String getHtmlLanguageRule(Long datasetId, String url) {

        DatasetHandleRulesDO filteredRule = getFilteredRule(datasetId, DataSourceDataTypeEnum.HTML.name(), url, null);
        CleanRuleVO cleanRuleVO = JSONUtil.toBean(filteredRule.getCleanRule(), CleanRuleVO.class);
        return cleanRuleVO.getHtmlCleanRule().getAcceptLanguage();
    }


    /**
     * 获取符合需求的规则 ID集合
     *
     * @param datasetId 数据集 ID
     * @param ruleType  数据类型 对应源数据类型
     * @param data      数据类型
     * @param ruleId    规则 ID ，如果为 null 查询所有类型规则
     * @return
     */
    public DatasetHandleRulesDO getFilteredRule(Long datasetId, String ruleType, String data, Long ruleId) {
        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .eq(DatasetHandleRulesDO::getDatasetId, datasetId)
                .eq(DatasetHandleRulesDO::getRuleType, ruleType)
                .eq(DatasetHandleRulesDO::getEnable, true)
                .eq(BooleanUtil.isTrue(ruleId != null), DatasetHandleRulesDO::getId, ruleId);

        List<DatasetHandleRulesDO> datasetHandleRulesDOS = handleRulesMapper.selectList(wrapper);

        // 根据数据匹配规则
        List<DatasetHandleRulesDO> filterIs = matchRules(data, ruleType, datasetHandleRulesDOS);

        if (filterIs.size() > 1) {
            List<Long> filterIIDs = filterIs.stream().map(DatasetHandleRulesDO::getId).collect(Collectors.toList());

            List<DatasetHandleRulesDO> repeatRuleDOS = getRuleByIds(filterIIDs);

            List<String> ruleNames = repeatRuleDOS.stream().map(DatasetHandleRulesDO::getRuleName).collect(Collectors.toList());
            throw exception(DATASET_HANDLE_RULE_REPEAT_NORMAL, CollUtil.join(ruleNames, ","));
        }


        // 没有匹配到用户指定的预处理规则
        if (CollUtil.isEmpty(filterIs)) {
            log.info("未匹配到用户配置的数据清洗规则，采用系统默认规则");
            // 获取系统配置
            return getSystemRuleConfig(ruleType);
        }
        // 匹配规则
        return filterIs.get(0);
    }

    /**
     * 执行清洗流程
     * 如果 data URL
     *
     * @return 编号
     */
    @Override
    public String processCleanRule(DatasetHandleRulesDO rulesDO, String url) {
        if (rulesDO == null) {
            throw exception(DATASET_HANDLE_RULES_NULL);
        }

        CleanRuleVO cleanRuleVO = JSONUtil.toBean(rulesDO.getCleanRule(), CleanRuleVO.class);

        if (DataSourceDataTypeEnum.HTML.name().equals(rulesDO.getRuleType())) {
            String data = TextCleanAndSplitUtils.processHtmlRule(url, cleanRuleVO.getHtmlCleanRule());
            return TextCleanAndSplitUtils.processCommonRule(data, cleanRuleVO.getCommonCleanRule());
        } else {
            Tika tika = new Tika();
            String data;
            // 获取文件数据
            try {
                data = tika.parseToString(new URL(url));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return TextCleanAndSplitUtils.processCommonRule(data, cleanRuleVO.getCommonCleanRule());
        }

    }

    private DatasetHandleRulesDO getSystemRuleConfig(String ruleType) {
        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .eq(DatasetHandleRulesDO::getFromScene, HandleRuleFromSceneEnum.SYSTEM.name())
                .eq(DatasetHandleRulesDO::getRuleType, ruleType)
                .eq(DatasetHandleRulesDO::getEnable, true);
        DatasetHandleRulesDO systemRuleConfig = handleRulesMapper.selectOne(wrapper);
        if (systemRuleConfig == null) {
            throw exception(DATASET_HANDLE_SYS_RULE_NO_EXISTS);
        }
        return systemRuleConfig;

    }

    /**
     * 匹配规则
     *
     * @param ruleType
     * @param rulesDOS
     * @return
     */
    private static List<DatasetHandleRulesDO> matchRules(String data, String ruleType, List<DatasetHandleRulesDO> rulesDOS) {

        DataSourceDataTypeEnum dataSourceDataTypeEnum = DataSourceDataTypeEnum.valueOf(ruleType);
        List<DatasetHandleRulesDO> matchIdList = new ArrayList<>();

        switch (dataSourceDataTypeEnum) {
            case HTML:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchHtmlRules(data, filters)) {
                        matchIdList.add(ruleDO);
                    }
                }
                break;
            case DOCUMENT:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchDocRules(data, filters)) {
                        matchIdList.add(ruleDO);
                    }
                }
                break;
            case CHARACTERS:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchCharactersRules(data, filters)) {
                        matchIdList.add(ruleDO);
                    }
                }
                break;
            default:
                throw new RuntimeException("获取规则失败，规则类型不匹配:" + ruleType);

        }
        log.info("完成清洗规则匹配，已经匹配到的规则 ID 为{}", matchIdList);
        return matchIdList;
    }

    // 匹配 HTML 规则
    private static Boolean matchHtmlRules(String url, List<String> filters) {
        for (String filter : filters) {
            if (filter.endsWith("*")) {
                String patternString = filter.replace("*", ".+") + "(/.*)?";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    return true;
                }
            } else {
                if (url.equals(filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 匹配 DOC 规则
    private static Boolean matchDocRules(String fileName, List<String> filters) {
        for (String filter : filters) {
            String patternString = filter.replace(".", "\\.").replace("*", ".*");
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    // 匹配 TEXT 规则
    private static Boolean matchCharactersRules(String fileName, List<String> filters) {
        for (String filter : filters) {
            if (filter.endsWith("*")) {
                String patternString = "^" + filter.replace("_", "\\_").replace("*", ".+") + "$";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {
                    return true;
                }
            } else {
                if (fileName.equals(filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validateExists(Long id) {
        if (handleRulesMapper.selectById(id) == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
    }
}
