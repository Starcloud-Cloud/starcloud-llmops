package com.starcloud.ops.business.log.service.embmedding;

import com.starcloud.ops.business.log.dal.dataobject.LogEmbeddingDO;
import com.starcloud.ops.business.log.dal.mysql.LogEmbeddingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class LogEmbeddingServiceImpl implements LogEmbeddingService{

    @Resource
    private LogEmbeddingMapper logEmbeddingMapper;


    @Override
    public void createLog(LogEmbeddingDO logEmbeddingDO) {
        logEmbeddingMapper.insert(logEmbeddingDO);
    }
}
