package com.starcloud.ops.business.app.controller.admin.xhs.material;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.TemplateRecordRespVO;
import com.starcloud.ops.business.app.service.template.TemplateRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/llm/template/record")
@Tag(name = "模板使用记录", description = "模板使用记录")
public class TemplateRecordController {

    @Resource
    private TemplateRecordService recordService;

    @GetMapping("/list")
    @Operation(summary = "模板使用记录", description = "模板使用记录")
    public CommonResult<List<TemplateRecordRespVO>> list() {
        return CommonResult.success(recordService.listRecord());
    }
}
