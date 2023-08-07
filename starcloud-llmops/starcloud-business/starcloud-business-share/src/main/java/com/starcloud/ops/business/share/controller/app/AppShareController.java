package com.starcloud.ops.business.share.controller.app;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.share.controller.app.vo.AppReq;
import com.starcloud.ops.business.share.util.EndUserCodeUtil;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@RestController
@RequestMapping("/share/app")
@Tag(name = "魔法AI-分享应用")
@Slf4j
public class AppShareController {

    @Autowired
    private EndUserServiceImpl endUserService;


    @GetMapping("/")
    @Operation(summary = "应用详情")
    @Parameter(name = "uid", description = "应用uuid", required = true)
    @PermitAll
    public String detail(@RequestParam(name = "uid") String appUid, @CookieValue(value = "fSId", required = false) String upfSId, HttpServletRequest request, HttpServletResponse response) {

        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);

        endUserService.webLogin(upfSId);

        return upfSId;
    }


    @PostMapping("/")
    @Operation(summary = "应用执行")
    @PermitAll
    public SseEmitter execute(@CookieValue(value = "fSId", required = false) String upfSId, @RequestBody AppReq appReq, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        //用户必须存在
        Assert.notNull(endUserService.checkUser(upfSId), "用户状态异常，请刷新页面重试");


        SseEmitter emitter = new SseEmitter(60000L);


        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();


        executeReqVO.setSseEmitter(emitter);

        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        BaseAppEntity app = AppFactory.factory(executeReqVO);

        app.aexecute(executeReqVO);

        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), emitter);
        return emitter;
    }


}
