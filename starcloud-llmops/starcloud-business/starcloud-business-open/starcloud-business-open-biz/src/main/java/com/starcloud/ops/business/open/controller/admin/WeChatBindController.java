package com.starcloud.ops.business.open.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.mp.controller.admin.account.vo.MpAccountRespVO;
import cn.iocoder.yudao.module.mp.convert.account.MpAccountConvert;
import cn.iocoder.yudao.module.mp.dal.dataobject.account.MpAccountDO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WechatWebChannelReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WeChatBindReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WeChatBindRespVO;
import com.starcloud.ops.business.open.service.WechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/delete/{uid}")
    @Operation(summary = "删除绑定微信群", description = "删除绑定微信群")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        wechatService.delete(uid);
        return CommonResult.success(true);
    }

    @PostMapping("/modify/{uid}")
    @Operation(summary = "修改绑定的微信群", description = "修改绑定的微信群")
    public CommonResult<Boolean> modify(@PathVariable("uid") String uid, @RequestBody @Valid WeChatBindReqVO reqVO) {
        wechatService.modify(uid, reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "公共号自动回复/菜单", description = "公共号自动回复/菜单")
    public CommonResult<Boolean> createWebChannel(@Valid @RequestBody WechatWebChannelReqVO req) {
        wechatService.createWebChannel(req);
        return CommonResult.success(true);
    }

}
