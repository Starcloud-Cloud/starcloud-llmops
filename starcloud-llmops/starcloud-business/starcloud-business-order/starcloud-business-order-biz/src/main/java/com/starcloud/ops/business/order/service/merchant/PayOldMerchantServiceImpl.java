package com.starcloud.ops.business.order.service.merchant;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.google.common.annotations.VisibleForTesting;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantExportReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantPageReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.merchant.PayMerchantUpdateReqVO;
import com.starcloud.ops.business.order.convert.merchant.PayMerchantConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import com.starcloud.ops.business.order.dal.mysql.merchant.PayOldAppMapper;
import com.starcloud.ops.business.order.dal.mysql.merchant.PayOldMerchantMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.*;

/**
 * 支付商户信息 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
public class PayOldMerchantServiceImpl implements PayMerchantService {

    @Resource
    private PayOldMerchantMapper merchantMapper;

    @Resource
    private PayOldAppMapper payOldAppMapper;

    @Override
    public Long createMerchant(PayMerchantCreateReqVO createReqVO) {
        // 插入
        PayMerchantDO merchant = PayMerchantConvert.INSTANCE.convert(createReqVO);
        merchant.setNo(this.generateMerchantNo());
        merchantMapper.insert(merchant);
        // 返回
        return merchant.getId();
    }

    @Override
    public void updateMerchant(PayMerchantUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateMerchantExists(updateReqVO.getId());
        // 更新
        PayMerchantDO updateObj = PayMerchantConvert.INSTANCE.convert(updateReqVO);
        merchantMapper.updateById(updateObj);
    }

    @Override
    public void deleteMerchant(Long id) {
        // 校验
        this.validateMerchantExists(id);
        this.validateAppExists(id);
        // 删除
        merchantMapper.deleteById(id);
    }

    @Override
    public PayMerchantDO getMerchant(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public List<PayMerchantDO> getMerchantList(Collection<Long> ids) {
        return merchantMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<PayMerchantDO> getMerchantPage(PayMerchantPageReqVO pageReqVO) {
        return merchantMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PayMerchantDO> getMerchantList(PayMerchantExportReqVO exportReqVO) {
        return merchantMapper.selectList(exportReqVO);
    }

    @Override
    public void updateMerchantStatus(Long id, Integer status) {
        // 校验商户存在
        this.checkMerchantExists(id);
        // 更新状态
        PayMerchantDO merchant = new PayMerchantDO();
        merchant.setId(id);
        merchant.setStatus(status);
        merchantMapper.updateById(merchant);
    }

    @Override
    public List<PayMerchantDO> getMerchantListByName(String merchantName) {
        return this.merchantMapper.getMerchantListByName(merchantName);
    }

    @VisibleForTesting
    public void checkMerchantExists(Long id) {
        if (id == null) {
            return;
        }
        PayMerchantDO merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw exception(CHANNEL_NOT_FOUND);
        }
    }

    /**
     * 校验商户是否存在
     *
     * @param id 商户 ID
     */
    private void validateMerchantExists(Long id) {
        if (ObjectUtil.isNull(merchantMapper.selectById(id))) {
            throw exception(CHANNEL_NOT_FOUND);
        }
    }

    /**
     * 校验商户是否还存在支付应用
     *
     * @param id 商户ID
     */
    private void validateAppExists(Long id) {
        if (payOldAppMapper.selectCount(id) > 0) {
            throw exception(CHANNEL_NOT_FOUND);
        }
    }

    // TODO @芋艿：后续增加下合适的算法
    /**
     * 根据年月日时分秒毫秒生成商户号
     *
     * @return 商户号
     */
    private String generateMerchantNo() {
        return "M" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
    }

}
