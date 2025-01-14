package com.starcloud.ops.business.app.controller.admin.opus;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;
import com.starcloud.ops.business.app.service.opus.OpusDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/llm/opus/dir")
@Tag(name = "作品集目录", description = "作品集目录")
public class OpusDirectoryController {

    @Resource
    private OpusDirectoryService dirService;

    @GetMapping("/tree/{opusUid}")
    @Operation(summary = "作品集目录树", description = "作品集目录树")
    public CommonResult<List<DirectoryNodeVO>> opusNodeTree(@PathVariable("opusUid") String opusUid) {
        return CommonResult.success(dirService.opusNodeTree(opusUid));
    }
}
