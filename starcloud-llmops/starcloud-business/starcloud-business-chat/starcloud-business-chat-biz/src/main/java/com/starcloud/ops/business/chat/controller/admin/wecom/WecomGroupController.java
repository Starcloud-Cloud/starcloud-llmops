package com.starcloud.ops.business.chat.controller.admin.wecom;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.request.WecomCreateGroupReqVO;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.response.WecomGroupRespVO;
import com.starcloud.ops.business.chat.service.WecomGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/llm/wecom")
@RestController
@Tag(name = "星河云海 - 企业微信群管理")
@Slf4j
public class WecomGroupController {

    @Resource
    private WecomGroupService groupService;

    @PostMapping("/group/create")
    @Operation(summary = "创建群聊", description = "创建群聊并绑定会话")
    public CommonResult<Boolean> createGroup(@RequestBody @Valid WecomCreateGroupReqVO reqVO) {
        groupService.initGroup(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/group/list/{appUid}")
    @Operation(summary = "查询所有的群聊", description = "查询所有的群聊")
    public CommonResult<List<WecomGroupRespVO>> listGroup(@PathVariable("appUid")String appUid) {
        List<WecomGroupRespVO> wecomGroupRespVOS = groupService.listGroupDetail(appUid);
        return CommonResult.success(wecomGroupRespVOS);
    }

}
