package com.starcloud.ops.business.app.controller.admin.plugins;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.idempotent.core.annotation.Idempotent;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/plugin/config")
@Tag(name = "星河云海-创作插件配置", description = "创作插件配置")
public class PluginConfigController {

    @Resource
    private PluginConfigService pluginConfigService;


    @PostMapping(value = "/create")
    @Operation(summary = "新增插件配置")
    @Idempotent
    public CommonResult<PluginConfigRespVO> create(@RequestBody @Valid PluginConfigVO pluginVO) {
        return CommonResult.success(pluginConfigService.create(pluginVO));
    }

    @GetMapping(value = "/detail")
    @Operation(summary = "素材库插件配置")
    public CommonResult<PluginConfigRespVO> detail(@RequestParam("libraryUid") String libraryUid,
                                                   @RequestParam("pluginUid") String pluginUid) {
        return CommonResult.success(pluginConfigService.getByLibrary(libraryUid, pluginUid));
    }

    @PostMapping(value = "/modify")
    @Operation(summary = "修改插件配置")
    public CommonResult<Boolean> modify(@RequestBody @Valid PluginConfigReqVO pluginVO) {
        pluginConfigService.modify(pluginVO);
        return CommonResult.success(true);
    }

    @DeleteMapping(value = "/delete/{uid}")
    @Operation(summary = "删除素材库插件")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        pluginConfigService.delete(uid, false);
        return CommonResult.success(true);
    }
}
