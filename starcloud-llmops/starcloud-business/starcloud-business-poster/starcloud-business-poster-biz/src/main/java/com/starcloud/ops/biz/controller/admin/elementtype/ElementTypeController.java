package com.starcloud.ops.biz.controller.admin.elementtype;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeRespVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.elementtype.ElementtypeDO;
import com.starcloud.ops.biz.service.elementtype.ElementTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 海报元素类型")
@RestController
@RequestMapping("/poster/elementtype")
@Validated
public class ElementTypeController {

    @Resource
    private ElementTypeService elementTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建海报元素类型")
    public CommonResult<ElementTypeRespVO> createElementtype(@Valid @RequestBody ElementTypeSaveReqVO createReqVO) {
        return success(elementTypeService.createElementtype(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海报元素类型")
    public CommonResult<Boolean> updateElementtype(@Valid @RequestBody ElementTypeSaveReqVO updateReqVO) {
        elementTypeService.updateElementtype(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海报元素类型")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> deleteElementtype(@RequestParam("uid") String uid) {
        elementTypeService.deleteElementtype(uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海报元素类型")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<ElementTypeRespVO> getElementtype(@RequestParam("uid") String uid) {
        ElementtypeDO elementtype = elementTypeService.getElementtype(uid);
        return success(BeanUtils.toBean(elementtype, ElementTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得海报元素类型分页")
    public CommonResult<PageResult<ElementTypeRespVO>> getElementtypePage(@Valid ElementTypePageReqVO pageReqVO) {
        PageResult<ElementtypeDO> pageResult = elementTypeService.getElementtypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ElementTypeRespVO.class));
    }


}