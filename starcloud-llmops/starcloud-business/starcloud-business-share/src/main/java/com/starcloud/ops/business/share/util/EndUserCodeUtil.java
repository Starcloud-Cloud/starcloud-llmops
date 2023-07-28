package com.starcloud.ops.business.share.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class EndUserCodeUtil {

    public static String parseUserCodeAndSaveCookie(String upfSId, HttpServletRequest request, HttpServletResponse response) {

        if (StrUtil.isBlank(upfSId)) {

            log.info("upfSId is blank");
            upfSId = request.getSession().getId();

            Cookie cookie = new Cookie("fSId", upfSId);
            // 设置为一年的有效期
            cookie.setMaxAge(365 * 24 * 60 * 60);
            response.addCookie(cookie);
            log.info("addCookie upfSId");
        }

        return upfSId;
    }
}
