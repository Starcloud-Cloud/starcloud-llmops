package com.starcloud.ops.business.app.service.opus;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.*;

import javax.validation.Valid;

public interface OpusService {

    /**
     * 新建作品集
     */
    OpusRespVO create(OpusBaseVO opusBaseVO);

    /**
     * 删除作品集
     * @param opusUid
     */
    void delete(String opusUid);

    /**
     * 修改作品集
     * @param reqVO
     */
    void modify(OpusModifyReqVO reqVO);

    /**
     * 作品集新增目录
     * @param dirBaseVO
     */
    DirectoryNodeVO createDir(@Valid OpusDirBaseVO dirBaseVO);

    /**
     * 分页查询作品集
     * @param pageParam
     * @return
     */
    PageResult<OpusRespVO> page(@Valid PageParam pageParam);

    /**
     * 添加创作内容绑定
     * @param bindReqVO
     */
    OpusBindRespVO addBind(OpusBindBaseVO bindReqVO);
}
