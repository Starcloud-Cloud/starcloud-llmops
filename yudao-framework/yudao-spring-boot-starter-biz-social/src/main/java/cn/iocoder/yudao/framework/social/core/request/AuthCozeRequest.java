package cn.iocoder.yudao.framework.social.core.request;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.social.core.enums.AuthExtendSource;
import com.alibaba.fastjson.JSONObject;
import com.xingyuv.http.support.HttpHeader;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.enums.AuthResponseStatus;
import com.xingyuv.jushauth.enums.AuthUserGender;
import com.xingyuv.jushauth.exception.AuthException;
import com.xingyuv.jushauth.log.Log;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthResponse;
import com.xingyuv.jushauth.model.AuthToken;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.request.AuthDefaultRequest;
import com.xingyuv.jushauth.utils.AuthChecker;
import com.xingyuv.jushauth.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;


public class AuthCozeRequest extends AuthDefaultRequest {
    private static final Logger log = LoggerFactory.getLogger(AuthCozeRequest.class);

    public AuthCozeRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthExtendSource.COZE, authStateCache);
    }

    /**
     * @param authToken
     * @return
     */
    @Override
    public AuthResponse revoke(AuthToken authToken) {
        return super.revoke(authToken);
    }

    /**
     * @param authToken
     * @return
     */
    @Override
    public AuthResponse refresh(AuthToken authToken) {
        try {
            String response = new HttpUtils(config.getHttpConfig())
                    .post(source.refresh(), refreshTokenData(authToken.getRefreshToken()), new HttpHeader().add("Authorization", StrUtil.format("Bearer {}", config.getClientSecret())).add("Content-Type", "application/json"))
                    .getBody();
            log.info("refresh response:{}", response);
            JSONObject object = JSONObject.parseObject(response);
            this.checkResponse(object);
            AuthToken build = AuthToken.builder()
                    .accessToken(object.getString("access_token"))
                    .refreshToken(object.getString("refresh_token"))
                    .expireIn(object.getInteger("expires_in"))
                    .build();
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(build).build();
        } catch (Exception e) {
            Log.error("Failed to login with oauth authorization.", e);
            return AuthResponse.builder().code(AuthResponseStatus.FAILURE.getCode()).data(e).build();
        }
    }

    /**
     * @param authCallback
     * @return
     */
    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {

        String response = new HttpUtils(config.getHttpConfig()).post(source.accessToken(), accessTokenData(authCallback.getCode()), new HttpHeader().add("Authorization", StrUtil.format("Bearer {}", config.getClientSecret())).add("Content-Type", "application/json")).getBody();
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return AuthToken.builder()
                .accessToken(object.getString("access_token"))
                .refreshToken(object.getString("refresh_token"))
                .expireIn(object.getInteger("expires_in"))
                .build();
    }

    /**
     * @param authToken
     * @return
     */
    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        Long loginUserId = getLoginUserId();
        // this.checkResponse(object);

        return AuthUser.builder()
                .uuid(String.valueOf(loginUserId))
                .nickname(StrUtil.sub(authToken.getAccessToken(), 0, 11))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }


    private void checkResponse(JSONObject object) {
        // oauth/token 验证异常
        if (object.containsKey("error_code")) {
            throw new AuthException(object.getString("error_message"));
        }
        // user 验证异常
        if (object.containsKey("error_message")) {
            throw new AuthException(object.getString("error_message"));
        }
    }


    protected String refreshTokenData(String refreshToken) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("grant_type", "refresh_token");
        jsonObject.put("refresh_token", refreshToken);
        jsonObject.put("client_id", config.getClientId());
        return JSONUtil.toJsonStr(jsonObject);
    }

    /**
     * 返回获取accessToken的数据
     *
     * @param code 授权码
     * @return 返回获取accessToken的url
     */

    protected String accessTokenData(String code) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("grant_type", "authorization_code");
        jsonObject.put("code", code);
        jsonObject.put("client_id", config.getClientId());
        jsonObject.put("redirect_uri", config.getRedirectUri());
        return JSONUtil.toJsonStr(jsonObject);
    }


    // 扣子暂未提供获取用户接口 直接使用获取 AccessToken 接口 然后 mock 用户执行流程
    @Override
    public AuthResponse login(AuthCallback authCallback) {
        try {
            if (!config.isIgnoreCheckState()) {
                AuthChecker.checkState(authCallback.getState(), source, authStateCache);
            }
            AuthToken authToken = this.getAccessToken(authCallback);
            LocalDateTime thirtyDaysLater = LocalDateTimeUtil.now().plusDays(30);
            // 指定时区（东八区）
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            ZonedDateTime zonedDateTime = thirtyDaysLater.atZone(zoneId);

            authToken.setRefreshTokenExpireIn((int) zonedDateTime.toEpochSecond());
            AuthUser user = this.getUserInfo(authToken);
            //
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(user).build();
        } catch (Exception e) {
            Log.error("Failed to login with oauth authorization.", e);
            return AuthResponse.builder().code(AuthResponseStatus.FAILURE.getCode()).data(e).build();
        }

    }


}
