package com.starcloud.ops.business.mission.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import org.apache.commons.lang3.StringUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

public class SingleMissionImportVO {

    @ExcelProperty("uid")
    private String uid;

    @ExcelProperty("认领人")
    private String claimUsername;

    @ExcelProperty("发布链接")
    private String publishUrl;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClaimUsername() {
        return claimUsername;
    }

    public void setClaimUsername(String claimUsername) {
        this.claimUsername = claimUsername;
    }

    public String getPublishUrl() {
        return publishUrl;
    }

    public void setPublishUrl(String publishUrl) {
        this.publishUrl = publishUrl;
    }

    public void valid() {
        if (StringUtils.isBlank(uid)
                || StringUtils.isBlank(claimUsername)
                || StringUtils.isBlank(publishUrl)) {
            throw exception(new ErrorCode(500, "导入的数据uid 认领人 发布链接不能为空 {}"), uid);
        }
        XhsDetailConstants.validNoteUrl(publishUrl);
    }
}
