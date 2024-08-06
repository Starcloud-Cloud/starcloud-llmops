package com.starcloud.ops.business.app.controller.admin.plugins;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginTestReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginTestRespVO;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceBot;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceInfo;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm/plugin/definition")
@Tag(name = "星河云海-创作插件定义", description = "创作插件定义")
public class PluginsDefinitionController {

    @Resource
    private PluginsDefinitionService pluginsDefinitionService;


    @GetMapping("/metadata")
    @Operation(summary = "元数据", description = "元数据")
    public CommonResult<Map<String, Object>> metadata() {
        return CommonResult.success(pluginsDefinitionService.metadata());
    }

    @PostMapping(value = "/create")
    @Operation(summary = "新增插件")
    public CommonResult<PluginRespVO> create(@RequestBody @Valid PluginDefinitionVO pluginVO) {
        return CommonResult.success(pluginsDefinitionService.create(pluginVO));
    }

    @PostMapping(value = "/detail/{uid}")
    @Operation(summary = "插件详情")
    public CommonResult<PluginRespVO> create(@PathVariable("uid") String uid) {
        return CommonResult.success(pluginsDefinitionService.detail(uid));
    }

    @GetMapping(value = "/published")
    @Operation(summary = "已发布插件列表")
    public CommonResult<List<PluginRespVO>> publishedList() {
        return CommonResult.success(pluginsDefinitionService.publishedList());
    }

    @GetMapping(value = "/ownerList")
    @Operation(summary = "未发布插件列表")
    public CommonResult<List<PluginRespVO>> ownerList() {
        return CommonResult.success(pluginsDefinitionService.ownerList());
    }

    @GetMapping(value = "/publish/{uid}")
    @Operation(summary = "发布插件")
    public CommonResult<Boolean> publish(@PathVariable("uid") String uid) {
        pluginsDefinitionService.publish(uid);
        return CommonResult.success(true);
    }

    @PutMapping(value = "/modify")
    @Operation(summary = "修改插件配置")
    public CommonResult<PluginRespVO> modifyPlugin(@RequestBody @Valid PluginConfigModifyReqVO reqVO) {
        return CommonResult.success(pluginsDefinitionService.modifyPlugin(reqVO));
    }

    @DeleteMapping(value = "/delete/{uid}")
    @Operation(summary = "删除插件")
    public CommonResult<Boolean> deletePlugin(@PathVariable("uid") String uid) {
        pluginsDefinitionService.delete(uid);
        return CommonResult.success(true);
    }

    @GetMapping(value = "/botInfo")
    @Operation(summary = "bot信息")
    public CommonResult<CozeBotInfo> botInfo(@RequestParam("botId") String botId, @RequestParam("accessTokenId") String accessTokenId) {
        return CommonResult.success(pluginsDefinitionService.botInfo(botId, accessTokenId));
    }

    @GetMapping(value = "/spaceBots")
    @Operation(summary = "bot列表")
    public CommonResult<SpaceInfo> spaceBots(@RequestParam("spaceId") String spaceId,
                                             @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                             @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                             @RequestParam("accessTokenId") String accessTokenId) {
        return CommonResult.success(pluginsDefinitionService.spaceBot(spaceId, accessTokenId,pageSize,pageIndex));
    }

    @PostMapping(value = "/verify")
    @Operation(summary = "验证")
    public CommonResult<PluginTestRespVO> verify(@RequestBody @Valid PluginTestReqVO reqVO) {
        return CommonResult.success(pluginsDefinitionService.verify(reqVO));
    }

}
