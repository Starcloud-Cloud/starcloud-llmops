package com.starcloud.ops.business.app.dal.redis;

import cn.iocoder.yudao.framework.redis.core.RedisKeyDefine;

import static cn.iocoder.yudao.framework.redis.core.RedisKeyDefine.KeyTypeEnum.STRING;

/**
 * Redis Key 枚举类
 */
public interface RedisKeyConstants {

    RedisKeyDefine CAPTCHA_CODE = new RedisKeyDefine("验证码的缓存xxxx",
            "xxxx_code:%s",
            STRING, String.class, RedisKeyDefine.TimeoutTypeEnum.DYNAMIC);

}
