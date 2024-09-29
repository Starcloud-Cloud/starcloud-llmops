package com.starcloud.ops.business.app.service.xhs.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreateSameAppReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanGetQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanUpgradeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
public interface CreativePlanService {

    /**
     * 创作计划元数据
     *
     * @return 元数据
     */
    Map<String, List<Option>> metadata();

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    UploadImageInfoDTO uploadImage(MultipartFile image);

    /**
     * 获取创作计划详情
     *
     * @param uid 创作计划UID
     * @return 创作计划详情
     */
    CreativePlanRespVO get(String uid);

    /**
     * 获取创作计划列表
     *
     * @param query 请求参数
     * @return 创作计划列表
     */
    List<CreativePlanRespVO> list(CreativePlanListQuery query);

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    PageResult<CreativePlanRespVO> page(CreativePlanPageQuery query);

    /**
     * 获取创作计划详情，如果不存在则创建
     *
     * @param query 请求
     * @return 创作计划详情
     */
    CreativePlanRespVO getOrCreate(CreativePlanGetQuery query);

    /**
     * 创建同款应用
     *
     * @param request 创作计划请求
     */
    String createSameApp(CreateSameAppReqVO request);

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    CreativePlanRespVO modify(CreativePlanModifyReqVO request);

    /**
     * 修改计划配置项
     *
     * @param request 请求
     * @return 创作计划UID
     */
    CreativePlanRespVO modifyConfiguration(CreativePlanModifyReqVO request);

    /**
     * 删除创作计划
     *
     * @param uid 创作计划UID
     */
    void delete(String uid);

    /**
     * 删除创作计划
     *
     * @param appUid 应用uid
     */
    void deleteByAppUid(String appUid);

    /**
     * 取消创作计划
     *
     * @param batchUid 批次UID
     */
    void cancel(String batchUid);

    /**
     * 更新创作计划状态
     *
     * @param planUid  计划UID
     * @param batchUid 批次UID
     */
    void updatePlanStatus(String planUid, String batchUid);

    /**
     * 升级创作计划
     *
     * @param request 执行请求
     */
    Integer upgrade(CreativePlanUpgradeReqVO request);

    /**
     * 获取应用信息
     *
     * @param appUid 应用UID
     * @param source 来源
     * @return 应用信息
     */
    AppMarketRespVO getAppInformation(String appUid, String source);

    /**
     * 重我的应用或执行计划中获取应用信息
     *
     * @param uid      应用UID
     * @param planSource 计划来源
     * @return 应用信息
     */
    AppMarketRespVO getAppRespVO(String uid, String planSource);

    /**
     * 创作计划集合
     *
     * @return 创作计划集合
     */
    List<CreativePlanRespVO> list(Integer limit);

}
