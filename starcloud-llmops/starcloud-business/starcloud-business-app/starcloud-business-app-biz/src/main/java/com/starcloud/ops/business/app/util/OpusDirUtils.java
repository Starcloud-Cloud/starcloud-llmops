package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.controller.admin.opus.vo.DirectoryNodeVO;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.OPUS_ERROR;

public class OpusDirUtils {

    public static final String ROOT = "root";

    private static final Integer VISITING = 1;
    private static final Integer VISITED = 2;


    /**
     * 校验循环依赖
     *
     * @param adjacencyList key为parentUid, value为子节点dirUid
     * @return
     */
    public static boolean hasCycle(Map<String, List<String>> adjacencyList) {
        Map<String, Integer> visited = new HashMap<>(adjacencyList.size());
        for (String dirUid : adjacencyList.keySet()) {
            if (Objects.isNull(visited.get(dirUid)) && isCyclic(dirUid, visited, adjacencyList)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCyclic(String dirUid, Map<String, Integer> visited, Map<String, List<String>> adjacencyList) {
        if (Objects.equals(visited.get(dirUid), VISITING)) {
            // 发现循环
            return true;
        }
        visited.put(dirUid, VISITING);

        List<String> children = adjacencyList.getOrDefault(dirUid, Collections.emptyList());
        for (String child : children) {
            if (!Objects.equals(visited.get(child), VISITED) && isCyclic(child, visited, adjacencyList)) {
                return true;
            }
        }

        visited.put(dirUid, VISITED);
        return false;
    }

    /**
     * 构建目录树
     */
    public static List<DirectoryNodeVO> buildChildrenNode(List<OpusDirectoryDO> opusDirectoryDOList) {
        Map<String, List<OpusDirectoryDO>> parentUidGroup = opusDirectoryDOList.stream().collect(Collectors.groupingBy(OpusDirectoryDO::getParentUid));
        Map<String, List<String>> adjacencyList = opusDirectoryDOList.stream()
                .collect(Collectors.groupingBy(OpusDirectoryDO::getParentUid,
                        Collectors.mapping(OpusDirectoryDO::getDirUid, Collectors.toList())));

        if (hasCycle(adjacencyList)) {
            throw exception(OPUS_ERROR, "目录树存在循环引用");
        }
        return buildChildrenNode(ROOT, parentUidGroup);
    }


    private static List<DirectoryNodeVO> buildChildrenNode(String parentUid, Map<String, List<OpusDirectoryDO>> parentUidGroup) {
        List<OpusDirectoryDO> childrenDirList = parentUidGroup.get(parentUid);
        if (CollectionUtil.isEmpty(childrenDirList)) {
            return null;
        }
        List<DirectoryNodeVO> result = new ArrayList<>(childrenDirList.size());
        for (OpusDirectoryDO opusDirectoryDO : childrenDirList) {
            List<DirectoryNodeVO> childrenNodeList = buildChildrenNode(opusDirectoryDO.getDirUid(), parentUidGroup);
            DirectoryNodeVO currentNode = OpusConvert.INSTANCE.convert(opusDirectoryDO);
            currentNode.setChildren(childrenNodeList);
            result.add(currentNode);
        }
        return result;
    }

}
