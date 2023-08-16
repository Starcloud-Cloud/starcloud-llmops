package com.starcloud.ops.business.app.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;

@Tag(name = "星河云海 - 对话-技能")
@RestController
@RequestMapping("/llm/chatskill")
public class ChatSkillController {

    @Autowired
    private ChatSkillService chatSkillService;



    @Operation(summary = "默认技能列表")
    @GetMapping("/list")
    public CommonResult<List<ChatSkillVO>> list(@RequestParam(value = "appUid") String appUid) {

        List<ChatSkillVO> chatSkillVOS = chatSkillService.listSkill();

        return CommonResult.success(chatSkillVOS);
    }



}
