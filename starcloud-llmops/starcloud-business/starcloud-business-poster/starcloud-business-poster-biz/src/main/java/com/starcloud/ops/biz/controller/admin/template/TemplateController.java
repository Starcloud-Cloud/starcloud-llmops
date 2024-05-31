package com.starcloud.ops.biz.controller.admin.template;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplatePageReqVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateRespVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.template.TemplateDO;
import com.starcloud.ops.biz.service.template.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 海报模板")
@RestController
@RequestMapping("/poster/template")
@Validated
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @PostMapping("/create")
    @Operation(summary = "创建海报模板")
    public CommonResult<TemplateRespVO> createTemplate(@Valid @RequestBody TemplateSaveReqVO createReqVO) {
        return success(templateService.createTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海报模板")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody TemplateSaveReqVO updateReqVO) {
        templateService.updateTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海报模板")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> deleteTemplate(@RequestParam("uid") String uid) {
        templateService.deleteTemplate(uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海报模板")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<TemplateRespVO> getTemplate(@RequestParam("uid") String uid) {
        TemplateDO template = templateService.getTemplate(uid);
        return success(BeanUtils.toBean(template, TemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得海报模板分页")
    public CommonResult<PageResult<TemplateRespVO>> getTemplatePage(@Valid TemplatePageReqVO pageReqVO) {
        PageResult<TemplateDO> pageResult = templateService.getTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TemplateRespVO.class));
    }


}