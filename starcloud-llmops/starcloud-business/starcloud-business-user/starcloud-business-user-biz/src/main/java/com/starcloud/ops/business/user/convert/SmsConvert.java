package com.starcloud.ops.business.user.convert;


import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeLoginReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeRegisterReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeSendReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeValidateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SmsConvert {

    SmsConvert INSTANCE = Mappers.getMapper(SmsConvert.class);



    @Mapping(source = "account", target = "mobile")
    SmsCodeSendReqDTO smsVo2SendDTO(CodeSendReqVO reqVO);

    @Mapping(source = "reqVO.account", target = "mobile")
    SmsCodeUseReqDTO smsVo2UseDTO(CodeValidateReqVO reqVO, Integer scene);

    @Mapping(source = "reqVO.account", target = "mobile")
    SmsCodeUseReqDTO convert(CodeLoginReqVO reqVO, Integer scene, String usedIp);

    @Mapping(source = "reqVO.account", target = "mobile")
    SmsCodeUseReqDTO convert(CodeRegisterReqVO reqVO, Integer scene, String usedIp);
}
