package cn.iocoder.yudao.module.starcloud.adapter.ruoyipro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import cn.iocoder.yudao.module.system.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import cn.iocoder.yudao.module.system.dal.mysql.oauth2.OAuth2RefreshTokenMapper;
import cn.iocoder.yudao.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2ClientService;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2TokenServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;


/**
 * 使用若以的表和逻辑，实现一个简单的游客状态的记录功能
 * 1，根据游客访问渠道（网页，微信对话，钉钉等），主动生成一个刷新令牌（如果存在就延长当前记录的过期时间）
 * 2，使用刷新令牌作为 EndUser 值 到上下文中
 * 3，下游手动增加 EndUser 字段到表中，自行使用
 */
@Service
public class EndUserService extends OAuth2TokenServiceImpl {

    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;

    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Resource
    private OAuth2ClientService oauth2ClientService;


    public OAuth2RefreshTokenDO weChatAccessToken(String refreshToken) {

        // 校验 Client 匹配
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache("WeChat");

        // 查询访问令牌
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);

        //没有就创建
        if (refreshTokenDO == null) {
            refreshTokenDO = createOAuth2RefreshToken(0L, UserTypeEnum.ADMIN.getValue(), clientDO, new ArrayList<>());
        }

        if (ObjectUtil.notEqual(clientDO.getClientId(), refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 次类令牌不过期的
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            //oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());

            //更新刷新令牌的过期时间，直接叠加过期的时间

            refreshTokenDO.getExpiresTime();

        } else {


            //更新刷新令牌的过期时间，直接叠加过期的时间

            refreshTokenDO.getExpiresTime();
        }

        return refreshTokenDO;
    }
}
