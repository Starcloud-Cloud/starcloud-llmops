package com.starcloud.ops.business.mission.service.qr;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.GENERATE_QR_ERROR;

@Slf4j
@Component
public class MiniAppManager {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private FileConfigService fileConfigService;

    private static final String path = "mini-app/qr/";

    /**
     * 生成小程序二维码
     *
     * @return
     */
    public String generateQr(String fileName, String scene, String page) {
        try {
            byte[] qrByte = wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(scene, page, true,
                    getEnvVersion(), 430, true, null, false);
            String mineType = FileTypeUtils.getMineType(qrByte, fileName);
            return fileConfigService.getMasterFileClient().upload(qrByte, path + fileName, mineType);
        } catch (Exception e) {
            log.error("generate mini app qr error", e);
            throw exception(GENERATE_QR_ERROR, e.getMessage());
        }
    }

    private String getEnvVersion() {
        String activeProfile = SpringUtil.getActiveProfile();
        String envVersion;
        switch (activeProfile) {
            case "cn-test":
            case "dev":
                envVersion = "trial";
                break;
            default:
                envVersion = "release";
                break;
        }
        return envVersion;
    }

}
