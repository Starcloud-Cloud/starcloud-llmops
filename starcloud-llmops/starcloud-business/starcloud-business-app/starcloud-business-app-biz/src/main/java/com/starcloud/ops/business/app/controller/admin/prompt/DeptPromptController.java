package com.starcloud.ops.business.app.controller.admin.prompt;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.DeptPromptModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.PromptPageReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.resp.PromptRespVO;
import com.starcloud.ops.business.app.service.prompt.DeptPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/app/prompt")
@Tag(name = "星河云海-团队提示词", description = "团队提示词")
public class DeptPromptController {

    @Resource
    private DeptPromptService deptPromptService;

    @PostMapping(value = "/dept/create")
    @Operation(summary = "新增提示词")
    public CommonResult<PromptRespVO> create(@RequestBody @Valid PromptBaseVO promptBaseVO) {
        return CommonResult.success(deptPromptService.create(promptBaseVO));
    }

    @PostMapping(value = "/dept/modify")
    @Operation(summary = "修改提示词")
    public CommonResult<PromptRespVO> modify(@RequestBody @Valid DeptPromptModifyReqVO reqVO) {
        return CommonResult.success(deptPromptService.modify(reqVO));
    }

    @DeleteMapping(value = "/dept/delete/{uid}")
    @Operation(summary = "删除提示词")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        deptPromptService.delete(uid);
        return CommonResult.success(true);
    }

    @PostMapping(value = "/dept/page")
    @Operation(summary = "分页查询团队提示词")
    public CommonResult<PageResult<PromptRespVO>> page(@RequestBody @Valid PromptPageReqVO reqVO) {
        return CommonResult.success(deptPromptService.page(reqVO));
    }


    @PostMapping(value = "/sys/page")
    @Operation(summary = "分页查询推荐提示词")
    public CommonResult<PageResult<PromptRespVO>> sysPage(@RequestBody @Valid PromptPageReqVO reqVO) {
        return CommonResult.success(deptPromptService.sysPage(reqVO));
    }
}
