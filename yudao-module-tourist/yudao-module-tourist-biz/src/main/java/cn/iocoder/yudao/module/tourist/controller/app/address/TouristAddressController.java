package cn.iocoder.yudao.module.tourist.controller.app.address;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.tourist.controller.app.address.vo.AppAddressCreateReqVO;
import cn.iocoder.yudao.module.tourist.controller.app.address.vo.AppAddressRespVO;
import cn.iocoder.yudao.module.tourist.controller.app.address.vo.AppAddressUpdateReqVO;
import cn.iocoder.yudao.module.tourist.convert.address.AddressConvert;
import cn.iocoder.yudao.module.tourist.dal.dataobject.address.AddressDO;
import cn.iocoder.yudao.module.tourist.service.address.TouristAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 用户收件地址")
@RestController
@RequestMapping("/tourist/address")
@Validated
public class TouristAddressController {

    @Resource
    private TouristAddressService touristAddressService;

    @PostMapping("/create")
    @Operation(summary = "创建用户收件地址")
    public CommonResult<Long> createAddress(@Valid @RequestBody AppAddressCreateReqVO createReqVO) {
        return success(touristAddressService.createAddress(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户收件地址")
    public CommonResult<Boolean> updateAddress(@Valid @RequestBody AppAddressUpdateReqVO updateReqVO) {
        touristAddressService.updateAddress(getLoginUserId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户收件地址")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteAddress(@RequestParam("id") Long id) {
        touristAddressService.deleteAddress(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户收件地址")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppAddressRespVO> getAddress(@RequestParam("id") Long id) {
        AddressDO address = touristAddressService.getAddress(getLoginUserId(), id);
        return success(AddressConvert.INSTANCE.convert(address));
    }

    @GetMapping("/get-default")
    @Operation(summary = "获得默认的用户收件地址")
    public CommonResult<AppAddressRespVO> getDefaultUserAddress() {
        AddressDO address = touristAddressService.getDefaultUserAddress(getLoginUserId());
        return success(AddressConvert.INSTANCE.convert(address));
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户收件地址列表")
    public CommonResult<List<AppAddressRespVO>> getAddressList() {
        List<AddressDO> list = touristAddressService.getAddressList(getLoginUserId());
        return success(AddressConvert.INSTANCE.convertList(list));
    }

}
