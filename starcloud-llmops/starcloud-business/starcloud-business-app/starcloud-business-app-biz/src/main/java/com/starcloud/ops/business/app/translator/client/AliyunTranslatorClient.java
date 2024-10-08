package com.starcloud.ops.business.app.translator.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.GetBatchTranslateRequest;
import com.aliyun.alimt20181012.models.GetBatchTranslateResponse;
import com.aliyun.alimt20181012.models.GetBatchTranslateResponseBody;
import com.starcloud.ops.business.app.translator.dto.TranslateIndexDTO;
import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialException;
import java.util.*;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Slf4j
@Component
public class AliyunTranslatorClient {

    @Resource
    private Client mtClient;

    /**
     * 批量文本翻译
     *
     * @param request 翻译请求
     * @return 翻译响应
     */
    public TranslateResponse bathTranslate(TranslateRequest request) {

        try {
            GetBatchTranslateRequest bathRequest = new GetBatchTranslateRequest();
            bathRequest.setFormatType("text");
            bathRequest.setApiType("translate_standard");
            bathRequest.setScene("general");
            String sourceLanguage = request.getSourceLanguage();
            if (StringUtils.isBlank(sourceLanguage)) {
                sourceLanguage = "auto";
            }
            bathRequest.setSourceLanguage(sourceLanguage);
            bathRequest.setTargetLanguage(request.getTargetLanguage());
            List<String> textList = request.getTextList();
            if (CollectionUtil.isEmpty(textList)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本不能为空"));
            }
            if (textList.size() > 50) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本不能超过50条"));
            }
            //  阿里云批量翻译单条字符长度上限是1000字符，一次最多可翻译50条
            //  text字符数超过1000个字符 拆分后翻译再合并结果  拆分后的总条数要小于50
            //  翻译前： 阿里云入参的key 按数组脚标 index * 100   如果字符超过1000拆分，key 为 index * 100 + 拆分后的顺序
            //  翻译后： 阿里云出参的key index * 100  <=  key < index * 100 + 100 为一条元数据拆分后对应的翻译结果 按顺序合并为一条
            Map<String, String> processText = processText(textList);
            if (processText.size() > 50) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本不能超过50条，且单条字符数不能超过1000"));
            }

            bathRequest.setSourceText(JSONUtil.toJsonStr(processText));
            GetBatchTranslateResponse bathResponse = mtClient.getBatchTranslate(bathRequest);
            if (bathResponse == null) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本失败"));
            }
            if (bathResponse.getBody() == null) {
                throw ServiceExceptionUtil.exception(new ErrorCode(19999, "批量翻译文本失败"));
            }
            if (bathResponse.getStatusCode() != 200) {
                throw ServiceExceptionUtil.exception(Integer.valueOf(new ErrorCode(bathResponse.getStatusCode(), "批量翻译文本失败: RequestId: " + bathResponse.getBody().requestId) + ". You can Go to https://next.api.aliyun.com/troubleshoot?spm=api-workbench.api_explorer.0.0.1694e014X6pJvQ to check your error. Message: " + bathResponse.getBody().message));
            }
            GetBatchTranslateResponseBody body = bathResponse.getBody();
            if (body.getCode() != 200 || CollectionUtil.isEmpty(body.getTranslatedList())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(body.getCode(), body.getMessage() + " RequestId: " + body.requestId + ". You can Go to https://next.api.aliyun.com/troubleshoot?spm=api-workbench.api_explorer.0.0.1694e014X6pJvQ to check your error."));
            }

            String detectedLanguage = (String) body.getTranslatedList().get(0).get("detectedLanguage");
            List<TranslateIndexDTO> indexList = parseResult(body.getTranslatedList());
            int wordCount = indexList.stream().map(TranslateIndexDTO::getWordCount).reduce(0, Integer::sum);

            TranslateResponse response = new TranslateResponse();
            response.setDetectedLanguage(detectedLanguage);
            response.setWordCount(wordCount);
            response.setTranslatedList(indexList);
            return response;
        } catch (SerialException e) {
            log.error("批量文本翻译失败 {}", e.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("批量文本翻译失败 {}", e.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(19999, e.getMessage()));
        }
    }

    /**
     * 翻译前：阿里云入参的key 按数组脚标 index * 100   如果字符超过1000拆分，key 为 index * 100 + 拆分后的顺序
     * index * 100 <= x < index * 100 + 100 为一条元数据拆分的结果 阿里云最多支持50条 不会溢出
     *
     * @param texts
     * @return
     */
    private Map<String, String> processText(List<String> texts) {
        HashMap<String, String> result = new HashMap<>(texts.size());

        for (int i = 0; i < texts.size(); i++) {
            String str = texts.get(i);
            if (StringUtils.isBlank(str)) {
                continue;
            }
            int maxLength = 900;
            if (str.length() > maxLength) {
                List<String> splitText = splitToChunks(str, maxLength);
                for (int j = 0; j < splitText.size(); j++) {
                    if (StringUtils.isBlank(splitText.get(j))) {
                        continue;
                    }
                    result.put(String.valueOf(i * 100 + j), splitText.get(j));
                }
                continue;
            }
            result.put(String.valueOf(i * 100), str);
        }
        return result;
    }

    /**
     * 翻译后：阿里云出参的key index * 100  <=  key < index * 100 + 100 为一条元数据拆分后对应的翻译结果 按顺序合并为一条
     * 合并后对应元数据的脚标为 key / 100
     *
     * @param translatedList
     * @return
     */
    private List<TranslateIndexDTO> parseResult(List<Map<String, ?>> translatedList) {
        TreeMap<Integer, TranslateIndexDTO> treeMap = new TreeMap<>();
        for (Map<String, ?> item : translatedList) {
            if (item != null && StringUtils.equals((String) item.get("code"), "200")) {
                String translated = (String) item.get("translated");
                Integer index = Integer.valueOf((String) item.get("index"));
                Integer count = Integer.valueOf((String) item.get("wordCount"));
                if (StringUtils.isNotBlank(translated)) {
                    treeMap.put(index, TranslateIndexDTO.of(index / 100, translated, count));
                }
            }
        }

        Map<Integer, TranslateIndexDTO> rootMap = new HashMap<>(translatedList.size());

        for (Integer key : treeMap.keySet()) {
            if (key % 100 == 0) {
                // 100的整数倍 拆分的第一条
                rootMap.put(key, treeMap.get(key));
            } else {
                TranslateIndexDTO sub = treeMap.get(key);
                TranslateIndexDTO root = rootMap.get(key / 100 * 100);
                root.setTranslated(root.getTranslated() + sub.getTranslated());
                root.setWordCount(root.getWordCount() + sub.getWordCount());
            }
        }

        return new ArrayList<>(rootMap.values());
    }


    public List<String> splitToChunks(String article, int maxChunkLength) {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        // 模拟原始字符数组，用于保留换行符和句号的位置
        char[] originalChars = article.toCharArray();

        for (int i = 0; i < originalChars.length; i++) {
            char c = originalChars[i];

            // 当遇到换行符时，直接添加当前chunk并开始新的chunk
            if (c == '\n') {
                if (i > 0 && originalChars[i - 1] == '\n') {
                    // 连续换行拼接在上一个chunk上
                    int previousIndex = chunks.size() - 1;
                    String previous = chunks.get(previousIndex) + "\n";
                    chunks.set(previousIndex, previous);
                } else {
                    addChunk(chunks, currentChunk, originalChars, i);
                }
                // 清空当前chunk
                currentChunk.setLength(0);
                continue;
            }

            currentChunk.append(c);

            // 当当前chunk长度达到或超过最大值时进行处理
            while (currentChunk.length() >= maxChunkLength) {
                // 尝试找到上一个句点作为分割位置
                int lastPeriodIndex = findLastSentenceEnd(currentChunk, maxChunkLength);
                if (lastPeriodIndex != -1) {
                    String chunk = currentChunk.substring(0, lastPeriodIndex).toString();
                    chunks.add(chunk);
                    currentChunk.delete(0, lastPeriodIndex);
                } else {
                    // 若没有找到句点，则强制按字符长度拆分
                    String chunk = currentChunk.substring(0, maxChunkLength).toString();
                    chunks.add(chunk);
                    currentChunk.delete(0, maxChunkLength);
                }
            }
        }

        // 添加剩余部分
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }
        return chunks;
    }

    private void addChunk(List<String> chunks, StringBuilder chunkBuilder, char[] originalChars, int endIndex) {
        String chunk = chunkBuilder.toString();
        // 从原始字符数组中取出分割符加入到chunk末尾
        if (endIndex < originalChars.length) {
            chunk += originalChars[endIndex];
        }
        chunks.add(chunk);
    }

    private int findLastSentenceEnd(StringBuilder sb, int maxLength) {
        for (int i = maxLength - 1; i >= 0; i--) {
            if (sb.charAt(i) == '.' || sb.charAt(i) == '。') {
                if (i < maxLength - 1) {
                    return i + 1;
                }
            }
        }
        return -1;
    }


}
