package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.intercepter.XhsRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "xhs", url = "https://www.xiaohongshu.com", configuration = {XhsRequestInterceptor.class})
@ConditionalOnProperty(value = "xhs.remote.agent", matchIfMissing = true, havingValue = "xhs")
public interface XhsCilent {

    @GetMapping("/explore/{noteId}")
    String noteDetail(@PathVariable("noteId") String noteId);

}
