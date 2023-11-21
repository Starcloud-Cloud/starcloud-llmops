package cn.iocoder.yudao.module.tourist.api.user;

import cn.iocoder.yudao.module.tourist.api.user.dto.TouristRespDTO;
import cn.iocoder.yudao.module.tourist.convert.user.UserConvert;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import cn.iocoder.yudao.module.tourist.service.user.TouristService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 会员用户的 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class TouristApiImpl implements TouristApi {

    @Resource
    private TouristService userService;

    @Override
    public TouristRespDTO getUser(Long id) {
        TouristDO user = userService.getUser(id);
        return UserConvert.INSTANCE.convert2(user);
    }

    @Override
    public List<TouristRespDTO> getUsers(Collection<Long> ids) {
        return UserConvert.INSTANCE.convertList2(userService.getUserList(ids));
    }

    @Override
    public List<TouristRespDTO> getUserListByNickname(String nickname) {
        return UserConvert.INSTANCE.convertList2(userService.getUserListByNickname(nickname));
    }

    @Override
    public TouristRespDTO getUserByMobile(String mobile) {
        return UserConvert.INSTANCE.convert2(userService.getUserByMobile(mobile));
    }

}
