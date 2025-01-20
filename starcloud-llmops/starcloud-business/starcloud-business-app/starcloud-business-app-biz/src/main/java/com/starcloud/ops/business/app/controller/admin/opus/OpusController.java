package com.starcloud.ops.business.app.controller.admin.opus;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.controller.admin.opus.vo.*;
import com.starcloud.ops.business.app.service.opus.OpusBindService;
import com.starcloud.ops.business.app.service.opus.OpusDirectoryService;
import com.starcloud.ops.business.app.service.opus.OpusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/llm/opus")
@Tag(name = "作品集", description = "作品集")
public class OpusController {

    @Resource
    private OpusService opusService;

    @Resource
    private OpusDirectoryService dirService;

    @Resource
    private OpusBindService opusBindService;


    @PostMapping("/create")
    @Operation(summary = "新建作品集", description = "新建作品集")
    @ApiOperationSupport(order = 1)
    public CommonResult<OpusRespVO> create(@Valid @RequestBody OpusBaseVO opusBaseVO) {
        return CommonResult.success(opusService.create(opusBaseVO));
    }

    @DeleteMapping("/delete/{opusUid}")
    @Operation(summary = "删除作品集", description = "删除作品集")
    @ApiOperationSupport(order = 2)
    public CommonResult<Boolean> deleteOpus(@PathVariable("opusUid") String opusUid) {
        opusService.delete(opusUid);
        return CommonResult.success(true);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改作品集", description = "修改作品集")
    @ApiOperationSupport(order = 3)
    public CommonResult<Boolean> modifyOpus(@Valid @RequestBody OpusModifyReqVO reqVO) {
        opusService.modify(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询作品集", description = "分页查询作品集")
    @ApiOperationSupport(order = 4)
    public CommonResult<PageResult<OpusRespVO>> page(@Valid @RequestBody PageParam pageParam) {
        return CommonResult.success(opusService.page(pageParam));
    }


    @GetMapping("/tree/{opusUid}")
    @Operation(summary = "作品集目录树", description = "作品集目录树")
    @ApiOperationSupport(order = 5)
    public CommonResult<List<DirectoryNodeVO>> opusNodeTree(@PathVariable("opusUid") String opusUid) {
        return CommonResult.success(dirService.opusNodeTree(opusUid));
    }

    // 作品集目录

    @PostMapping("/dir/add")
    @Operation(summary = "新建作品集目录", description = "新建作品集目录")
    @ApiOperationSupport(order = 20)
    public CommonResult<DirectoryNodeVO> createDir(@Valid @RequestBody OpusDirBaseVO dirBaseVO) {
        return CommonResult.success(opusService.createDir(dirBaseVO));
    }

    @PutMapping("/dir/modify")
    @Operation(summary = "修改作品集目录", description = "修改作品集目录")
    @ApiOperationSupport(order = 21)
    public CommonResult<Boolean> modifyOpusDir(@Valid @RequestBody OpusDirModifyReqVO reqVO) {
        dirService.modify(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/dir/delete/{dirUid}")
    @Operation(summary = "删除作品集目录", description = "删除作品集目录")
    @ApiOperationSupport(order = 22)
    public CommonResult<Boolean> deleteOpusDir(@PathVariable("dirUid") String dirUid) {
        dirService.delete(dirUid);
        return CommonResult.success(true);
    }

    // 作品集创作内容

    @PostMapping("/bind/add")
    @Operation(summary = "绑定创作内容", description = "绑定创作内容")
    @ApiOperationSupport(order = 30)
    public CommonResult<OpusBindRespVO> bindCreative(@Valid @RequestBody OpusBindBaseVO bindReqVO) {
        return CommonResult.success(opusService.addBind(bindReqVO));
    }

    @DeleteMapping("/bind/delete/{bindUid}")
    @Operation(summary = "删除创作内容绑定", description = "删除创作内容绑定")
    @ApiOperationSupport(order = 31)
    public CommonResult<Boolean> deleteBind(@PathVariable("bindUid") String bindUid) {
        opusBindService.delete(bindUid);
        return CommonResult.success(true);
    }

    @PostMapping("/bind/page")
    @Operation(summary = "分页查询创作内容", description = "分页查询创作内容")
    @ApiOperationSupport(order = 32)
    public CommonResult<PageResult<OpusBindRespVO>> page(@Valid @RequestBody OpusBindPageReqVO pageParam) {
        return CommonResult.success(opusBindService.page(pageParam));
    }

    @PostMapping("/bind/modify")
    @ApiOperationSupport(order = 33)
    @Operation(summary = "修改绑定", description = "修改绑定")
    public void modifyBind() {

    }

}
