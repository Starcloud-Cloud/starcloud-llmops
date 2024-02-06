package com.starcloud.ops.business.listing.job.sellersprite;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AutoUpdateCookiesJob implements JobHandler {


    @Resource
    private SellerSpriteService sellerSpriteService;

    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public String execute(String param) throws Exception {
        sellerSpriteService.AutoUpdateCheckCookies(null);
        return String.format("检测卖家精灵 Cookie");
    }
}
