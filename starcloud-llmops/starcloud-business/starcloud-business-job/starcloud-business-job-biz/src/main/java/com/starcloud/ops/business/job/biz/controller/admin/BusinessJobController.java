package com.starcloud.ops.business.job.biz.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.BusinessJobModifyReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/llm/job/config")
@Tag(name = "星河云海-定时任务", description = "定时任务")
public class BusinessJobController {

    @Resource
    private BusinessJobService businessJobService;

    @GetMapping("/metadata")
    @Operation(summary = "元数据", description = "元数据")
    public CommonResult<Map<String, Object>> metadata() {
        return CommonResult.success(businessJobService.metadata());
    }

    @PostMapping("/create")
    @Operation(summary = "新建定时任务", description = "新建定时任务")
    public CommonResult<BusinessJobRespVO> createJob(@RequestBody @Valid BusinessJobBaseVO businessJobBaseVO) {
        return CommonResult.success(businessJobService.createJob(businessJobBaseVO));
    }

    @PostMapping("/modify")
    @Operation(summary = "修改定时任务", description = "修改定时任务")
    public CommonResult<Boolean> modify(@RequestBody @Valid BusinessJobModifyReqVO reqVO) {
        businessJobService.modify(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除定时任务", description = "删除定时任务")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        businessJobService.delete(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/stop/{uid}")
    @Operation(summary = "暂停定时任务", description = "暂停定时任务")
    public CommonResult<Boolean> stop(@PathVariable("uid") String uid) {
        businessJobService.stop(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/start/{uid}")
    @Operation(summary = "启动定时任务", description = "启动定时任务")
    public CommonResult<Boolean> start(@PathVariable("uid") String uid) {
        businessJobService.start(uid);
        return CommonResult.success(true);
    }

    @GetMapping("/detail/{foreignKey}")
    @Operation(summary = "查询定时任务", description = "查询定时任务")
    public CommonResult<BusinessJobRespVO> detail(@PathVariable("foreignKey") String foreignKey) {
        BusinessJobRespVO respVO = businessJobService.getByForeignKey(foreignKey);
        return CommonResult.success(respVO);
    }

    @GetMapping("/runJob/{uid}")
    @Operation(summary = "执行定时任务", description = "执行定时任务")
    public CommonResult<Boolean> runJob(@PathVariable("uid") String uid) {
        businessJobService.runJob(uid);
        return CommonResult.success(true);
    }


}
