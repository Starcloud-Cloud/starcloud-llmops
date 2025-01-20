package com.starcloud.ops.business.app.service.opus;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindBaseVO;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindRespVO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusBindDO;

import java.util.List;

public interface OpusBindService {

    /**
     * 删除作品集绑定
     */
    void deleteByOpusUid(String opusUid);

    /**
     * 删除目录绑定
     */
    void deleteByDirUid(String dirUid);

    /**
     * 删除绑定
     */
    void delete(String bindUid);

    /**
     * 添加创作内容绑定
     */
    OpusBindRespVO addBind(OpusBindBaseVO bindReqVO);

    /**
     * 分页查询绑定
     */
    PageResult<OpusBindRespVO> page(OpusBindPageReqVO pageParam);

}
