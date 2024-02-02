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
        sellerSpriteService.AutoUpdateCookies();
        // int count = adminUserLevelService.expireLevel();
        return String.format("过期等级 %s 个");
    }
}

//@Component
// public class AdminUserLevelAutoExpireJob implements JobHandler {
//
//
//     @Resource
//     private AdminUserLevelService adminUserLevelService;
//     /**
//      * 执行任务
//      *
//      * @param param 参数
//      * @return 结果
//      * @throws Exception 异常
//      */
//     @Override
//     @TenantJob
//     public String execute(String param) throws Exception {
//         int count = adminUserLevelService.expireLevel();
//         return String.format("过期等级 %s 个", count);
//     }
// }
