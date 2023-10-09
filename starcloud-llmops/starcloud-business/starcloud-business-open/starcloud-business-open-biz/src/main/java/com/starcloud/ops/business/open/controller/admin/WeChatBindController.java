package com.starcloud.ops.business.open.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.mp.controller.admin.account.vo.MpAccountRespVO;
import cn.iocoder.yudao.module.mp.convert.account.MpAccountConvert;
import cn.iocoder.yudao.module.mp.dal.dataobject.account.MpAccountDO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WeChatBindReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WeChatBindRespVO;
import com.starcloud.ops.business.open.service.WechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RequestMapping("/llm/wechat/bind")
@RestController
@Tag(name = "星河云海 - 公共号管理")
@Slf4j
public class WeChatBindController {

    @Resource
    private WechatService wechatService;

    @PostMapping("/create")
    @Operation(summary = "新建绑定", description = "绑定微信群")
    public CommonResult<WeChatBindRespVO> bindWxAccount(@RequestBody @Valid WeChatBindReqVO reqVO) {
        return CommonResult.success(wechatService.bindWxAccount(reqVO));
    }

    @GetMapping("/get/{appUid}")
    @Operation(summary = "绑定的公众号账号")
    public CommonResult<List<MpAccountRespVO>> getAccount(@PathVariable("appUid") String appUid) {
        List<MpAccountDO> wxAccount = wechatService.getAccount(appUid);
        return success(MpAccountConvert.INSTANCE.convertList(wxAccount));
    }

}
