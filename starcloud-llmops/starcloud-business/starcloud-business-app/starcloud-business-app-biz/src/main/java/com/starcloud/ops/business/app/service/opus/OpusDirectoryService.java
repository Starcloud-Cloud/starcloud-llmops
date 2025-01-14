package com.starcloud.ops.business.app.service.opus;

import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;

import java.util.List;

public interface OpusDirectoryService {

    /**
     * 作品集目录树
     */
    List<DirectoryNodeVO> opusNodeTree(String opusUid);
}
