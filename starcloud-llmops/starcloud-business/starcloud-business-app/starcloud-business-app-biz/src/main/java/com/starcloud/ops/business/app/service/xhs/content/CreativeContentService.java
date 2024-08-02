package com.starcloud.ops.business.app.service.xhs.content;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentRegenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentTaskReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;

import java.util.List;

public interface CreativeContentService {

    /**
     * 获取创作内容详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    CreativeContentRespVO get(String uid);

    /**
     * 查询详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    CreativeContentRespVO detail(String uid);

    /**
     * 查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    List<CreativeContentRespVO> list(CreativeContentListReqVO query);

    /**
     * 查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    List<CreativeContentRespVO> listStatus(CreativeContentListReqVO query);

    /**
     * 查询创作内容任务列表
     *
     * @param query 查询条件
     * @return 创作内容任务列表
     */
    List<CreativeContentRespVO> listTask(CreativeContentTaskReqVO query);

    /**
     * 分页查询创作内容
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO query);

    /**
     * 创建装作内容
     *
     * @param request 请求
     * @return 创作内容UID
     */
    String create(CreativeContentCreateReqVO request);

    /**
     * 批量创建创作内容
     *
     * @param requestList 批量请求
     */
    void batchCreate(List<CreativeContentCreateReqVO> requestList);

    /**
     * 修改创作内容
     *
     * @param request 修改请求
     * @return 创作内容UID
     */
    String modify(CreativeContentModifyReqVO request);

    /**
     * 删除创作内容
     *
     * @param uid 创作内容UID
     */
    void delete(String uid);

    /**
     * 删除计划下的所有创作内容
     *
     * @param planUid 计划UID
     */
    void deleteByPlanUid(String planUid);

    /**
     * 执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    CreativeContentExecuteRespVO execute(CreativeContentExecuteReqVO request);

    /**
     * 批量执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    List<CreativeContentExecuteRespVO> batchExecute(List<CreativeContentExecuteReqVO> request);

    /**
     * 重新生成创作内容
     *
     * @param request 执行请求
     */
    void regenerate(CreativeContentRegenerateReqVO request);

    /**
     * 失败重试
     *
     * @param uid 任务 uid
     */
    void retry(String uid);

    /**
     * 批量绑定创作内容
     *
     * @param uidList 创作内容UID集合
     * @return 绑定之后结果
     */
    List<CreativeContentRespVO> batchBind(List<String> uidList);

    /**
     * 批量解绑创作内容
     *
     * @param uidList 创作内容UID集合
     */
    void batchUnbind(List<String> uidList);

    /**
     * 点赞
     *
     * @param uid 创作内容UID
     */
    void like(String uid);

    /**
     * 取消点赞
     *
     * @param uid 创作内容UID
     */
    void unlike(String uid);

}
