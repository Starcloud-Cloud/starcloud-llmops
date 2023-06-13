package com.starcloud.ops.business.app.service.log;

public interface APPLogService {

    String generateAPPRequestUid();

    String generateStepRequestUid(String appRequestUid);

}
