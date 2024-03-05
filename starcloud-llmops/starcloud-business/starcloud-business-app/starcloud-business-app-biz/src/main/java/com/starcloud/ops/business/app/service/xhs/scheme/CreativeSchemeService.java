package com.starcloud.ops.business.app.service.xhs.scheme;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;

import java.util.List;
import java.util.Map;

/**
 * 创作方案服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
public interface CreativeSchemeService {

    /**
     * 获取创作方案元数据
     *
     * @return 创作方案元数据
     */
    Map<String, Object> metadata();

    /**
     * 获取创作方案配置
     *
     * @return 创作方案配置
     */
    List<CreativeSchemeConfigurationDTO> configurationList(String model);

    /**
     * 获取创作方案详情
     *
     * @param uid 创作方案UID
     * @return 创作方案详情
     */
    CreativeSchemeRespVO get(String uid);

    /**
     * 获取创作方案列表
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    List<CreativeSchemeRespVO> list(CreativeSchemeListReqVO query);

    /**
     * 查询并且校验创作方案是否存在
     *
     * @param uidList 创作方案UID列表
     * @return 创作方案列表
     */
    List<CreativeSchemeRespVO> list(List<String> uidList);

    /**
     * 获取创作方案列表
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    List<CreativeSchemeListOptionRespVO> listOption(CreativeSchemeListReqVO query);

    /**
     * 分页查询创作方案
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    PageResult<CreativeSchemeRespVO> page(CreativeSchemePageReqVO query);

    /**
     * 创建创作方案
     *
     * @param request 创作方案请求
     */
    void create(CreativeSchemeReqVO request);

    /**
     * 复制创作方案
     *
     * @param request 请求
     */
    void copy(UidRequest request);

    /**
     * 修改创作方案
     *
     * @param request 创作方案请求
     */
    void modify(CreativeSchemeModifyReqVO request);

    /**
     * 删除创作方案
     *
     * @param uid 创作方案UID
     */
    void delete(String uid);

    /**
     * 创建文案示例
     *
     * @param request 创作方案需求请求
     * @return 文案示例
     */
    void example(CreativeSchemeModifyReqVO request);

}
