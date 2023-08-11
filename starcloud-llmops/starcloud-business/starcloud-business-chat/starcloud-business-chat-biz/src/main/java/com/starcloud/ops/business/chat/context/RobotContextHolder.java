package com.starcloud.ops.business.chat.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class RobotContextHolder {

    /**
     * worktool平台 robotId
     */
    private static final ThreadLocal<String> ROBOTID = new TransmittableThreadLocal<>();

    public static void setRobotId(String robotId) {
        ROBOTID.set(robotId);
    }

    public static String getRobotId() {
        return ROBOTID.get();
    }

    public static void clear() {
        ROBOTID.remove();
    }
}
