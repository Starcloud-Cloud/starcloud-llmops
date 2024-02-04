package cn.iocoder.yudao.framework.common.enums;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.MISS_CLIENT_TYPE;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_SUPPORTED_CLIENT_TYPE;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Getter
@AllArgsConstructor
public enum ClientTypeEnum {


    BROWSER(1, "浏览器"),

    CLIENT(2, "客户端");


    private Integer code;

    private String desc;


    public static Integer getTypeCode(String codeStr) {
        if (StringUtils.isBlank(codeStr)) {
            return null;
        }

        if (!NumberUtil.isInteger(codeStr)) {
            throw exception(MISS_CLIENT_TYPE);
        }
        for (ClientTypeEnum value : ClientTypeEnum.values()) {
            if (value.code.equals(Integer.valueOf(codeStr))) {
                return value.code;
            }
        }
        throw exception(NOT_SUPPORTED_CLIENT_TYPE);
    }


}
