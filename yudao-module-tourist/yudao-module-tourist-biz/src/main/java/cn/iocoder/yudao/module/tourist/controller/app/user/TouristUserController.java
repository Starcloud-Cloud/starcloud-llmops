package cn.iocoder.yudao.module.tourist.controller.app.user;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.tourist.controller.app.user.vo.AppUserInfoRespVO;
import cn.iocoder.yudao.module.tourist.controller.app.user.vo.AppUserUpdateMobileReqVO;
import cn.iocoder.yudao.module.tourist.convert.user.UserConvert;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import cn.iocoder.yudao.module.tourist.service.user.TouristService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;

@Tag(name = "用户 APP - 用户个人中心")
@RestController
@RequestMapping("/tourist/user")
@Validated
@Slf4j
public class TouristUserController {

    @Resource
    private TouristService userService;

    @PutMapping("/update-nickname")
    @Operation(summary = "修改用户昵称")
    @PreAuthenticated
    public CommonResult<Boolean> updateUserNickname(@RequestParam("nickname") String nickname) {
        userService.updateUserNickname(getLoginUserId(), nickname);
        return success(true);
    }

    @PostMapping("/update-avatar")
    @Operation(summary = "修改用户头像")
    @PreAuthenticated
    public CommonResult<String> updateUserAvatar(@RequestParam("avatarFile") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw exception(FILE_IS_EMPTY);
        }
        String avatar = userService.updateUserAvatar(getLoginUserId(), file.getInputStream());
        return success(avatar);
    }

    @GetMapping("/get")
    @Operation(summary = "获得基本信息")
    @PreAuthenticated
    public CommonResult<AppUserInfoRespVO> getUserInfo() {
        TouristDO user = userService.getUser(getLoginUserId());
        return success(UserConvert.INSTANCE.convert(user));
    }

    @PostMapping("/update-mobile")
    @Operation(summary = "修改用户手机")
    @PreAuthenticated
    public CommonResult<Boolean> updateMobile(@RequestBody @Valid AppUserUpdateMobileReqVO reqVO) {
        userService.updateUserMobile(getLoginUserId(), reqVO);
        return success(true);
    }

}

