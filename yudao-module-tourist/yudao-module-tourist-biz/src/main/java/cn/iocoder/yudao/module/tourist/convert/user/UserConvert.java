package cn.iocoder.yudao.module.tourist.convert.user;

import cn.iocoder.yudao.module.tourist.api.user.dto.TouristRespDTO;
import cn.iocoder.yudao.module.tourist.controller.app.user.vo.AppUserInfoRespVO;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    AppUserInfoRespVO convert(TouristDO bean);

    TouristRespDTO convert2(TouristDO bean);

    List<TouristRespDTO> convertList2(List<TouristDO> list);

}
