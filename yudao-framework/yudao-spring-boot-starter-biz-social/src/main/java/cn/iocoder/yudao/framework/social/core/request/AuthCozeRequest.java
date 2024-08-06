package cn.iocoder.yudao.framework.social.core.request;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.social.core.enums.AuthExtendSource;
import com.alibaba.fastjson.JSONObject;
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


public class AuthCozeRequest extends AuthDefaultRequest {
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

        String response = refreshTokenUrl(authToken.getRefreshToken());
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);
        return AuthResponse.builder()
                .data(object)
                .build();
    }

    /**
     * @param authCallback
     * @return
     */
    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String response = doPostAuthorizationCode(authCallback.getCode());
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
        String response = doGetUserInfo(authToken);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return AuthUser.builder()
                .uuid(object.getString("id"))
                .username(object.getString("username"))
                .nickname(object.getString("name"))
                .avatar(object.getString("avatar_url"))
                .blog(object.getString("web_url"))
                .company(object.getString("organization"))
                .location(object.getString("location"))
                .email(object.getString("email"))
                .remark(object.getString("bio"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }


    private void checkResponse(JSONObject object) {
        // oauth/token 验证异常
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
        // user 验证异常
        if (object.containsKey("message")) {
            throw new AuthException(object.getString("message"));
        }
    }

    @Override
    public AuthResponse login(AuthCallback authCallback) {
        try {
            if (!config.isIgnoreCheckState()) {
                AuthChecker.checkState(authCallback.getState(), source, authStateCache);
            }
            AuthToken authToken = this.getAccessToken(authCallback);
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(authToken).build();
        } catch (Exception e) {
            Log.error("Failed to login with oauth authorization.", e);
            return  AuthResponse.builder().code(AuthResponseStatus.FAILURE.getCode()).data(e).build();
        }

    }




}
