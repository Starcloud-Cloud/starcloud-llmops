package com.starcloud.ops.business.app.enums.xhs;


import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;

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

    public static final String DOMAIN = "https://www.xiaohongshu.com/explore/([a-zA-Z0-9]{24})";

    public static final String XHS_URL_REGEX = "^(https://www.xiaohongshu.com/explore/{1,1}\\w{24,24}\\?*\\S*)";

    public static final String SHARE_LINK = "http://xhslink.com/[a-zA-Z]{1}/([a-zA-Z0-9]{12})";

    public static final String SHARE_NOTEID = "https://www.xiaohongshu.com/discovery/item/([a-zA-Z0-9]{24})";

    public static final String TAGS = "#(.*?)(?=\n|$)";

    public static void validNoteUrl(String noteUrl) {
        boolean match = ReUtil.isMatch(XhsDetailConstants.XHS_URL_REGEX, noteUrl);
        if (match) {
            return;
        }
        match = ReUtil.contains(XhsDetailConstants.SHARE_LINK, noteUrl);
        if (match) {
            return;
        }
        throw exception(XHS_URL_ERROR, noteUrl);
    }

    public static String parsingShareLink(String shareLink) {
        String shareUid = ReUtil.get(XhsDetailConstants.SHARE_LINK, shareLink, 0);
        String html = HttpUtil.get(shareUid);
        return ReUtil.get(XhsDetailConstants.SHARE_NOTEID, html, 1);
    }

    public static String parsingWebUrl(String noteUrl) {
        return ReUtil.get(XhsDetailConstants.DOMAIN, noteUrl, 1);
    }

    /**
     * 解析小红noteId
     *
     * @param str app分享链接/webUrl
     * @return 小红noteId
     */
    public static String parsingNoteId(String str) {
        if (ReUtil.contains(XhsDetailConstants.SHARE_LINK, str)) {
            return parsingShareLink(str);
        }

        if (ReUtil.isMatch(XhsDetailConstants.XHS_URL_REGEX, str)) {
            return parsingWebUrl(str);
        }
        throw exception(XHS_URL_ERROR, str);
    }

}
