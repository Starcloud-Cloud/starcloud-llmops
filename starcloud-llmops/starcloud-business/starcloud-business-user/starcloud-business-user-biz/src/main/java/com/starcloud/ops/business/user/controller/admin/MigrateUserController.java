package com.starcloud.ops.business.user.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.starcloud.ops.business.user.pojo.dto.WpUserDTO;
import com.starcloud.ops.business.user.pojo.dto.MigrateResultDTO;
import com.starcloud.ops.business.user.service.MigrateUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/llm/migrate")
@Tag(name = "星河云海-wp用户迁移")
public class MigrateUserController {

    @Autowired
    private MigrateUserService migrateUserService;

    @PostMapping("/import")
    @Operation(summary = "导入用户")
    @PreAuthorize("@ss.hasRole('super_admin')")
    @TenantIgnore
    public CommonResult<List<MigrateResultDTO>> importExcel(@RequestParam("file") MultipartFile file) throws Exception {

        List<WpUserDTO> wpUserDTOS = ExcelUtils.read(file, WpUserDTO.class);
        return  CommonResult.success(migrateUserService.migrateUsers(wpUserDTOS));
    }
}
