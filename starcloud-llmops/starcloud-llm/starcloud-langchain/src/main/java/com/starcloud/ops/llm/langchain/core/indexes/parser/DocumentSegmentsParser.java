package com.starcloud.ops.llm.langchain.core.indexes.parser;

import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitDetail;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class DocumentSegmentsParser {

    private static final String[] BACKUP_SEPARATORS = new String[]{"。", ".", "！", "!"};

    private static final String SEPARATOR = StringUtils.LF;

    private static final int MAX_TOKENS = 1000;

    private DocumentSegmentsParser() {
    }

    public static final DocumentSegmentsParser INSTANCE = new DocumentSegmentsParser();

    public List<SplitDetail> splitText(String text, SplitRule processRule) {
        if (StringUtils.isNoneBlank(processRule.getPattern())) {
            Pattern customize = Pattern.compile(processRule.getPattern());
            text = customize.matcher(text).replaceAll(StringUtils.EMPTY);
        }
        if (BooleanUtils.isTrue(processRule.getAutomatic())) {
            text = cleanText(true, false, text);
            List<SplitDetail> splitStr = processSplit(text, MAX_TOKENS, SEPARATOR);
            return mergeText(splitStr, MAX_TOKENS);
        } else {
            text = cleanText(processRule.getRemoveExtraSpaces(), processRule.getRemoveUrlsEmails(), text);
            List<SplitDetail> splitStr = processSplit(text, processRule.getMaxTokens(), processRule.getSeparator());
            return mergeText(splitStr, processRule.getMaxTokens());
        }
    }

    public List<SplitDetail> mergeText(List<SplitDetail> originalList, long maxTokens) {
        List<SplitDetail> newList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        long sumTokens = 0L;
        for (SplitDetail detail : originalList) {
            long curToken = TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, detail.getSegment());
            if (sumTokens + curToken <= maxTokens) {
                sumTokens += curToken;
                sb.append(detail.getSegment());
                continue;
            }
            newList.add(new SplitDetail(sb.toString(),sumTokens));
            sb.setLength(0);
            sb.append(detail.getSegment());
            sumTokens = curToken;
        }
        if (sb.length() > 0) {
            newList.add(new SplitDetail(sb.toString(),TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, sb.toString())));
        }
        return newList;
    }

    public String cleanText(Boolean removeExtraSpaces, Boolean removeUrlsEmails, String text) {
        if (BooleanUtils.isTrue(removeExtraSpaces)) {
            text = removeExtraSpaces(text);
        }

        if (BooleanUtils.isTrue(removeUrlsEmails)) {
            text = removeUrlsEmails(text);
        }
        return text;
    }

    public String removeExtraSpaces(String text) {
        Pattern line = Pattern.compile("\n{2,}");
        text = line.matcher(text).replaceAll(StringUtils.LF);
        Pattern space = Pattern.compile("[\t\f\r\\x20\u00a0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]{2,}");
        return space.matcher(text).replaceAll(StringUtils.SPACE);
    }

    public String removeUrlsEmails(String text) {
        Pattern email = Pattern.compile("([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)");
        return email.matcher(text).replaceAll(StringUtils.EMPTY);
    }

    public List<SplitDetail> processSplit(String text, int maxTokens, String separator) {
        String[] splits = text.split(separator);
        List<SplitDetail> result = new ArrayList<>();
        for (String split : splits) {
            Long currentToken = TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, split);
            if (currentToken <= maxTokens) {
                result.add(new SplitDetail(split,currentToken));
                continue;
            }
            String[] currentSplits = new String[]{split};
            if (BACKUP_SEPARATORS.length > 0) {
                for (String backupSeparator : BACKUP_SEPARATORS) {
                    if (split.contains(backupSeparator)) {
                        currentSplits = split.split(backupSeparator);
                        break;
                    }
                }
            }
            for (String currentSplit : currentSplits) {
                Long tokens = TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, currentSplit);
                if (tokens <= maxTokens) {
                    result.add(new SplitDetail(currentSplit,tokens));
                    continue;
                }
                // 按最大长度为maxTokens切割字符串
                int startIndex = 0;
                // 中文的token一般大于字符串长度小于字符串长度的两倍，所以中文是分段token可能会大于maxTokens 但是不会超过 maxTokens*2，
                int endIndex = Math.min(maxTokens, currentSplit.length());
                while (startIndex < currentSplit.length()) {
                    String part = currentSplit.substring(startIndex, endIndex);
                    result.add(new SplitDetail(part,TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, part)));
                    startIndex = endIndex;
                    endIndex = Math.min(startIndex + maxTokens, currentSplit.length());
                }
            }
        }
        return result;
    }
}
