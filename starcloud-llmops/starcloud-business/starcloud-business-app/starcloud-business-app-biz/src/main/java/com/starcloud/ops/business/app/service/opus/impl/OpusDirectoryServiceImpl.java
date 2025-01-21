package com.starcloud.ops.business.app.service.opus.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.controller.admin.opus.vo.*;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDirectoryDO;
import com.starcloud.ops.business.app.dal.mysql.opus.OpusDirectoryMapper;
import com.starcloud.ops.business.app.service.opus.OpusBindService;
import com.starcloud.ops.business.app.service.opus.OpusDirectoryService;
import com.starcloud.ops.business.app.util.OpusDirUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.util.OpusDirUtils.ROOT;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.OPUS_ERROR;

@Slf4j
@Service
public class OpusDirectoryServiceImpl implements OpusDirectoryService {

    @Resource
    private OpusDirectoryMapper opusDirectoryMapper;

    @Resource
    private OpusBindService bindService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DirectoryNodeVO addDirNode(OpusDirBaseVO dirBaseVO) {
        String parentUid = StringUtils.isBlank(dirBaseVO.getParentUid()) ? ROOT : dirBaseVO.getParentUid();
        dirBaseVO.setParentUid(parentUid);
        if (StringUtils.isNoneBlank(parentUid) && !Objects.equals(ROOT, parentUid)) {
            // check parent dir
            OpusDirectoryDO parentDir = opusDirectoryMapper.selectParentDir(dirBaseVO.getOpusUid(), parentUid);
            if (Objects.isNull(parentDir)) {
                throw exception(OPUS_ERROR, "父目录不存在，parentUid=" + parentUid);
            }
        }

        updateOrder(dirBaseVO);
        OpusDirectoryDO addDir = OpusConvert.INSTANCE.convert(dirBaseVO);
        addDir.setDirUid(IdUtil.fastSimpleUUID());
        opusDirectoryMapper.insert(addDir);
        return OpusConvert.INSTANCE.convert(addDir);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByOpusUid(String opusUid) {
        opusDirectoryMapper.delete(OpusDirectoryDO::getOpusUid, opusUid);
        bindService.deleteByOpusUid(opusUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String dirUid) {
        OpusDirectoryDO directoryDO = opusDirectoryMapper.selectByDirUid(dirUid);
        if (Objects.isNull(directoryDO)) {
            throw exception(OPUS_ERROR, "目录不存在，dirUid=" + dirUid);
        }
        opusDirectoryMapper.deleteById(directoryDO.getId());
        bindService.deleteByDirUid(dirUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(OpusDirModifyReqVO reqVO) {
        OpusDirectoryDO directoryDO = opusDirectoryMapper.selectByDirUid(reqVO.getDirUid());
        if (Objects.isNull(directoryDO)) {
            throw exception(OPUS_ERROR, "目录不存在，dirUid=" + reqVO.getDirUid());
        }
        if (!Objects.equals(directoryDO.getOpusUid(), reqVO.getOpusUid())) {
            throw exception(OPUS_ERROR, "目录不在作品集下，dirUid=" + reqVO.getDirUid() + ",opusUid=" + reqVO.getOpusUid());
        }

        if (StringUtils.isBlank(reqVO.getParentUid())) {
            reqVO.setParentUid(ROOT);
        }

        // 父目录不变 校验order 更新
        if (Objects.equals(reqVO.getParentUid(), directoryDO.getParentUid())) {
            if (!Objects.equals(directoryDO.getOrder(), reqVO.getOrder())) {
                updateOrder(reqVO);
            }
            OpusDirectoryDO updateDO = OpusConvert.INSTANCE.convert(reqVO);
            updateDO.setId(directoryDO.getId());
            opusDirectoryMapper.updateById(updateDO);
            return;
        }
        // 校验循环目录
        List<OpusDirectoryDO> opusDirectoryDOList = opusDirectoryMapper.selectByOpusUid(reqVO.getOpusUid());
        Map<String, List<String>> adjacencyList = new HashMap<>();
        for (OpusDirectoryDO opusDirectoryDO : opusDirectoryDOList) {
            if (Objects.equals(opusDirectoryDO.getDirUid(), reqVO.getDirUid())) {
                // 修改后的目录
                adjacencyList.computeIfAbsent(reqVO.getParentUid(), k -> new ArrayList<String>()).add(reqVO.getDirUid());
                continue;
            }
            adjacencyList.computeIfAbsent(opusDirectoryDO.getParentUid(), k -> new ArrayList<String>()).add(opusDirectoryDO.getDirUid());
        }
        if (OpusDirUtils.hasCycle(adjacencyList)) {
            throw exception(OPUS_ERROR, "目录树存在循环引用");
        }

        // 修改父目录 校验order 更新
        updateOrder(reqVO);
        OpusDirectoryDO updateDO = OpusConvert.INSTANCE.convert(reqVO);
        updateDO.setId(directoryDO.getId());
        opusDirectoryMapper.updateById(updateDO);
    }

    @Override
    public List<DirectoryNodeVO> opusNodeTree(String opusUid) {
        List<OpusDirectoryDO> opusDirectoryDOList = opusDirectoryMapper.selectByOpusUid(opusUid);
        return OpusDirUtils.buildChildrenNode(opusDirectoryDOList);
    }

    @Override
    public OpusBindRespVO addBind(OpusBindBaseVO bindReqVO) {
        OpusDirectoryDO directoryDO = opusDirectoryMapper.selectByDirUid(bindReqVO.getDirUid());
        if (Objects.isNull(directoryDO)) {
            throw exception(OPUS_ERROR, "目录不存在，dirUid=" + bindReqVO.getDirUid());
        }
        if (!Objects.equals(directoryDO.getOpusUid(), bindReqVO.getOpusUid())) {
            throw exception(OPUS_ERROR, "目录不在作品集下，dirUid=" + bindReqVO.getDirUid() + ",opusUid=" + bindReqVO.getOpusUid());
        }

        return bindService.addBind(bindReqVO);
    }

    /**
     * 更新排序字段
     */
    private void updateOrder(OpusDirBaseVO opusVO) {
        List<OpusDirectoryDO> directoryDOList = opusDirectoryMapper.selectByParentUid(opusVO.getOpusUid(), opusVO.getParentUid());
        List<OpusDirectoryDO> updateDir = new ArrayList<>();
        for (OpusDirectoryDO opusDirectoryDO : directoryDOList) {
            if (Objects.equals(opusDirectoryDO.getDirName(), opusVO.getDirName())) {
                throw exception(OPUS_ERROR, "同级目录下已有同名目录，" + opusVO.getDirName());
            }
            if (opusDirectoryDO.getOrder() >= opusVO.getOrder()) {
                opusDirectoryDO.setOrder(opusDirectoryDO.getOrder() + 1);
                updateDir.add(opusDirectoryDO);
            }
        }
        if (CollectionUtil.isNotEmpty(updateDir)) {
            opusDirectoryMapper.updateBatch(updateDir);
        }
    }

}
