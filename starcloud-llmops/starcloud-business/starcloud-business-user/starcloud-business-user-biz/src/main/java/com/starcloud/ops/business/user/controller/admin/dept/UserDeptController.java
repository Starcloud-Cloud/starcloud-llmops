package com.starcloud.ops.business.user.controller.admin.dept;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.UserDeptUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptUserRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import com.starcloud.ops.business.user.pojo.dto.PermissionOption;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Tag(name = "用户部门管理")
@RestController
@RequestMapping("/llm/space")
@DataPermission(enable = false)
public class UserDeptController {

    @Resource
    private UserDeptService userDeptService;

    @GetMapping("/metadata")
    @Operation(summary = "查询元数据", description = "查询元数据")
    public CommonResult<List<PermissionOption>> metadata() {
        return CommonResult.success(UserDeptRoleEnum.options());
    }

    @GetMapping("/dept/list")
    @Operation(summary = "登录用户所在部门列表")
    public CommonResult<List<UserDeptRespVO>> deptList() {
        List<UserDeptRespVO> result = userDeptService.deptList();
        return CommonResult.success(result);
    }

    @PutMapping("/checkout/{deptId}")
    @Operation(summary = "切换部门")
    public CommonResult<Boolean> checkout(@PathVariable("deptId") Long deptId) {
        userDeptService.checkout(deptId);
        return CommonResult.success(true);
    }

    @GetMapping("/userList/{deptId}")
    @Operation(summary = "部门下用户列表")
    public CommonResult<List<DeptUserRespVO>> userList(@PathVariable("deptId") Long deptId) {
        List<DeptUserRespVO> result = userDeptService.userList(deptId);
        return CommonResult.success(result);
    }

    @GetMapping("/detail/{deptId}")
    @Operation(summary = "部门详细信息")
    public CommonResult<DeptRespVO> getDept(@PathVariable("deptId") Long deptId) {
        DeptRespVO detail = userDeptService.deptDetail(deptId);
        return CommonResult.success(detail);
    }

    @PutMapping("/update")
    @Operation(summary = "更新部门信息")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody UserDeptUpdateReqVO reqVO) {
        userDeptService.updateDept(reqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/join/{inviteCode}")
    @Operation(summary = "加入部门")
    public CommonResult<Boolean> joinDept(@PathVariable("inviteCode") String inviteCode) {
        userDeptService.joinDept(inviteCode);
        return CommonResult.success(true);
    }

    @DeleteMapping("/remove/{userDeptId}")
    @Operation(summary = "移除用户")
    public CommonResult<Boolean> removeUser(@PathVariable("userDeptId") Long userDeptId) {
        userDeptService.removeUser(userDeptId);
        return CommonResult.success(true);
    }

    @GetMapping("/simple/{inviteCode}")
    @Operation(summary = "部门精简信息")
    @PermitAll
    @OperateLog(enable = false)
    public CommonResult<UserDeptRespVO> getSimpleDept(@PathVariable("inviteCode") String inviteCode) {
        UserDeptRespVO respVO = userDeptService.getSimpleDept(inviteCode);
        return CommonResult.success(respVO);
    }


    // 修改角色
    @PutMapping("/role/{userDeptId}/{role}")
    @Operation(summary = "修改角色")
    public CommonResult<Boolean> updateRole(@PathVariable("userDeptId") Long userDeptId,@PathVariable("role") Integer role) {
        userDeptService.updateRole(userDeptId,role);
        return CommonResult.success(true);
    }

    @PutMapping("/dept/create")
    @Operation(summary = "创建新部门")
    public CommonResult<Boolean> create(@Valid @RequestBody CreateDeptReqVO reqVO) {
        userDeptService.createDept(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/permission")
    @Operation(summary = "用户部门空间权限点")
    public CommonResult<Set<String>> permission() {
        Set<String> result = userDeptService.getUserPermission();
        return CommonResult.success(result);
    }

}
