package com.starcloud.ops.business.app.service.xhs.huitun;

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
     * 获取灰豚个人便签信息
     */
    void getPersonFeat();

    // /**
    //  * 灰豚数据 小红书数据搜索
    //  */
    // void noteSearch(Long batch, CreativePlanRespVO creativePlan);

    /**
     * 灰豚手机登录接口
     *
     * @param mobile   手机号
     * @param password 密码
     */
    String phoneLogin(String mobile, String password);


    void autoLogin();
}
