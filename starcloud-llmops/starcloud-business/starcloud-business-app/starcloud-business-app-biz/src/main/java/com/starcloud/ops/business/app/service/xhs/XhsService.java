package com.starcloud.ops.business.app.service.xhs;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
public interface XhsService {

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    List<XhsImageTemplateResponse> imageTemplates();

    /**
     * 根据类型获取需要执行的应用信息
     *
     * @param type 计划类型
     * @return 应用信息
     */
    AppMarketRespVO getExecuteApp(String type);

    /**
     * 根据类型获取应用列表
     *
     * @param type 类型
     * @return 文案模板列表
     */
    List<AppMarketRespVO> appMarketplaceList(String type);

    /**
     * 执行应用
     *
     * @param request 请求
     * @return 响应
     */
    List<XhsAppExecuteResponse> appExecute(XhsAppExecuteRequest request);

    /**
     * 异步执行应用
     *
     * @param request 请求
     */
    void asyncAppExecute(XhsAppExecuteRequest request);

    /**
     * 批量执行应用, 同步执行
     *
     * @param requests 请求
     * @return 响应
     */
    List<XhsAppCreativeExecuteResponse> bathAppCreativeExecute(List<XhsAppCreativeExecuteRequest> requests);

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    XhsImageExecuteResponse imageExecute(XhsImageExecuteRequest request);

    /**
     * 异步批量执行图片
     *
     * @param request 请求
     * @return 响应
     */
    List<XhsImageExecuteResponse> bathImageExecute(XhsBathImageExecuteRequest request);
}
