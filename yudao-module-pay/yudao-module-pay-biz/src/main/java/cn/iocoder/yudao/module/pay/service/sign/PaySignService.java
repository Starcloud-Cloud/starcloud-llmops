package cn.iocoder.yudao.module.pay.service.sign;

import javax.validation.*;

import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementRespDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.*;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.time.LocalDateTime;

/**
 * 支付签约
 Service 接口
 *
 * @author Cusack Alan
 */
public interface PaySignService {

    /**
     * 创建支付签约

     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSign(@Valid PaySignCreateReqDTO createReqVO);

    /**
     * 提交支付签约
     * @param reqVO
     * @param userIp
     * @return
     */
    PaySignSubmitRespVO submitSign(PaySignSubmitReqVO reqVO, String userIp);

    /**
     * 更新支付签约

     *
     * @param updateReqVO 更新信息
     */
    void updateSign(@Valid SignSaveReqVO updateReqVO);

    /**
     * 删除支付签约

     *
     * @param id 编号
     */
    void deleteSign(Long id);

    /**
     * 获得支付签约

     *
     * @param id 编号
     * @return 支付签约

     */
    PaySignDO getSign(Long id);

    /**
     * 获得支付签约
     */
    PaySignDO  getSignByMerchantSignId(Long appId, String merchantSignId);

    /**
     * 获得支付签约
     分页
     *
     * @param pageReqVO 分页查询
     * @return 支付签约
    分页
     */
    PageResult<PaySignDO> getSignPage(SignPageReqVO pageReqVO);

    int syncSign(LocalDateTime minCreateTime);

    /**
     * 同步签约支付
     * @return
     */
    int syncSignPay();

    /**
     * 同步签约状态
     * @return
     */
    int syncSignStatus();

    void notifySignStatus(Long channelId,PayAgreementRespDTO respDTO);
}
