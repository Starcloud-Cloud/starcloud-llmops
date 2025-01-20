package com.starcloud.ops.business.app.service.opus.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindBaseVO;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.opus.OpusConvert;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusBindDO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusCreativeBindDTO;
import com.starcloud.ops.business.app.dal.mysql.opus.OpusBindMapper;
import com.starcloud.ops.business.app.service.opus.OpusBindService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.OPUS_ERROR;

@Slf4j
@Service
public class OpusBindServiceImpl implements OpusBindService {

    @Resource
    private OpusBindMapper bindMapper;

    @Resource
    private CreativeContentService contentService;

    @Override
    public void deleteByOpusUid(String opusUid) {
        bindMapper.delete(OpusBindDO::getOpusUid, opusUid);
    }

    @Override
    public void deleteByDirUid(String dirUid) {
        bindMapper.delete(OpusBindDO::getDirUid, dirUid);
    }

    @Override
    public void delete(String bindUid) {
        bindMapper.delete(OpusBindDO::getBindUid, bindUid);
    }

    @Override
    public OpusBindRespVO addBind(OpusBindBaseVO bindReqVO) {
        OpusBindDO opusBindDO = bindMapper.selectBind(bindReqVO.getOpusUid(), bindReqVO.getCreativeContentUid());
        if (Objects.nonNull(opusBindDO)) {
            throw exception(OPUS_ERROR,
                    "作品集已存在绑定，opusUid=" + bindReqVO.getOpusUid()
                            + ",creativeUid=" + bindReqVO.getCreativeContentUid());
        }

        OpusBindDO addBind = OpusConvert.INSTANCE.convert(bindReqVO);
        addBind.setBindUid(IdUtil.fastSimpleUUID());
        bindMapper.insert(addBind);
        return OpusConvert.INSTANCE.convert(addBind);
    }

    @Override
    public PageResult<OpusBindRespVO> page(OpusBindPageReqVO pageParam) {
        PageResult<OpusCreativeBindDTO> pageResult = bindMapper.page(pageParam);
        return OpusConvert.INSTANCE.convertBind(pageResult);
    }

}
