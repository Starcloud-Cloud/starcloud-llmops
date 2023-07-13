package com.starcloud.ops.business.order.service.merchant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.google.common.annotations.VisibleForTesting;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppExportReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppPageReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppUpdateReqVO;
import com.starcloud.ops.business.order.convert.app.PayAppConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import com.starcloud.ops.business.order.dal.mysql.merchant.PayAppMapper;
import com.starcloud.ops.business.order.dal.mysql.merchant.PayMerchantMapper;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderMapper;
import com.starcloud.ops.business.order.dal.mysql.refund.PayRefundMapper;
import com.starcloud.ops.business.order.enums.ErrorCodeConstants;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.refund.PayRefundStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.PAY_APP_EXIST_TRANSACTION_ORDER_CANT_DELETE;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.PAY_APP_NOT_FOUND;

/**
 * 支付应用信息 Service 实现类
 *
 * @author aquan
 */
@Slf4j
@Service
@Validated
public class PayAppServiceImpl implements PayAppService {

    @Resource
    private PayAppMapper payAppMapper;
    // TODO @aquan：使用对方的 Service。模块与模块之间，避免直接调用对方的 mapper
    @Resource
    private PayMerchantMapper merchantMapper;
    @Resource
    private PayOrderMapper orderMapper;
    @Resource
    private PayRefundMapper refundMapper;

    @Override
    public Long createApp(PayAppCreateReqVO createReqVO) {
        // 插入
        PayAppDO app = PayAppConvert.INSTANCE.convert(createReqVO);
        payAppMapper.insert(app);
        // 返回
        return app.getId();
    }

    @Override
    public void updateApp(PayAppUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateAppExists(updateReqVO.getId());
        // 更新
        PayAppDO updateObj = PayAppConvert.INSTANCE.convert(updateReqVO);
        payAppMapper.updateById(updateObj);
    }

    @Override
    public void deleteApp(Long id) {
        // 校验存在
        this.validateAppExists(id);
        this.validateOrderTransactionExist(id);

        // 删除
        payAppMapper.deleteById(id);
    }

    private void validateAppExists(Long id) {
        if (payAppMapper.selectById(id) == null) {
            throw exception(PAY_APP_NOT_FOUND);
        }
    }

    @Override
    public PayAppDO getApp(Long id) {
        return payAppMapper.selectById(id);
    }

    @Override
    public List<PayAppDO> getAppList(Collection<Long> ids) {
        return payAppMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<PayAppDO> getAppPage(PayAppPageReqVO pageReqVO) {
        Set<Long> merchantIdList = this.getMerchantCondition(pageReqVO.getMerchantName());
        if (StrUtil.isNotBlank(pageReqVO.getMerchantName()) && CollectionUtil.isEmpty(merchantIdList)) {
            return new PageResult<>();
        }
        return payAppMapper.selectPage(pageReqVO, merchantIdList);
    }

    @Override
    public List<PayAppDO> getAppList(PayAppExportReqVO exportReqVO) {
        Set<Long> merchantIdList = this.getMerchantCondition(exportReqVO.getMerchantName());
        if (StrUtil.isNotBlank(exportReqVO.getMerchantName()) && CollectionUtil.isEmpty(merchantIdList)) {
            return new ArrayList<>();
        }
        return payAppMapper.selectList(exportReqVO, merchantIdList);
    }

    /**
     * 获取商户编号集合，根据商户名称模糊查询得到所有的商户编号集合
     *
     * @param merchantName 商户名称
     * @return 商户编号集合
     */
    private Set<Long> getMerchantCondition(String merchantName) {
        if (StrUtil.isBlank(merchantName)) {
            return Collections.emptySet();
        }
        return convertSet(merchantMapper.getMerchantListByName(merchantName), PayMerchantDO::getId);
    }

    @Override
    public void updateAppStatus(Long id, Integer status) {
        // 校验商户存在
        this.checkAppExists(id);
        // 更新状态
        PayAppDO app = new PayAppDO();
        app.setId(id);
        app.setStatus(status);
        payAppMapper.updateById(app);
    }

    @Override
    public List<PayAppDO> getListByMerchantId(Long merchantId) {
        return payAppMapper.getListByMerchantId(merchantId);
    }

    /**
     * 检查商户是否存在
     *
     * @param id 商户编号
     */
    @VisibleForTesting
    public void checkAppExists(Long id) {
        if (id == null) {
            return;
        }
        PayAppDO payApp = payAppMapper.selectById(id);
        if (payApp == null) {
            throw exception(PAY_APP_NOT_FOUND);
        }
    }

    /**
     * 验证是否存在交易中或者退款中等处理中状态的订单
     *
     * @param appId 应用 ID
     */
    private void validateOrderTransactionExist(Long appId) {
        // 查看交易订单
        if (orderMapper.selectCount(appId, PayOrderStatusEnum.WAITING.getStatus()) > 0) {
            throw exception(PAY_APP_EXIST_TRANSACTION_ORDER_CANT_DELETE);
        }
        // 查看退款订单
        if (refundMapper.selectCount(appId, PayRefundStatusEnum.CREATE.getStatus()) > 0) {
            throw exception(PAY_APP_EXIST_TRANSACTION_ORDER_CANT_DELETE);
        }
    }

    @Override
    public PayAppDO validPayApp(Long id) {
        PayAppDO app = payAppMapper.selectById(id);
        // 校验是否存在
        if (app == null) {
            throw ServiceExceptionUtil.exception(PAY_APP_NOT_FOUND);
        }
        // 校验是否禁用
        if (CommonStatusEnum.DISABLE.getStatus().equals(app.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_APP_IS_DISABLE);
        }
        return app;
    }

    /**
     * 获取当前商户信息
     *
     * @return 支付应用信息列表
     */
    @Override
    public PayAppDO getAppInfo() {
        List<PayAppDO> payAppDOS = payAppMapper.selectList();
        if (payAppDOS.size() < 1) {
            throw ServiceExceptionUtil.exception(PAY_APP_NOT_FOUND);
        } else if (payAppDOS.size() > 1) {
            log.error("存在多条应用配置，请立刻删除无用的数据");
        }

        return payAppDOS.get(0);
    }

}
