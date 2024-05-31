package com.starcloud.ops.biz.controller.admin.element;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementPageReqVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementRespVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.element.ElementDO;
import com.starcloud.ops.biz.service.element.ElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 海报元素")
@RestController
@RequestMapping("/poster/element")
@Validated
public class ElementController {

    @Resource
    private ElementService elementService;

    @PostMapping("/create")
    @Operation(summary = "创建海报元素")
    public CommonResult<ElementRespVO> createElement(@Valid @RequestBody ElementSaveReqVO createReqVO) {
        return success(elementService.createElement(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海报元素")
    public CommonResult<Boolean> updateElement(@Valid @RequestBody ElementSaveReqVO updateReqVO) {
        elementService.updateElement(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海报元素")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> deleteElement(@RequestParam("uid") String uid) {
        elementService.deleteElement(uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海报元素")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<ElementRespVO> getElement(@RequestParam("uid") String uid) {
        ElementDO element = elementService.getElement(uid);
        return success(BeanUtils.toBean(element, ElementRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得海报元素分页")
    public CommonResult<PageResult<ElementRespVO>> getElementPage(@Valid ElementPageReqVO pageReqVO) {
        PageResult<ElementDO> pageResult = elementService.getElementPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ElementRespVO.class));
    }

}