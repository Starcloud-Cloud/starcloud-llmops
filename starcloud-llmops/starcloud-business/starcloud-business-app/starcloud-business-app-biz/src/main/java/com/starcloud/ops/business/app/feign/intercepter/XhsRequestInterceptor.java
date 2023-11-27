package com.starcloud.ops.business.app.feign.intercepter;

import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.DICT_DATA_NOT_EXISTS;

@Slf4j
public class XhsRequestInterceptor implements RequestInterceptor {

    private static final String COOKIE = "cookie";

    private static final String DICTTYPE = "xhs";

    private static final String DICTLABEL = "xhs-cookie";

    @Resource
    private DictDataService dictDataService;

    @Override
    public void apply(RequestTemplate template) {
        DictDataDO dictDataDO = dictDataService.parseDictData(DICTTYPE, DICTLABEL);
        if (dictDataDO == null || StringUtils.isBlank(dictDataDO.getValue())) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
        template.header(COOKIE, dictDataDO.getValue());
    }
}
