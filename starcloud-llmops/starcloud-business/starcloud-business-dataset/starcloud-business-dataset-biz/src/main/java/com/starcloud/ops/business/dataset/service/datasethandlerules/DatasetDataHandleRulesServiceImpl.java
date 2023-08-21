package com.starcloud.ops.business.dataset.service.datasethandlerules;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesDebugReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasethandlerules.DatasetHandleRulesConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.mysql.segment.DatasetHandleRulesMapper;
import com.starcloud.ops.business.dataset.enums.DataSourceDataFormatEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRule;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_HANDLE_RULE_EXISTS;

/**
 * 数据集规则实现类
 *
 * @author Alan Cusack
 */

@Slf4j
@Service
public class DatasetDataHandleRulesServiceImpl implements DatasetDataHandleRulesService {

    @Autowired
    private DatasetHandleRulesMapper handleRulesMapper;

    // 默认的链接清洗标签
    private static final String DEFAULT_HANDLER = "script,.hidden,style,form";

    private static final String DEFAULT_LANGUAGE = "zh";
    private static final Integer CHUNK_SIZE = 300;


    /**
     * 创建规则
     *
     * @param datasetId 创建信息
     * @return 编号
     */
    @Override
    public Boolean createDefaultRules(Long datasetId) {
        DatasetHandleRulesDO handleRulesDO = new DatasetHandleRulesDO();
        // 配置默认规则

        handleRulesDO.setCleanRule(JSONUtil.toJsonStr(new CleanRuleVO()
                .setURL(setUrlCleanRule())
                .setDOCUMENT(setDocumentCleanRule())
                .setCHARACTERS(setCharactersCleanRule())));

        handleRulesDO.setSplitRule(JSONUtil.toJsonStr(new SplitRule()
                .setChunkSize(CHUNK_SIZE)
                .setSeparator(null)));

        handleRulesDO.setDatasetId(datasetId);

        int result = handleRulesMapper.insert(handleRulesDO);
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
     * @param Id 创建信息
     * @return 编号
     */
    @Override
    public DatasetHandleRulesRespVO getRuleById(Long Id) {
        DatasetHandleRulesDO handleRulesDO = handleRulesMapper.selectById(Id);
        if (handleRulesDO == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
        return DatasetHandleRulesConvert.INSTANCE.convert(handleRulesDO);

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
     * 规则调试
     *
     * @param debugReqVO 调试信息
     * @return 编号
     */
    @Override
    public String debugRule(DatasetHandleRulesDebugReqVO debugReqVO) {

        DataSourceDataTypeEnum dataTypeEnum = DataSourceDataTypeEnum.valueOf(debugReqVO.getDataType());

        String content = null;
        switch (dataTypeEnum) {
            case URL:
                content = debugUrlRule(debugReqVO.getData(), debugReqVO.getCleanRuleVO().getURL());
                break;
            case CHARACTERS:
                content = debugCharactersRule(debugReqVO.getData(), debugReqVO.getCleanRuleVO().getCHARACTERS());
                break;
            case DOCUMENT:
                content = debugFileRule(debugReqVO.getUploadFile(), debugReqVO.getCleanRuleVO().getDOCUMENT());
                break;
            default:
                throw new RuntimeException("数据类型选择错误，请重新选择");
        }
        return content;
    }


    private String debugFileRule(MultipartFile multipartFile, CleanRule cleanRule) {

        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("文件有误，请重新上传");
        }
        Tika tika = new Tika();
        // 获取文件数据
        String text = null;
        try {
            text = tika.parseToString(inputStream);
        } catch (IOException | TikaException e) {
            throw new RuntimeException("文件有误，请重新上传");
        }
        return debugCharactersRule(text, cleanRule);
    }

    private String debugCharactersRule(String content, CleanRule cleanRule) {

        String data = TextCleanAndSplitUtils.cleanText(content, DataSourceDataTypeEnum.CHARACTERS.name(), cleanRule);

        return TextCleanAndSplitUtils.processFormat(data, cleanRule.getConvertFormat(), DataSourceDataTypeEnum.CHARACTERS.name());
    }

    private String debugUrlRule(String url, CleanRule cleanRule) {

        String content;
        try {

            String normalize = URLUtil.normalize(url);

            Connection connection = Jsoup.connect(normalize);

            // 设置请求头中的 Accept-Language 属性
            connection.header("Accept-Language", cleanRule.getAcceptLanguage());

            content = connection.get().toString();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("链接内容获取失败，换个链接试试呗");
        }
        String data = TextCleanAndSplitUtils.cleanText(content, DataSourceDataTypeEnum.URL.name(), cleanRule);

        return TextCleanAndSplitUtils.processFormat(data, cleanRule.getConvertFormat(), DataSourceDataTypeEnum.URL.name());
    }


    private void validateExists(Long id) {
        if (handleRulesMapper.selectById(id) == null) {
            throw exception(DATASET_HANDLE_RULE_EXISTS);
        }
    }

    private CleanRule setUrlCleanRule() {
        return new CleanRule()
                .setBlackList(Collections.singletonList(DEFAULT_HANDLER))
                .setWhiteList(null)
                .setRemoveConsecutiveSpaces(true)
                .setRemoveConsecutiveNewlines(true)
                .setRemoveConsecutiveTabs(true)
                .setRemoveUrlsEmails(true)
                .setAcceptLanguage(DEFAULT_LANGUAGE)
                .setConvertFormat(DataSourceDataFormatEnum.MARKDOWN.name());
    }

    private CleanRule setDocumentCleanRule() {
        return new CleanRule()
                .setBlackList(null)
                .setWhiteList(null)
                .setRemoveConsecutiveSpaces(true)
                .setRemoveConsecutiveNewlines(true)
                .setRemoveConsecutiveTabs(true)
                .setRemoveUrlsEmails(true)
                .setAcceptLanguage(DEFAULT_LANGUAGE)
                .setConvertFormat(DataSourceDataFormatEnum.TXT.name());
    }

    private CleanRule setCharactersCleanRule() {
        return new CleanRule()
                .setBlackList(null)
                .setWhiteList(null)
                .setRemoveConsecutiveSpaces(true)
                .setRemoveConsecutiveNewlines(true)
                .setRemoveConsecutiveTabs(true)
                .setRemoveUrlsEmails(true)
                .setAcceptLanguage(DEFAULT_LANGUAGE)
                .setConvertFormat(DataSourceDataFormatEnum.TXT.name());
    }

}
