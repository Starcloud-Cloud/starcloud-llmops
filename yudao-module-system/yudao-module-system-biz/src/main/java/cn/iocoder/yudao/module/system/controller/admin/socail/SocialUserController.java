package cn.iocoder.yudao.module.system.controller.admin.socail;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.SocialUserBindReqVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.SocialUserUnbindReqVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserRespVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserUpdateSimpleReqVO;
import cn.iocoder.yudao.module.system.convert.social.SocialUserConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 社交用户")
@RestController
@RequestMapping("/system/social-user")
@Validated
public class SocialUserController {

    @Resource
    private SocialUserService socialUserService;

    @PostMapping("/bind")
    @Operation(summary = "社交绑定，使用 code 授权码")
    public CommonResult<Boolean> socialBind(@RequestBody @Valid SocialUserBindReqVO reqVO) {
        socialUserService.bindSocialUser(SocialUserConvert.INSTANCE.convert(getLoginUserId(), UserTypeEnum.ADMIN.getValue(), reqVO));
        return CommonResult.success(true);
    }

    @DeleteMapping("/unbind")
    @Operation(summary = "取消社交绑定")
    public CommonResult<Boolean> socialUnbind(@RequestBody SocialUserUnbindReqVO reqVO) {
        socialUserService.unbindSocialUser(getLoginUserId(), UserTypeEnum.ADMIN.getValue(), reqVO.getType(), reqVO.getOpenid());
        return CommonResult.success(true);
    }

    // ==================== 社交用户 CRUD ====================

    @GetMapping("/get")
    @Operation(summary = "获得社交用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SocialUserRespVO> getSocialUser(@RequestParam("id") Long id) {
        SocialUserDO socialUser = socialUserService.getSocialUser(id);
        return success(SocialUserConvert.INSTANCE.convert(socialUser));
    }

    @GetMapping("/get-new")
    @Operation(summary = "获得社交用户-获取最新 token")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<SocialUserRespVO> getNewSocialUser(@RequestParam("id") Long id) {
        SocialUserDO socialUser = socialUserService.getNewSocialUser(id);
        return success(SocialUserConvert.INSTANCE.convert(socialUser));
    }

    @GetMapping("/page")
    @Operation(summary = "获得社交用户分页")
    public CommonResult<PageResult<SocialUserRespVO>> getSocialUserPage(@Valid SocialUserPageReqVO pageVO) {
        return success(socialUserService.getSocialUserPage2(pageVO));
    }


    @PostMapping("/update-nickname")
    @Operation(summary = "更新社交用户昵称")
    public CommonResult<Boolean> getSocialUserPage(@RequestBody @Valid SocialUserUpdateSimpleReqVO simpleReqVO) {
         socialUserService.updateNickname(simpleReqVO);
        return success(true);
    }
}
