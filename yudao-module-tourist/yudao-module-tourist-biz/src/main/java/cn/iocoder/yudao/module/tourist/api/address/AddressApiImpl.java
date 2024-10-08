package cn.iocoder.yudao.module.tourist.api.address;

import cn.iocoder.yudao.module.tourist.api.address.dto.AddressRespDTO;
import cn.iocoder.yudao.module.tourist.convert.address.AddressConvert;
import cn.iocoder.yudao.module.tourist.service.address.TouristAddressService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 用户收件地址 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AddressApiImpl implements AddressApi {

    @Resource
    private TouristAddressService touristAddressService;

    @Override
    public AddressRespDTO getAddress(Long id, Long userId) {
        return AddressConvert.INSTANCE.convert02(touristAddressService.getAddress(userId, id));
    }

}
