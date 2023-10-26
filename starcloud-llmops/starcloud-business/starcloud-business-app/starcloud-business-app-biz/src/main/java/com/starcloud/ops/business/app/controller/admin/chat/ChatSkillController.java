package com.starcloud.ops.business.app.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.AllChatSkilVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "星河云海 - 对话-技能")
@RestController
@RequestMapping("/llm/chatskill")
public class ChatSkillController {

    @Autowired
    private ChatSkillService chatSkillService;

    @Operation(summary = "默认技能列表")
    @GetMapping("/list")
    public CommonResult<List<ChatSkillVO>> list() {

        List<ChatSkillVO> chatSkillVOS = chatSkillService.listSkill();

        return CommonResult.success(chatSkillVOS);
    }

    @Operation(summary = "所有技能列表")
    @GetMapping("/all")
    public CommonResult<AllChatSkilVO> all() {
        AllChatSkilVO chatSkill = chatSkillService.allChatSkill();
        return CommonResult.success(chatSkill);
    }



}
