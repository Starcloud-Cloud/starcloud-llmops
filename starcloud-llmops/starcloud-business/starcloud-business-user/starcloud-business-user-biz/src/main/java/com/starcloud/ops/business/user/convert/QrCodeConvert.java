package com.starcloud.ops.business.user.convert;

import com.starcloud.ops.business.user.controller.admin.vo.QrCodeTicketVO;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QrCodeConvert {
    QrCodeConvert INSTANCE = Mappers.getMapper(QrCodeConvert.class);

    QrCodeTicketVO toVO(WxMpQrCodeTicket wxMpQrCodeTicket);
}
