package com.starcloud.ops.business.app.service.opus.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import com.starcloud.ops.business.app.service.opus.OpusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpusServiceImpl implements OpusService {


    public void directoryTree(String opusUid) {
        List<OpusDirectoryDO> opusDirectoryDOList = new ArrayList<>();
        Map<String, OpusDirectoryDO> dirUidMap = opusDirectoryDOList.stream().collect(Collectors.toMap(OpusDirectoryDO::getDirUid, Function.identity()));
        Map<String, List<OpusDirectoryDO>> parentUidGroup = opusDirectoryDOList.stream().collect(Collectors.groupingBy(OpusDirectoryDO::getParentUid));


    }




}
