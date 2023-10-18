package com.starcloud.ops.business.listing.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/listing/dict")
@Tag(name = "listing词库管理", description = "listing词库管理")
public class ListingDictController {

    @Resource
    private DictService dictService;

    @PutMapping("/create")
    @Operation(summary = "新增词库", description = "新增词库")
    public CommonResult<DictRespVO> create(@Valid @RequestBody DictCreateReqVO reqVO) {
        DictRespVO dictRespVO = dictService.create(reqVO);
        return CommonResult.success(dictRespVO);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询词库列表", description = "分页查询词库列表")
    public CommonResult<PageResult<DictRespVO>> page(@Valid DictPageReqVO dictPageReqVO) {
        PageResult<DictRespVO> resp = dictService.getDictPage(dictPageReqVO);
        return CommonResult.success(resp);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改词库", description = "修改词库")
    public CommonResult<Boolean> modify(@Valid @RequestBody DictModifyReqVO modifyReqVO) {
        dictService.modify(modifyReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除词库", description = "批量删除词库")
    public CommonResult<Boolean> delete(@RequestParam("uids") List<String> uids) {
        dictService.deleteDict(uids);
        return CommonResult.success(true);
    }

    @GetMapping("/detail/{uid}")
    @Operation(summary = "词库详情", description = "词库详情")
    public CommonResult<DictRespVO> dictDetail(@PathVariable("uid") String uid) {
        return CommonResult.success(dictService.dictDetail(uid));
    }
}
