package com.starcloud.ops.business.app.controller.admin.chat;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigReqVO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "星河云海 - 聊天配置")
@RestController
@RequestMapping("/llm/chat/config")
public class ChatExpandConfigController {

    @Resource
    private ChatExpandConfigService configService;

    @GetMapping("/{appConfigId}")
    @Schema(description = "查询聊天配置")
    public CommonResult<Map<Integer, List<ChatExpandConfigRespVO>>> detail(@PathVariable("appConfigId") String appConfigId) {
        return CommonResult.success(configService.getConfig(appConfigId));
    }

    @PostMapping("/create")
    @Schema(description = "新增聊天配置")
    public CommonResult<String> create(@RequestBody @Valid ChatExpandConfigReqVO reqVO) {
        return CommonResult.success(configService.create(reqVO));
    }

    @PostMapping("/modify")
    @Schema(description = "修改聊天配置")
    public CommonResult<Boolean> modify(@RequestBody @Valid ChatExpandConfigReqVO reqVO) {
        configService.modify(reqVO);
        return CommonResult.success(true);
    }


    @DeleteMapping("/delete/{uid}")
    @Schema(description = "修改聊天配置")
    private CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        configService.delete(uid);
        return CommonResult.success(true);
    }


}
