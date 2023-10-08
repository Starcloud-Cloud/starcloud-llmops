package com.starcloud.ops.business.open.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.open.controller.admin.vo.request.AddFriendReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WecomGroupRespVO;
import com.starcloud.ops.business.open.service.WecomGroupService;
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

    @PostMapping("/group/list/{appUid}")
    @Operation(summary = "查询所有的群聊", description = "查询所有的群聊")
    public CommonResult<List<WecomGroupRespVO>> listGroup(@PathVariable("appUid")String appUid) {
        List<WecomGroupRespVO> wecomGroupRespVOS = groupService.listGroupDetail(appUid);
        return CommonResult.success(wecomGroupRespVOS);
    }

    @PostMapping("/add/friend")
    @Operation(summary = "添加好友", description = "添加企业微信好友")
    public CommonResult<Boolean> addFriend(@RequestBody @Valid AddFriendReqVO reqVO) {
        groupService.addFriend(reqVO);
        return CommonResult.success(true);
    }


}
