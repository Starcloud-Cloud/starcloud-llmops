package com.starcloud.ops.business.app.service.opus.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import com.starcloud.ops.business.app.dal.mysql.opus.OpusDirectoryMapper;
import com.starcloud.ops.business.app.service.opus.OpusDirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpusDirectoryServiceImpl implements OpusDirectoryService {

    @Resource
    private OpusDirectoryMapper opusDirectoryMapper;

    @Override
    public List<DirectoryNodeVO> opusNodeTree(String opusUid) {
        List<OpusDirectoryDO> opusDirectoryDOList = opusDirectoryMapper.selectByOpusUid(opusUid);
        Map<String, OpusDirectoryDO> dirUidMap = opusDirectoryDOList.stream().collect(Collectors.toMap(OpusDirectoryDO::getDirUid, Function.identity()));
        Map<String, List<OpusDirectoryDO>> parentUidGroup = opusDirectoryDOList.stream().collect(Collectors.groupingBy(OpusDirectoryDO::getParentUid));
        return buildChildrenNode("root", parentUidGroup, dirUidMap);
    }

    public List<DirectoryNodeVO> buildChildrenNode(String parentUid, Map<String, List<OpusDirectoryDO>> parentUidGroup, Map<String, OpusDirectoryDO> dirUidMap) {
        List<OpusDirectoryDO> childrenDirList = parentUidGroup.get(parentUid);
        if (CollectionUtil.isEmpty(childrenDirList)) {
            return null;
        }
        List<DirectoryNodeVO> result = new ArrayList<>(childrenDirList.size());
        for (OpusDirectoryDO opusDirectoryDO : childrenDirList) {
            List<DirectoryNodeVO> childrenNodeList = buildChildrenNode(opusDirectoryDO.getDirUid(), parentUidGroup, dirUidMap);
            DirectoryNodeVO currentNode = OpusConvert.INSTANCE.convert(opusDirectoryDO);
            currentNode.setChildren(childrenNodeList);
            result.add(currentNode);
        }
        return result;
    }
}
