package com.starcloud.ops.business.app.service.opus.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.*;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusDO;
import com.starcloud.ops.business.app.dal.mysql.opus.OpusMapper;
import com.starcloud.ops.business.app.service.opus.OpusDirectoryService;
import com.starcloud.ops.business.app.service.opus.OpusService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.OPUS_ERROR;

@Slf4j
@Service
public class OpusServiceImpl implements OpusService {

    @Resource
    private OpusMapper opusMapper;

    @Resource
    private OpusDirectoryService directoryService;

    @Resource
    private CreativeContentService contentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpusRespVO create(OpusBaseVO opusBaseVO) {
        OpusDO opusDO = OpusConvert.INSTANCE.convert(opusBaseVO);
        opusDO.setOpusUid(IdUtil.fastSimpleUUID());
        opusMapper.insert(opusDO);
        directoryService.addDirNode(OpusDirBaseVO.defaultDir(opusDO.getOpusUid()));
        return OpusConvert.INSTANCE.convert(opusDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String opusUid) {
        OpusDO opusDO = opusMapper.selectByOpusUid(opusUid);
        if (Objects.isNull(opusDO)) {
            throw exception(OPUS_ERROR, "作品集不存在，opusUid=" + opusUid);
        }
        opusMapper.delete(OpusDO::getOpusUid, opusUid);
        directoryService.deleteByOpusUid(opusUid);
    }

    @Override
    public void modify(OpusModifyReqVO reqVO) {
        OpusDO opusDO = opusMapper.selectByOpusUid(reqVO.getOpusUid());
        if (Objects.isNull(opusDO)) {
            throw exception(OPUS_ERROR, "作品集不存在，opusUid=" + reqVO.getOpusUid());
        }
        OpusDO updateDO = OpusConvert.INSTANCE.convert(reqVO);
        updateDO.setId(opusDO.getId());
        opusMapper.updateById(updateDO);
    }

    @Override
    public PageResult<OpusRespVO> page(PageParam pageParam) {
        PageResult<OpusDO> page = opusMapper.page(pageParam);
        return OpusConvert.INSTANCE.convert(page);
    }

    @Override
    public DirectoryNodeVO createDir(OpusDirBaseVO dirBaseVO) {
        OpusDO opusDO = opusMapper.selectByOpusUid(dirBaseVO.getOpusUid());
        if (Objects.isNull(opusDO)) {
            throw exception(OPUS_ERROR, "作品集不存在，opusUid=" + dirBaseVO.getOpusUid());
        }
        return directoryService.addDirNode(dirBaseVO);
    }

    @Override
    public OpusBindRespVO addBind(OpusBindBaseVO bindReqVO) {
        OpusDO opusDO = opusMapper.selectByOpusUid(bindReqVO.getOpusUid());
        if (Objects.isNull(opusDO)) {
            throw exception(OPUS_ERROR, "作品集不存在，opusUid=" + bindReqVO.getOpusUid());
        }

        // check opusType

        return directoryService.addBind(bindReqVO);
    }
}
