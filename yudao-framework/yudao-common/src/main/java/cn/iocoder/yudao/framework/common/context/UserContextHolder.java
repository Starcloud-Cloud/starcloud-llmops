package cn.iocoder.yudao.framework.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class UserContextHolder {

    /**
     * 当前用户编号
     */
    private static final ThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();

    /**
     * 获得用户编号。
     *
     * @return 用户编号
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void clear() {
        USER_ID.remove();
    }

}
