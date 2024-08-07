package cn.iocoder.yudao.framework.social.core.enums;

import cn.iocoder.yudao.framework.social.core.request.AuthCozeRequest;
import com.xingyuv.jushauth.config.AuthSource;
import com.xingyuv.jushauth.request.AuthDefaultRequest;

/**
 * 拓展 JustAuth 各 api 需要的 url， 用枚举类分平台类型管理
 *
 * 默认配置 {@link com.xingyuv.jushauth.config.AuthDefaultSource}
 *
 * @author timfruit
 */
public enum AuthExtendSource implements AuthSource {

    /**
     * 微信小程序授权登录
     */
    WECHAT_MINI_APP {

        @Override
        public String authorize() {
            // 参见 https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html 文档
            throw new UnsupportedOperationException("不支持获取授权 url，请使用小程序内置函数 wx.login() 登录获取 code");
        }

        @Override
        public String accessToken() {
            // 参见 https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html 文档
            // 获取 openid, unionId , session_key 等字段
            return "https://api.weixin.qq.com/sns/jscode2session";
        }

        @Override
        public String userInfo() {
            // 参见 https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserProfile.html 文档
            throw new UnsupportedOperationException("不支持获取用户信息 url，请使用小程序内置函数 wx.getUserProfile() 获取用户信息");
        }

        @Override
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return null;
        }
    }
    ,
    COZE {
        /**
         * 授权的api
         *
         * @return url
         */
        @Override
        public String authorize() {
            return "https://www.coze.cn/api/permission/oauth2/authorize";
        }

        /**
         * 获取accessToken的api
         *
         * @return url
         */
        @Override
        public String accessToken() {
            return "https://api.coze.cn/api/permission/oauth2/token";
        }

        /**
         * 获取用户信息的api
         *
         * @return url
         */
        @Override
        public String userInfo() {
            return "http://gitlab.xxx.com/api/v4/user";
        }

        /**
         * @return
         */
        @Override
        public String revoke() {
            return super.revoke();
        }

        /**
         * @return
         */
        @Override
        public String refresh() {
            return "https://api.coze.cn/api/permission/oauth2/token";
        }


        /**
         * @return
         */
        @Override
        public Class<AuthCozeRequest> getTargetClass() {
            return AuthCozeRequest.class;
        }
    }

}
