package com.starcloud.ops.business.user.controller.admin.tag;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigRespVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.convert.tag.MemberTagConvert;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import com.starcloud.ops.business.user.service.tag.AdminUserTagConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员标签")
@RestController
@RequestMapping("/llm/member/tag")
@Validated
public class AdminUserTagConfigController {

    @Resource
    private AdminUserTagConfigService adminUserTagConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建会员标签")
    @PreAuthorize("@ss.hasPermission('member:tag:create')")
    public CommonResult<Long> createTag(@Valid @RequestBody AdminUserTagConfigCreateReqVO createReqVO) {
        return success(adminUserTagConfigService.createTag(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员标签")
    @PreAuthorize("@ss.hasPermission('member:tag:update')")
    public CommonResult<Boolean> updateTag(@Valid @RequestBody AdminUserTagConfigUpdateReqVO updateReqVO) {
        adminUserTagConfigService.updateTag(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员标签")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:tag:delete')")
    public CommonResult<Boolean> deleteTag(@RequestParam("id") Long id) {
        adminUserTagConfigService.deleteTag(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员标签")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:tag:query')")
    public CommonResult<AdminUserTagConfigRespVO> getMemberTag(@RequestParam("id") Long id) {
        AdminUserTagConfigDO tag = adminUserTagConfigService.getTag(id);
        return success(MemberTagConvert.INSTANCE.convert(tag));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取会员标签精简信息列表", description = "只包含被开启的会员标签，主要用于前端的下拉选项")
    public CommonResult<List<AdminUserTagConfigRespVO>> getSimpleTagList() {
        // 获用户列表，只要开启状态的
        List<AdminUserTagConfigDO> list = adminUserTagConfigService.getTagList();
        // 排序后，返回给前端
        return success(MemberTagConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list")
    @Operation(summary = "获得会员标签列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('member:tag:query')")
    public CommonResult<List<AdminUserTagConfigRespVO>> getMemberTagList(@RequestParam("ids") Collection<Long> ids) {
        List<AdminUserTagConfigDO> list = adminUserTagConfigService.getTagList(ids);
        return success(MemberTagConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会员标签分页")
    @PreAuthorize("@ss.hasPermission('member:tag:query')")
    public CommonResult<PageResult<AdminUserTagConfigRespVO>> getTagPage(@Valid AdminUserTagConfigPageReqVO pageVO) {
        PageResult<AdminUserTagConfigDO> pageResult = adminUserTagConfigService.getTagPage(pageVO);
        return success(MemberTagConvert.INSTANCE.convertPage(pageResult));
    }

}
