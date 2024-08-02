package com.starcloud.ops.business.app.feign.intercepter;

import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.common.value.qual.EnsuresMinLenIf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.DICT_DATA_NOT_EXISTS;

@Slf4j
public class XhsRequestInterceptor implements RequestInterceptor {

    private static final String COOKIE = "cookie";

    private static final String DICTTYPE = "xhs";

    private static final String DICTLABEL = "xhs-cookie";

    private static List<String> USER_AGENT = Arrays.asList("Apifox/1.0.0 (https://apifox.com)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0");


    @Resource
    private DictDataService dictDataService;

    @Override
    public void apply(RequestTemplate template) {
        DictDataDO dictDataDO = dictDataService.parseDictData(DICTTYPE, DICTLABEL);
        if (dictDataDO == null || StringUtils.isBlank(dictDataDO.getValue())) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
        template.header(COOKIE, dictDataDO.getValue());

        template.header("User-Agent", USER_AGENT.get(RandomUtil.randomInt(USER_AGENT.size())));
    }
}
