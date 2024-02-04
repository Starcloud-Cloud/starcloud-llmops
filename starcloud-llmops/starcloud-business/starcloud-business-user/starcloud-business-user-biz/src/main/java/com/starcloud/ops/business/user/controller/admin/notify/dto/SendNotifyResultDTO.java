package com.starcloud.ops.business.user.controller.admin.notify.dto;

import lombok.Data;

@Data
public class SendNotifyResultDTO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 媒介记录Id
     */
    private Long mediaLogId;

    public static SendNotifyResultDTO success(Long mediaLogId) {
        SendNotifyResultDTO sendNotifyResultDTO = new SendNotifyResultDTO();
        sendNotifyResultDTO.setSuccess(true);
        sendNotifyResultDTO.setMediaLogId(mediaLogId);
        return sendNotifyResultDTO;
    }

    public static SendNotifyResultDTO error(Long mediaLogId,String message) {
        SendNotifyResultDTO sendNotifyResultDTO = new SendNotifyResultDTO();
        sendNotifyResultDTO.setSuccess(false);
        sendNotifyResultDTO.setMediaLogId(mediaLogId);
        sendNotifyResultDTO.setErrorMsg(message);
        return sendNotifyResultDTO;
    }
}
