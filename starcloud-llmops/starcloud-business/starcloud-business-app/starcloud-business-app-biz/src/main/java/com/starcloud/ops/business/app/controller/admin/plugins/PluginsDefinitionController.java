package com.starcloud.ops.business.app.controller.admin.plugins;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.AppBindPluginRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.BotListInfo;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceListInfo;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
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

    @Resource
    private PluginsService pluginsService;


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
    public CommonResult<PluginRespVO> detail(@PathVariable("uid") String uid) {
        return CommonResult.success(pluginsDefinitionService.detail(uid));
    }

    @GetMapping(value = "/published")
    @Operation(summary = "已发布插件列表")
    public CommonResult<List<PluginRespVO>> publishedList() {
        return CommonResult.success(pluginsDefinitionService.publishedList());
    }

    @GetMapping(value = "/ownerList")
    @Operation(summary = "owner插件列表")
    public CommonResult<List<PluginRespVO>> ownerList() {
        return CommonResult.success(pluginsDefinitionService.ownerList());
    }

    @PostMapping(value = "/list")
    @Operation(summary = "插件列表")
    public CommonResult<List<PluginRespVO>> list(@RequestBody @Valid PluginListReqVO reqVO) {
        return CommonResult.success(pluginsDefinitionService.list(reqVO));
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
    public CommonResult<BotListInfo> BotList(@RequestParam("spaceId") String spaceId,
                                             @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                             @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                             @RequestParam("accessTokenId") String accessTokenId) {
        return CommonResult.success(pluginsDefinitionService.spaceBot(spaceId, accessTokenId, pageSize, pageIndex));
    }

    @GetMapping(value = "/spaceList")
    @Operation(summary = "space列表")
    public CommonResult<SpaceListInfo> spaceBots(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                 @RequestParam("accessTokenId") String accessTokenId) {
        return CommonResult.success(pluginsDefinitionService.spaceList(accessTokenId, pageSize, pageIndex));
    }

    @PostMapping(value = "/verify")
    @Operation(summary = "验证")
    public CommonResult<String> verify(@RequestBody @Valid PluginTestReqVO reqVO) {
        return CommonResult.success(pluginsService.verify(reqVO));
    }

    @PostMapping(value = "/verifyResult")
    @Operation(summary = "验证结果")
    @OperateLog(enable = false)
    public CommonResult<VerifyResult> verifyResult(@RequestBody @Valid PluginTestResultReqVO resultReqVO) {
        return CommonResult.success(pluginsService.verifyResult(resultReqVO));
    }

    @PostMapping(value = "/bindPlugin")
    @Operation(summary = "应用绑定的插件")
    @OperateLog(enable = false)
    public CommonResult<AppBindPluginRespVO> bindPlugin(@RequestBody @Valid AppBindPluginReqVO resultReqVO) {
        return CommonResult.success(pluginsService.bindPlugin(resultReqVO));
    }

}
