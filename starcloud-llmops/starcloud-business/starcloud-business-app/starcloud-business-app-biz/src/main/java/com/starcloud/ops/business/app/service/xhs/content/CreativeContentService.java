package com.starcloud.ops.business.app.service.xhs.content;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDO;

import java.util.List;
import java.util.Map;

public interface CreativeContentService {

    /**
     * 批量创建
     */
    void create(List<CreativeContentCreateReqVO> list);

    /**
     * 批量执行
     *
     * @param type  XhsCreativeContentTypeEnums.code
     * @param force 忽略重试次数限制
     */
    Map<Long, Boolean> execute(List<Long> ids, String type, Boolean force);

    /**
     * 重试
     */
    CreativeContentRespVO retry(String businessUid);

    /**
     * 查询任务
     */
    List<XhsCreativeContentDO> jobQuery(CreativeQueryReqVO queryReq);

    /**
     * 查询计划的所有任务
     */
    List<XhsCreativeContentDO> listByPlanUid(String planUid);

    /**
     * 计划下的所有任务根据 业务uid 分组
     *
     * @param planUidList 计划uid
     * @return 业务uid
     */
    List<XhsCreativeContentBusinessPO> listGroupByBusinessUid(List<String> planUidList);

    /**
     * 分页查询创作内容
     */
    PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO req);

    com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<CreativeContentRespVO> newPage(CreativeContentPageReqVO req);

    /**
     * 查询详情
     */
    CreativeContentRespVO detail(String businessUid);

    /**
     * 修改创作内容
     */
    CreativeContentRespVO modify(CreativeContentModifyReqVO modifyReq);

    /**
     * 删除
     *
     * @param businessUid
     * @return
     */
    void delete(String businessUid);

    /**
     * 删除计划下的所有创作内容
     *
     * @param planUid 计划uid
     */
    void deleteByPlanUid(String planUid);

    /**
     * 绑定任务
     */
    List<CreativeContentRespVO> bound(List<String> businessUids);

    /**
     * 点赞
     *
     * @param businessUid 业务uid
     */
    void like(String businessUid);

    /**
     * 取消点赞
     *
     * @param businessUid 业务uid
     */
    void unlike(String businessUid);

}
