package cn.iocoder.yudao.module.pay.convert.sign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.agreement.PayAgreementUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.*;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignSubmitReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignSubmitRespVO;
import cn.iocoder.yudao.module.pay.controller.app.order.vo.AppPayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignExtensionDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付订单 Convert
 *
 * @author aquan
 */
@Mapper
public interface PaySignConvert {

    PaySignConvert INSTANCE = Mappers.getMapper(PaySignConvert.class);

    // PayOrderRespVO convert(PayOrderDO bean);

    PayOrderSubmitReqVO convert(PaySignSubmitReqVO reqVO);

    PayOrderRespDTO convert2(PayOrderDO order);

    default PayOrderDetailsRespVO convert(PayOrderDO order, PayOrderExtensionDO orderExtension, PayAppDO app) {
        PayOrderDetailsRespVO respVO = convertDetail(order);
        respVO.setExtension(convert(orderExtension));
        if (app != null) {
            respVO.setAppName(app.getName());
        }
        return respVO;
    }
    PayOrderDetailsRespVO convertDetail(PayOrderDO bean);
    PayOrderDetailsRespVO.PayOrderExtension convert(PayOrderExtensionDO bean);

    default PageResult<PayOrderPageItemRespVO> convertPage(PageResult<PayOrderDO> page, Map<Long, PayAppDO> appMap) {
        PageResult<PayOrderPageItemRespVO> result = convertPage(page);
        result.getList().forEach(order -> MapUtils.findAndThen(appMap, order.getAppId(), app -> order.setAppName(app.getName())));
        return result;
    }
    PageResult<PayOrderPageItemRespVO> convertPage(PageResult<PayOrderDO> page);

    // default List<PayOrderExcelVO> convertList(List<PayOrderDO> list, Map<Long, PayAppDO> appMap) {
    //     return CollectionUtils.convertList(list, order -> {
    //         PayOrderExcelVO excelVO = convertExcel(order);
    //         MapUtils.findAndThen(appMap, order.getAppId(), app -> excelVO.setAppName(app.getName()));
    //         return excelVO;
    //     });
    // }
    // PayOrderExcelVO convertExcel(PayOrderDO bean);

    PaySignDO convert(PaySignCreateReqDTO bean);

    @Mapping(target = "id", ignore = true)
    default PaySignExtensionDO convert(PaySignSubmitReqVO bean, String userIp){
        if ( bean == null && userIp == null ) {
            return null;
        }

        PaySignExtensionDO.PaySignExtensionDOBuilder builder = PaySignExtensionDO.builder();

        if ( bean != null ) {
            builder.channelCode( bean.getChannelCode() );
            Map<String, String> map = bean.getChannelExtras();
            if ( map != null ) {
                builder.channelExtras(String.valueOf(new LinkedHashMap<String, String>( map )));
            }
        }
        builder.userIp( userIp );

        return builder.build();
    }

    PayAgreementUnifiedReqDTO convert2(PaySignSubmitReqVO reqVO, String userIp);

    @Mapping(source = "signDO.status", target = "status")
    PaySignSubmitRespVO convert(PaySignDO signDO, PayAgreementRespDTO respDTO);

    // AppPayOrderSubmitRespVO convert3(PayOrderSubmitRespVO bean);

}
