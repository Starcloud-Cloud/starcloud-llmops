package cn.iocoder.yudao.module.tourist.service.user;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.tourist.controller.app.user.vo.AppUserUpdateMobileReqVO;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import cn.iocoder.yudao.module.tourist.dal.mysql.user.TouristMapper;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.tourist.enums.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * 会员 User Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Valid
@Slf4j
public class TouristServiceImpl implements TouristService {

    @Resource
    private TouristMapper touristMapper;

    @Resource
    private FileApi fileApi;
    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public TouristDO getUserByMobile(String mobile) {
        return touristMapper.selectByMobile(mobile);
    }

    @Override
    public List<TouristDO> getUserListByNickname(String nickname) {
        return touristMapper.selectListByNicknameLike(nickname);
    }

    @Override
    public TouristDO createUserIfAbsent(String mobile, String registerIp) {
        // 用户已经存在
        TouristDO user = touristMapper.selectByMobile(mobile);
        if (user != null) {
            return user;
        }
        // 用户不存在，则进行创建
        return this.createUser(mobile, registerIp);
    }

    public TouristDO createUser(String mobile, String registerIp) {
        // 生成密码
        return this.createUser(mobile, "", registerIp);
    }

    public TouristDO createUser(String mobile, String avatar, String registerIp) {
        // 生成密码
        String password = IdUtil.fastSimpleUUID();
        // 插入用户
        TouristDO user = new TouristDO();
        user.setAvatar(avatar);
        user.setMobile(mobile);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setPassword(encodePassword(password)); // 加密密码
        user.setRegisterIp(registerIp);
        touristMapper.insert(user);
        return user;
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        touristMapper.updateById(new TouristDO().setId(id)
                .setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public TouristDO getUser(Long id) {
        return touristMapper.selectById(id);
    }

    @Override
    public List<TouristDO> getUserList(Collection<Long> ids) {
        return touristMapper.selectBatchIds(ids);
    }

    @Override
    public void updateUserNickname(Long userId, String nickname) {
        TouristDO user = this.checkUserExists(userId);
        // 仅当新昵称不等于旧昵称时进行修改
        if (nickname.equals(user.getNickname())) {
            return;
        }
        TouristDO userDO = new TouristDO();
        userDO.setId(user.getId());
        userDO.setNickname(nickname);
        touristMapper.updateById(userDO);
    }

    @Override
    public String updateUserAvatar(Long userId, InputStream avatarFile) throws Exception {
        this.checkUserExists(userId);
        // 创建文件
        String avatar = fileApi.createFile(IoUtil.readBytes(avatarFile));
        // 更新头像路径
        touristMapper.updateById(TouristDO.builder().id(userId).avatar(avatar).build());
        return avatar;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserMobile(Long userId, AppUserUpdateMobileReqVO reqVO) {
        // 检测用户是否存在
        checkUserExists(userId);
        // TODO 芋艿：oldMobile 应该不用传递

        // 校验旧手机和旧验证码
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO().setMobile(reqVO.getOldMobile()).setCode(reqVO.getOldCode())
                .setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene()).setUsedIp(getClientIP()));
        // 使用新验证码
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO().setMobile(reqVO.getMobile()).setCode(reqVO.getCode())
                .setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene()).setUsedIp(getClientIP()));

        // 更新用户手机
        touristMapper.updateById(TouristDO.builder().id(userId).mobile(reqVO.getMobile()).build());
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @VisibleForTesting
    public TouristDO checkUserExists(Long id) {
        if (id == null) {
            return null;
        }
        TouristDO user = touristMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

}
