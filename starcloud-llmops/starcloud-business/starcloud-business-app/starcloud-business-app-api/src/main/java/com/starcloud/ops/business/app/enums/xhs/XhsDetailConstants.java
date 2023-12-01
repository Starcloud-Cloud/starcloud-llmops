package com.starcloud.ops.business.app.enums.xhs;


import cn.hutool.core.util.ReUtil;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_URL_ERROR;

public class XhsDetailConstants {

    public static final String SCRIPT = "script";

    public static final String USER = "user";

    public static final String LOGGED_IN = "loggedIn";

    public static final String NOTE = "note";

    public static final String SERVER_REQUEST_INFO = "serverRequestInfo";

    public static final String NOTE_DETAIL_MAP = "noteDetailMap";

    public static final String INITIAL_STATE = "window.__INITIAL_STATE__=";

    public static final String DOMAIN = "https://www.xiaohongshu.com/explore/";

    public static final String XHS_URL_REGEX = "^(https://www.xiaohongshu.com/explore/{1,1}\\w{24,24})$";

    public static void validNoteUrl(String noteUrl) {
        boolean match = ReUtil.isMatch(XhsDetailConstants.XHS_URL_REGEX, noteUrl);
        if (!match) {
            throw exception(XHS_URL_ERROR, noteUrl);
        }
    }

}
