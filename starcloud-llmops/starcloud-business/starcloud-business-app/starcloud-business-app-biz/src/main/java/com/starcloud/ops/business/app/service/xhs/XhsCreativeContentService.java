package com.starcloud.ops.business.app.service.xhs;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeQueryReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;

import javax.validation.Valid;
import java.util.List;

public interface XhsCreativeContentService {

    /**
     * 批量创建
     */
    void create(List<XhsCreativeContentCreateReq> list);

    /**
     * 批量执行
     *
     * @param type  XhsCreativeContentTypeEnums.code
     * @param force 忽略重试次数限制
     */
    void execute(List<Long> ids, String type, Boolean force);

    /**
     * 重试
     */
    void retry(String businessUid);

    /**
     * 查询任务
     */
    List<XhsCreativeContentDO> batchSelect(@Valid XhsCreativeQueryReq queryReq);

    /**
     * 分页查询创作内容
     */
    PageResult<XhsCreativeContentResp> page(XhsCreativeContentPageReq req);

    /**
     * 查询详情
     */
    XhsCreativeContentResp detail(String businessUid);

    /**
     * 修改创作内容
     */
    XhsCreativeContentResp modify(XhsCreativeContentModifyReq modifyReq);
}
