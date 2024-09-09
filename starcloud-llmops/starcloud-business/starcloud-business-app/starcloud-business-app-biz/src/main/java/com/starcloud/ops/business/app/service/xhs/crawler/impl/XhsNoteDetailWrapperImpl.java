package com.starcloud.ops.business.app.service.xhs.crawler.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.xhs.note.NoteDetail;
import com.starcloud.ops.business.app.api.xhs.note.ServerRequestInfo;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.feign.XhsCilent;
import com.starcloud.ops.business.app.service.xhs.crawler.XhsNoteDetailWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_REMOTE_ERROR;

@Slf4j
@Component
public class XhsNoteDetailWrapperImpl implements XhsNoteDetailWrapper {

    @Resource
    private XhsCilent xhsCilent;

    @Resource
    private SmsSendApi smsSendApi;

    @Override
    public ServerRequestInfo requestDetail(String noteId) {
        return requestDetail0(noteId, 0);
    }

    @SneakyThrows(InterruptedException.class)
    public ServerRequestInfo requestDetail0(String noteId, int retry) {
        String html = StringUtils.EMPTY;
        try {
            long start = System.currentTimeMillis();
            html = xhsCilent.noteDetail(noteId);
            Document doc = Jsoup.parse(html);
            String jsonStr = doc.getElementsByTag(XhsDetailConstants.SCRIPT).last().html().replace(XhsDetailConstants.INITIAL_STATE, StringUtils.EMPTY);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            Boolean loggedIn = jsonObject.getJSONObject(XhsDetailConstants.USER).getBoolean(XhsDetailConstants.LOGGED_IN);
            if (BooleanUtils.isNotTrue(loggedIn)) {
                throw exception(new ErrorCode(500, "xhs登录过期"));
            }

            ServerRequestInfo requestInfo = Optional.ofNullable(jsonObject.getJSONObject(XhsDetailConstants.NOTE))
                    .map(n -> n.getObject(XhsDetailConstants.SERVER_REQUEST_INFO, ServerRequestInfo.class))
                    .orElseThrow(() -> exception(new ErrorCode(500, "json转换异常")));
            if (!"success".equalsIgnoreCase(requestInfo.getState())) {
                throw exception(new ErrorCode(500, requestInfo.getErrMsg()));
            }

            NoteDetail noteDetail = Optional.ofNullable(jsonObject.getJSONObject(XhsDetailConstants.NOTE))
                    .map(n -> n.getJSONObject(XhsDetailConstants.NOTE_DETAIL_MAP))
                    .map(n -> n.getJSONObject(noteId))
                    .map(n -> n.getObject(XhsDetailConstants.NOTE, NoteDetail.class))
                    .orElseThrow(() -> exception(new ErrorCode(500, "json转换异常")));

            noteDetail.setDesc(ReUtil.replaceAll(noteDetail.getDesc(), XhsDetailConstants.TAGS, StringUtils.EMPTY));
            requestInfo.setNoteDetail(noteDetail);
            long end = System.currentTimeMillis();
            log.info("query note detail , rt = {} ms", end - start);
            return requestInfo;
        } catch (JSONException e) {
            if (retry == 0) {
                TimeUnit.MILLISECONDS.sleep(500);
            } else {
                log.warn("小红书数据json转换异常, {}", e.getMessage());
                throw e;
            }
            return requestDetail0(noteId, retry + 1);
        } catch (ServiceException e) {
            log.warn("小红书接口异常, {}", e.getMessage());
            sendMessage(e.getMessage());
            throw exception(XHS_REMOTE_ERROR, e.getMessage());
        } catch (Exception e) {
            log.warn("处理小红书数据异常, {}", html, e);
            sendMessage(e.getMessage());
            throw exception(XHS_REMOTE_ERROR, e.getMessage());
        }
    }

    private void sendMessage(String errorMsg) {
        try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("errorMsg", errorMsg);
            templateParams.put("date", LocalDateTime.now());
            templateParams.put("environment", SpringUtil.getActiveProfile());
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_XHS_LOGIN_WARN")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("系统通知信息发送失败", e);
        }
    }


}
