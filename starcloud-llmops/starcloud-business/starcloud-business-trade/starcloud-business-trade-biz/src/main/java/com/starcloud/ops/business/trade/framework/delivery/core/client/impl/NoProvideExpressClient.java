package com.starcloud.ops.business.trade.framework.delivery.core.client.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.trade.framework.delivery.core.client.ExpressClient;
import com.starcloud.ops.business.trade.framework.delivery.core.client.dto.ExpressTrackQueryReqDTO;
import com.starcloud.ops.business.trade.framework.delivery.core.client.dto.ExpressTrackRespDTO;
import com.starcloud.ops.business.trade.enums.ErrorCodeConstants;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 未实现的快递客户端，用来提醒用户需要接入快递服务商，
 *
 * @author jason
 */
public class NoProvideExpressClient implements ExpressClient {

    @Override
    public List<ExpressTrackRespDTO> getExpressTrackList(ExpressTrackQueryReqDTO reqDTO) {
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXPRESS_CLIENT_NOT_PROVIDE);
    }

}
