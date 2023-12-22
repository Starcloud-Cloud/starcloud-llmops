package com.starcloud.ops.business.app.service.xhs.crawler.impl;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.XHS_REMOTE_ERROR;

@Slf4j
@Component
@ConditionalOnProperty(value = "xhs.remote.agent", matchIfMissing = true, havingValue = "xhs")
public class XhsNoteDetailWrapperImpl implements XhsNoteDetailWrapper {

    @Resource
    private XhsCilent xhsCilent;

    @Resource
    private SmsSendApi smsSendApi;

    @Override
    @SneakyThrows(InterruptedException.class)
    public ServerRequestInfo requestDetail(String noteId) {
        try {
            return requestDetail0(noteId);
        } catch (JSONException e) {
            // 小红书偶尔点赞数返回 10+ 转int错误  重试一次
            log.warn("{} 数据错误重试 {}", noteId, e.getMessage());
            TimeUnit.MILLISECONDS.sleep(500);
            return requestDetail0(noteId);
        }
    }

    public ServerRequestInfo requestDetail0(String noteId) {
        String html = StringUtils.EMPTY;
        try {
            html = xhsCilent.noteDetail(noteId);
            Document doc = Jsoup.parse(html);
            String jsonStr = doc.getElementsByTag(XhsDetailConstants.SCRIPT).last().html().replace(XhsDetailConstants.INITIAL_STATE, StringUtils.EMPTY);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            Boolean loggedIn = jsonObject.getJSONObject(XhsDetailConstants.USER).getBoolean(XhsDetailConstants.LOGGED_IN);
            if (BooleanUtils.isNotTrue(loggedIn)) {
                throw exception(new ErrorCode(500, "xhs登录过期"));
            }
            ServerRequestInfo requestInfo = jsonObject.getJSONObject(XhsDetailConstants.NOTE)
                    .getObject(XhsDetailConstants.SERVER_REQUEST_INFO, ServerRequestInfo.class);
            if (!"success".equalsIgnoreCase(requestInfo.getState())) {
                throw exception(new ErrorCode(500, requestInfo.getErrMsg()));
            }
            NoteDetail noteDetail = jsonObject.getJSONObject(XhsDetailConstants.NOTE)
                    .getJSONObject(XhsDetailConstants.NOTE_DETAIL_MAP)
                    .getJSONObject(noteId)
                    .getObject(XhsDetailConstants.NOTE, NoteDetail.class);
            requestInfo.setNoteDetail(noteDetail);
            return requestInfo;
        } catch (ServiceException e) {
            log.warn("处理小红书数据异常, {}", e.getMessage());
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
