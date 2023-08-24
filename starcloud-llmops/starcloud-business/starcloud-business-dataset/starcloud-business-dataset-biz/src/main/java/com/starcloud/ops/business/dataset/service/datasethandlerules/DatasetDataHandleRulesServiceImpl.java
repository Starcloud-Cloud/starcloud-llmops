package com.starcloud.ops.business.dataset.service.datasethandlerules;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.*;
import com.starcloud.ops.business.dataset.convert.datasethandlerules.DatasetHandleRulesConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasethandlerules.DatasetHandleRulesMapper;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
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
    private DatasetHandleRulesMapper handleRulesMapper;

    /**
     * 获得规则分页
     *
     * @param pageReqVO 分页查询
     * @return 规则分页
     */
    @Override
    public PageResult<DatasetHandleRulesRespVO> getRulePage(DatasetHandleRulesPageReqVO pageReqVO) {

        // 获取数据集信息
        DatasetsDO datasets = datasetsService.getDatasets(pageReqVO.getDatasetUid());

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
    public Boolean createDefaultRules(DatasetHandleRulesCreateReqVO createReqVO) {
        // 获取数据集信息
        DatasetsDO datasets = datasetsService.getDatasets(createReqVO.getDatasetUid());

        createReqVO.setDatasetUid(String.valueOf(datasets.getId()));
        // 数据转换
        DatasetHandleRulesDO convert = DatasetHandleRulesConvert.INSTANCE.convert(createReqVO);

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

        DatasetHandleRulesDO updateObj = DatasetHandleRulesConvert.INSTANCE.convert(updateReqVO);
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
        DatasetsDO datasets = datasetsService.getDatasets(debugReqVO.getDatasetUid());

        List<Long> filteredRuleIds = this.getFilteredRuleIds(datasets.getId(), debugReqVO.getDataType(), debugReqVO.getUrl(), null);

        if (filteredRuleIds.size()>1){
            List<DatasetHandleRulesDO> repeatRuleDOS = this.getRuleByIds(filteredRuleIds);
            List<String> ruleNames = repeatRuleDOS.stream().map(DatasetHandleRulesDO::getRuleName).collect(Collectors.toList());
            throw exception(DATASET_HANDLE_RULE_REPEAT_NORMAL,CollUtil.join(ruleNames,","));
        }

        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .in(DatasetHandleRulesDO::getId, filteredRuleIds)
                .eq(DatasetHandleRulesDO::getEnable, true);

        DatasetHandleRulesDO handleRulesDO = handleRulesMapper.selectOne(wrapper);

        CleanRuleVO cleanRuleVO = JSONUtil.toBean(handleRulesDO.getCleanRule(), CleanRuleVO.class);


        String cleanData;
        if (DataSourceDataTypeEnum.HTML.name().equals(debugReqVO.getDataType())) {
            String data = TextCleanAndSplitUtils.processHtmlRule(debugReqVO.getUrl(), cleanRuleVO.getHtmlCleanRule());
             cleanData = TextCleanAndSplitUtils.processCommonRule(data, cleanRuleVO.getCommonCleanRule());
        } else if (DataSourceDataTypeEnum.CHARACTERS.name().equals(debugReqVO.getDataType())) {
            String data = TextCleanAndSplitUtils.processHtmlRule(debugReqVO.getUrl(), cleanRuleVO.getHtmlCleanRule());
             cleanData = TextCleanAndSplitUtils.processCommonRule(data, cleanRuleVO.getCommonCleanRule());
        } else {
            throw exception(DATASET_HANDLE_RULE_TYPE_UNKNOWN);
        }
        datasetHandleRulesDebugRespVO.setRuleName(handleRulesDO.getRuleName());
        datasetHandleRulesDebugRespVO.setData(cleanData);
        return datasetHandleRulesDebugRespVO;
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
    public List<Long> getFilteredRuleIds(Long datasetId, String ruleType, String data, Long ruleId) {
        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .eq(DatasetHandleRulesDO::getDatasetId, datasetId)
                .eq(DatasetHandleRulesDO::getRuleType, ruleId)
                .eq(DatasetHandleRulesDO::getEnable, true)
                .eq(BooleanUtil.isTrue(ruleId == null), DatasetHandleRulesDO::getId, ruleId);

        List<DatasetHandleRulesDO> datasetHandleRulesDOS = handleRulesMapper.selectList(wrapper);


        List<Long> filterIs = matchRules(data, ruleType, datasetHandleRulesDOS);
        // 没有匹配到用户指定的预处理规则
        if (CollUtil.isEmpty(filterIs)) {
            // 获取系统配置
            DatasetHandleRulesDO systemRuleConfig = getSystemRuleConfig();
            filterIs = CollUtil.toList(systemRuleConfig.getId());
        }
        // 匹配规则
        return filterIs;
    }

    /**
     * 执行清洗流程
     * 如果 data URL
     *
     * @return 编号
     */
    @Override
    public String processCleanRule(List<Long> ruleIds, String url) {
        if (CollUtil.isEmpty(ruleIds)) {
            throw exception(DATASET_HANDLE_RULES_NULL);
        }
        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .in(DatasetHandleRulesDO::getId, ruleIds)
                .eq(DatasetHandleRulesDO::getEnable, true);
        DatasetHandleRulesDO handleRulesDO = handleRulesMapper.selectOne(wrapper);
        CleanRuleVO cleanRuleVO = JSONUtil.toBean(handleRulesDO.getCleanRule(), CleanRuleVO.class);

        if (DataSourceDataTypeEnum.HTML.name().equals(handleRulesDO.getRuleType())) {
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

    private DatasetHandleRulesDO getSystemRuleConfig() {
        // 获取当前数据集
        LambdaQueryWrapper<DatasetHandleRulesDO> wrapper = Wrappers.lambdaQuery(DatasetHandleRulesDO.class)
                .eq(DatasetHandleRulesDO::getFromScene, "SYSTEM")
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
    private static List<Long> matchRules(String data, String ruleType, List<DatasetHandleRulesDO> rulesDOS) {

        DataSourceDataTypeEnum dataSourceDataTypeEnum = DataSourceDataTypeEnum.valueOf(ruleType);
        List<Long> matchIdList = new ArrayList<>();

        switch (dataSourceDataTypeEnum) {
            case HTML:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchHtmlRules(data, filters)) {
                        matchIdList.add(ruleDO.getId());
                    }
                }
                return matchIdList;
            case DOCUMENT:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchDocRules(data, filters)) {
                        matchIdList.add(ruleDO.getId());
                    }
                }
                return matchIdList;
            case CHARACTERS:
                for (DatasetHandleRulesDO ruleDO : rulesDOS) {
                    List<String> filters = CollUtil.toList(ruleDO.getRuleFilter().split(","));
                    if (matchCharactersRules(data, filters)) {
                        matchIdList.add(ruleDO.getId());
                    }
                }
                return matchIdList;
            default:
                throw new RuntimeException("获取规则失败，规则类型不匹配:" + ruleType);

        }
    }


    // 匹配 HTML 规则
    private static Boolean matchHtmlRules(String url, List<String> filters) {
        for (String filter : filters) {
            if (filter.endsWith("*")) {
                String patternString = filter.replace("*", ".+") + ".*";
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

    //
    // private String debugFileRule(MultipartFile multipartFile, CleanRule cleanRule) {
    //
    //     InputStream inputStream;
    //     try {
    //         inputStream = multipartFile.getInputStream();
    //     } catch (IOException e) {
    //         throw new RuntimeException("文件有误，请重新上传");
    //     }
    //     Tika tika = new Tika();
    //     // 获取文件数据
    //     String text = null;
    //     try {
    //         text = tika.parseToString(inputStream);
    //     } catch (IOException | TikaException e) {
    //         throw new RuntimeException("文件有误，请重新上传");
    //     }
    //     return debugCharactersRule(text, cleanRule);
    // }
    //
    // private String debugCharactersRule(String content, CleanRule cleanRule) {
    //
    //     String data = TextCleanAndSplitUtils.cleanText(content, DataSourceDataTypeEnum.CHARACTERS.name(), cleanRule);
    //
    //     return TextCleanAndSplitUtils.processFormat(data, cleanRule.getConvertFormat(), DataSourceDataTypeEnum.CHARACTERS.name());
    // }
    //
    // private String debugUrlRule(String url, CleanRule cleanRule) {
    //
    //     String content;
    //     try {
    //
    //         String normalize = URLUtil.normalize(url);
    //
    //         Connection connection = Jsoup.connect(normalize);
    //
    //         // 设置请求头中的 Accept-Language 属性
    //         connection.header("Accept-Language", cleanRule.getAcceptLanguage());
    //
    //         content = connection.get().toString();
    //     } catch (RuntimeException | IOException e) {
    //         throw new RuntimeException("链接内容获取失败，换个链接试试呗");
    //     }
    //     String data = TextCleanAndSplitUtils.cleanText(content, DataSourceDataTypeEnum.HTML.name(), cleanRule);
    //
    //     return TextCleanAndSplitUtils.processFormat(data, cleanRule.getConvertFormat(), DataSourceDataTypeEnum.HTML.name());
    // }
    //
    //
    private void validateExists(Long id) {
        if (handleRulesMapper.selectById(id) == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
    }
    //
    // private CleanRule setUrlCleanRule() {
    //     return new CleanRule()
    //             .setBlackList(Collections.singletonList(DEFAULT_HANDLER))
    //             .setWhiteList(null)
    //             .setRemoveConsecutiveSpaces(true)
    //             .setRemoveConsecutiveNewlines(true)
    //             .setRemoveConsecutiveTabs(true)
    //             .setRemoveUrlsEmails(true)
    //             .setAcceptLanguage(DEFAULT_LANGUAGE)
    //             .setConvertFormat(DataSourceDataFormatEnum.MARKDOWN.name());
    // }
    //
    // private CleanRule setDocumentCleanRule() {
    //     return new CleanRule()
    //             .setBlackList(null)
    //             .setWhiteList(null)
    //             .setRemoveConsecutiveSpaces(true)
    //             .setRemoveConsecutiveNewlines(true)
    //             .setRemoveConsecutiveTabs(true)
    //             .setRemoveUrlsEmails(true)
    //             .setAcceptLanguage(DEFAULT_LANGUAGE)
    //             .setConvertFormat(DataSourceDataFormatEnum.TXT.name());
    // }
    //
    // private CleanRule setCharactersCleanRule() {
    //     return new CleanRule()
    //             .setBlackList(null)
    //             .setWhiteList(null)
    //             .setRemoveConsecutiveSpaces(true)
    //             .setRemoveConsecutiveNewlines(true)
    //             .setRemoveConsecutiveTabs(true)
    //             .setRemoveUrlsEmails(true)
    //             .setAcceptLanguage(DEFAULT_LANGUAGE)
    //             .setConvertFormat(DataSourceDataFormatEnum.TXT.name());
    // }


}
