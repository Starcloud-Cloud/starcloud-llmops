package cn.iocoder.yudao.module.tourist.service.user;

import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.collection.ArrayUtils;
import cn.iocoder.yudao.framework.redis.config.YudaoRedisAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbAndRedisUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.tourist.controller.app.user.vo.AppUserUpdateMobileReqVO;
import cn.iocoder.yudao.module.tourist.dal.dataobject.user.TouristDO;
import cn.iocoder.yudao.module.tourist.dal.mysql.user.TouristMapper;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.tourist.service.auth.TouristAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

// TODO @芋艿：单测的 review，等逻辑都达成一致后
/**
 * {@link TouristServiceImpl} 的单元测试类
 *
 * @author 宋天
 */
@Import({TouristServiceImpl.class, YudaoRedisAutoConfiguration.class})
public class TouristServiceImplTest extends BaseDbAndRedisUnitTest {

    @Resource
    private TouristServiceImpl touristService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TouristMapper userMapper;

    @MockBean
    private TouristAuthServiceImpl authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SmsCodeApi smsCodeApi;
    @MockBean
    private FileApi fileApi;

    @Test
    public void testUpdateNickName_success(){
        // mock 数据
        TouristDO userDO = randomUserDO();
        userMapper.insert(userDO);

        // 随机昵称
        String newNickName = randomString();

        // 调用接口修改昵称
        touristService.updateUserNickname(userDO.getId(),newNickName);
        // 查询新修改后的昵称
        String nickname = touristService.getUser(userDO.getId()).getNickname();
        // 断言
        assertEquals(newNickName,nickname);
    }

    @Test
    public void testUpdateAvatar_success() throws Exception {
        // mock 数据
        TouristDO dbUser = randomUserDO();
        userMapper.insert(dbUser);

        // 准备参数
        Long userId = dbUser.getId();
        byte[] avatarFileBytes = randomBytes(10);
        ByteArrayInputStream avatarFile = new ByteArrayInputStream(avatarFileBytes);
        // mock 方法
        String avatar = randomString();
        when(fileApi.createFile(eq(avatarFileBytes))).thenReturn(avatar);
        // 调用
        String str = touristService.updateUserAvatar(userId, avatarFile);
        // 断言
        assertEquals(avatar, str);
    }

    @Test
    public void updateMobile_success(){
        // mock数据
        String oldMobile = randomNumbers(11);
        TouristDO userDO = randomUserDO();
        userDO.setMobile(oldMobile);
        userMapper.insert(userDO);

        // TODO 芋艿：需要修复该单元测试，重构多模块带来的
        // 旧手机和旧验证码
//        SmsCodeDO codeDO = new SmsCodeDO();
        String oldCode = RandomUtil.randomString(4);
//        codeDO.setMobile(userDO.getMobile());
//        codeDO.setCode(oldCode);
//        codeDO.setScene(SmsSceneEnum.MEMBER_UPDATE_MOBILE.getScene());
//        codeDO.setUsed(Boolean.FALSE);
//        when(smsCodeService.checkCodeIsExpired(codeDO.getMobile(),codeDO.getCode(),codeDO.getScene())).thenReturn(codeDO);

        // 更新手机号
        String newMobile = randomNumbers(11);
        String newCode = randomNumbers(4);
        AppUserUpdateMobileReqVO reqVO = new AppUserUpdateMobileReqVO();
        reqVO.setMobile(newMobile);
        reqVO.setCode(newCode);
        reqVO.setOldMobile(oldMobile);
        reqVO.setOldCode(oldCode);
        touristService.updateUserMobile(userDO.getId(),reqVO);

        assertEquals(touristService.getUser(userDO.getId()).getMobile(),newMobile);
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static TouristDO randomUserDO(Consumer<TouristDO>... consumers) {
        Consumer<TouristDO> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
        };
        return randomPojo(TouristDO.class, ArrayUtils.append(consumer, consumers));
    }

}