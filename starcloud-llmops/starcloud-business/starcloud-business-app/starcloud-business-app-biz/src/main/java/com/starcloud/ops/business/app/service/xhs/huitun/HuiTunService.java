package com.starcloud.ops.business.app.service.xhs.huitun;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;

import java.util.List;

/**
 * 灰豚数据
 */
public interface HuiTunService {

    /**
     * 获取灰豚第一层标签
     */
    void getFirstNoteTags();

    /**
     * 获取搜索条件
     */
    void getSearchConditions();

    /**
     *获取灰豚个人便签信息
     */
    void getPersonFeat();

    /**
     * 灰豚数据 小红书数据搜索
     */
    void noteSearch(Long batch, CreativePlanRespVO creativePlan);

    /**
     * 灰豚手机登录接口
     *
     * @param mobile
     * @param password
     * @return
     */
    String phoneLogin(String mobile, String password);


    void autoLogin();
}
