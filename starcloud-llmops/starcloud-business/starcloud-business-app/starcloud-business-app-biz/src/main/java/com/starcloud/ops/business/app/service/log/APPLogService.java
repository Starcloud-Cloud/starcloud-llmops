package com.starcloud.ops.business.app.service.log;

public interface AppLogService {

    String generateAPPRequestUid();

    String generateStepRequestUid(String appRequestUid);

}
