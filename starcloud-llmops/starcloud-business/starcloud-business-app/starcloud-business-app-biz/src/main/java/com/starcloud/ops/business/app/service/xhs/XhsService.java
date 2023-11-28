package com.starcloud.ops.business.app.service.xhs;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageStyleExecuteResponse;

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
    List<XhsImageTemplateDTO> imageTemplates();

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
     * 通用执行应用
     *
     * @param request 请求
     * @return 响应
     */
    String execute(AppExecuteReqVO request);

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
     * 执行生成小红书图片
     *
     * @param request 请求
     * @return 响应
     */
    XhsImageExecuteResponse imageExecute(XhsImageExecuteRequest request);

    /**
     * 小红书图片风格执行，一个风格可能多个图片
     *
     * @param request 请求
     * @return 响应
     */
    XhsImageStyleExecuteResponse imageStyleExecute(XhsImageStyleExecuteRequest request);

    /**
     * 执行创作中心小红书图片生成
     *
     * @param request 请求
     * @return 响应
     */
    XhsImageCreativeExecuteResponse imageCreativeExecute(XhsImageCreativeExecuteRequest request);

    /**
     * 批量执行创作中心小红书图片生成
     *
     * @param requests 请求
     * @return 响应
     */
    List<XhsImageCreativeExecuteResponse> bathImageCreativeExecute(List<XhsImageCreativeExecuteRequest> requests);

}
