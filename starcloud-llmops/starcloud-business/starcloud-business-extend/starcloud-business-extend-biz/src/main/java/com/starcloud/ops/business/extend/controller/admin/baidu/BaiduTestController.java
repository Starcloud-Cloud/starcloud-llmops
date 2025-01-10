package com.starcloud.ops.business.extend.controller.admin.baidu;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.extend.service.BaiduPushServiceImpl;
import com.starcloud.ops.business.extend.service.dto.baidu.PushResourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
@RestController
@RequestMapping("/llm/extend")

@Tag(name = "百度数据测试", description = "百度数据测试")
@Validated
public class BaiduTestController {

    @Resource
    private BaiduPushServiceImpl baiduPushClient;

    @PostMapping("/push")
    @Operation(summary = "百度资源推送")
    public CommonResult<Boolean> push(@Valid @RequestBody List<PushResourceDTO> resourceDTOS) throws Exception {
        baiduPushClient.push(resourceDTOS);
        return success(Boolean.TRUE);
    }

}