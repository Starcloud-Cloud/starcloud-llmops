package cn.iocoder.yudao.module.tourist.service.address;

import cn.iocoder.yudao.module.tourist.controller.app.address.vo.AppAddressCreateReqVO;
import cn.iocoder.yudao.module.tourist.controller.app.address.vo.AppAddressUpdateReqVO;
import cn.iocoder.yudao.module.tourist.dal.dataobject.address.AddressDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户收件地址 Service 接口
 *
 * @author 芋道源码
 */
public interface TouristAddressService {

    /**
     * 创建用户收件地址
     *
     *
     * @param userId 用户编号
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAddress(Long userId, @Valid AppAddressCreateReqVO createReqVO);

    /**
     * 更新用户收件地址
     *
     * @param userId 用户编号
     * @param updateReqVO 更新信息
     */
    void updateAddress(Long userId, @Valid AppAddressUpdateReqVO updateReqVO);

    /**
     * 删除用户收件地址
     *
     * @param userId 用户编号
     * @param id 编号
     */
    void deleteAddress(Long userId, Long id);

    /**
     * 获得用户收件地址
     *
     * @param id 编号
     * @return 用户收件地址
     */
    AddressDO getAddress(Long userId, Long id);

    /**
     * 获得用户收件地址列表
     *
     * @param userId 用户编号
     * @return 用户收件地址列表
     */
    List<AddressDO> getAddressList(Long userId);

    /**
     * 获得用户默认的收件地址
     *
     * @param userId 用户编号
     * @return 用户收件地址
     */
    AddressDO getDefaultUserAddress(Long userId);

}
