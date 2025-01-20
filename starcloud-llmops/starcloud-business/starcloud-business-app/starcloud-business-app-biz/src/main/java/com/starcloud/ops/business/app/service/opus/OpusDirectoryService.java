package com.starcloud.ops.business.app.service.opus;

import com.starcloud.ops.business.app.controller.admin.opus.vo.*;

import java.util.List;

public interface OpusDirectoryService {

    /**
     * 新增目录节点
     */
    DirectoryNodeVO addDirNode(OpusDirBaseVO dirBaseVO);

    /**
     * 删除作品集目录
     */
    void deleteByOpusUid(String opusUid);

    /**
     * 修改作品集目录
     * @param reqVO
     */
    void modify(OpusDirModifyReqVO reqVO);

    /**
     * 作品集目录树
     */
    List<DirectoryNodeVO> opusNodeTree(String opusUid);

    /**
     * 删除目录
     * @param dirUid
     */
    void delete(String dirUid);

    /**
     * 添加创作内容绑定
     * @param bindReqVO
     */
    OpusBindRespVO addBind(OpusBindBaseVO bindReqVO);
}
