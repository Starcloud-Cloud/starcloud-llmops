package com.starcloud.ops.business.app.service.plugins;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.plugin.WordCheckContent;
import com.starcloud.ops.business.app.api.plugin.WordCheckResp;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.RISK_WORD_ERROR;

@Component
public class WuyouClient {

    @Value("${check51.app.id}")
    private String appId;

    @Value("${check51.secret.secretKey}")
    private String secretKey;

    private static final String URL = "https://api.check51.com/api/word/detect-text";


    public WordCheckContent risk(String sourceContent) {
        Map<String, Object> body = new HashMap<>();
        body.put("appid", appId);
        body.put("secretKey", secretKey);
        body.put("content", StringUtil.encodeToUnicode(sourceContent));
        body.put("isCommonWord", true);
        body.put("isDefined", true);
        body.put("isDefinedTeam", true);
        String result = HttpUtil.post(URL, body);
        WordCheckResp resp = JSONUtil.toBean(result, WordCheckResp.class);

        if (!Objects.equals("0000", resp.getCode())) {
            throw exception(RISK_WORD_ERROR, resp.getMsg());
        }
        return resp.getData();
    }

}
