package com.starcloud.ops.business.app.service.xhs.plan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanGetQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanUpgradeReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
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
     * 创建创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    String create(CreativePlanCreateReqVO request);

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    String modify(CreativePlanModifyReqVO request);

    /**
     * 删除创作计划
     *
     * @param uid 创作计划UID
     */
    void delete(String uid);

    /**
     * 更新创作计划状态
     *
     * @param planUid  计划UID
     * @param batchUid 批次UID
     */
    void updatePlanStatus(String planUid, String batchUid);

    /**
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    void execute(String uid);

    /**
     * 升级创作计划
     *
     * @param request 执行请求
     */
    void upgrade(CreativePlanUpgradeReqVO request);

    /**
     * 获取应用信息
     *
     * @param appUid 应用UID
     * @param source 来源
     * @return 应用信息
     */
    AppMarketRespVO getAppInformation(String appUid, String source);

    /**
     * 创作计划集合
     *
     * @return 创作计划集合
     */
    List<CreativePlanRespVO> list();

}
