package com.starcloud.ops.biz.controller.admin.templatetype;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeRespVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.templatetype.TemplatetypeDO;
import com.starcloud.ops.biz.service.templatetype.TemplateTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 海报模板类型")
@RestController
@RequestMapping("/poster/templatetype")
@Validated
public class TemplateTypeController {

    @Resource
    private TemplateTypeService templateTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建海报模板类型")
    public CommonResult<TemplateTypeRespVO> createTemplatetype(@Valid @RequestBody TemplateTypeSaveReqVO createReqVO) {
        return success(templateTypeService.createTemplatetype(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海报模板类型")
    public CommonResult<Boolean> updateTemplatetype(@Valid @RequestBody TemplateTypeSaveReqVO updateReqVO) {
        templateTypeService.updateTemplatetype(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海报模板类型")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> deleteTemplatetype(@RequestParam("uid") String uid) {
        templateTypeService.deleteTemplatetype(uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海报模板类型")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<TemplateTypeRespVO> getTemplatetype(@RequestParam("uid") String uid) {
        TemplatetypeDO templatetype = templateTypeService.getTemplatetype(uid);
        return success(BeanUtils.toBean(templatetype, TemplateTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得海报模板类型分页")
    public CommonResult<PageResult<TemplateTypeRespVO>> getTemplatetypePage(@Valid TemplateTypePageReqVO pageReqVO) {
        PageResult<TemplatetypeDO> pageResult = templateTypeService.getTemplatetypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TemplateTypeRespVO.class));
    }

}