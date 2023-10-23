package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.convert.ListingDictConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordBindDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywordBindMapper;
import com.starcloud.ops.business.listing.dal.mysql.ListingDictMapper;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.enums.AnalysisStatusEnum;
import com.starcloud.ops.business.listing.service.DictService;
import com.starcloud.ops.business.listing.service.KeywordBindService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class DictServiceImpl implements DictService {

    @Resource
    private ListingDictMapper dictMapper;

    @Resource
    private KeywordBindService keywordBindService;


    @Resource
    private KeywordBindMapper keywordBindMapper;

    @Resource(name = "listingExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictRespVO create(DictCreateReqVO reqVO) {
        ListingDictDO dictDO = dictMapper.getByName(reqVO.getName());
        if (dictDO != null) {
            throw exception(DICT_NAME_EXISTS, reqVO.getName());
        }
        ListingDictDO listingDictDO = ListingDictConvert.INSTANCE.convert(reqVO);
        listingDictDO.setUid(IdUtil.fastSimpleUUID());
        listingDictDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
        dictMapper.insert(listingDictDO);

        List<String> keys = reqVO.getKeys();
        if (CollectionUtil.isNotEmpty(keys)) {
            execute(keys, listingDictDO);
        }
        return ListingDictConvert.INSTANCE.convert(listingDictDO);
    }

    @Override
    public PageResult<DictRespVO> getDictPage(DictPageReqVO dictPageReqVO) {
        PageResult<ListingDictDO> page = dictMapper.page(dictPageReqVO);
        return ListingDictConvert.INSTANCE.convert(page);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(DictModifyReqVO modifyReqVO) {
        ListingDictDO dictDO = getDict(modifyReqVO.getUid());
        if (AnalysisStatusEnum.ANALYSIS.name().equals(dictDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS, modifyReqVO.getUid());
        }

        List<KeywordBindDO> oldKey = keywordBindMapper.getByDictId(dictDO.getId());
        List<String> newKey = modifyReqVO.getKeywordResume();

        ListingDictConvert.INSTANCE.updateParams(modifyReqVO, dictDO);
        if (CollectionUtil.isNotEmpty(newKey)) {
            List<Long> delKeyIds = new ArrayList<>();
            for (KeywordBindDO keywordBindDO : oldKey) {
                boolean remove = newKey.remove(keywordBindDO.getKeyword());
                if (!remove) {
                    delKeyIds.add(keywordBindDO.getId());
                }
            }
            keywordBindMapper.deleteBatchIds(delKeyIds);
            execute(newKey, dictDO);
        } else {
            dictMapper.updateById(dictDO);
        }
    }

    @Override
    public void deleteDict(List<String> uids) {
        if (CollectionUtil.isEmpty(uids)) {
            return;
        }
        dictMapper.delete(uids);
    }

    @Override
    public DictRespVO dictDetail(String uid) {
        ListingDictDO dictDO = getDict(uid);
        DictRespVO respVO = ListingDictConvert.INSTANCE.convert(dictDO);
        List<String> keywordBinds = keywordBindMapper.getByDictId(dictDO.getId()).stream()
                .map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        respVO.setKeywordResume(keywordBinds);
        if (AnalysisStatusEnum.ANALYSIS.name().equals(respVO.getStatus())) {
            return respVO;
        }

        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(keywordBinds, dictDO.getEndpoint());
        respVO.setKeywordMetaData(metaData);
        return respVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addKeyword(String uid, List<String> keys) {
        ListingDictDO dictDO = getDict(uid);
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        List<String> newKey = keys.stream()
                .map(String::trim).distinct().collect(Collectors.toList());

        List<String> oldKey = keywordBindMapper.getByDictId(dictDO.getId())
                .stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        newKey.removeAll(oldKey);
        if (oldKey.size() + newKey.size() > 2000) {
            throw exception(new ErrorCode(500, "关键词总个数不能超过2000，新增后数量:{}"), oldKey.size() + newKey.size());
        }
        execute(newKey, dictDO);
    }

    @Override
    public void removeKey(String uid, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            throw exception(new ErrorCode(500, "删除关键词不能为空"));
        }
        ListingDictDO dictDO = getDict(uid);
        keywordBindMapper.deleteDictKey(keys, dictDO.getId());
    }

    private void execute(List<String> keys, ListingDictDO dictDO) {
        List<String> keywords = keys.stream().map(String::trim)
                .distinct().collect(Collectors.toList());
        dictDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        keywordBindService.addDictKeyword(keywords, dictDO.getId());
        dictMapper.updateById(dictDO);
        executor.execute(() -> {
            try {
                keywordBindService.analysisKeyword(keywords, dictDO.getEndpoint());
                dictDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
                dictMapper.updateById(dictDO);
            } catch (Exception e) {
                log.error("分析关键词失败", e);
                dictDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
                dictMapper.updateById(dictDO);
            }
        });
    }

    private ListingDictDO getDict(String uid) {
        ListingDictDO dictDO = dictMapper.getByUid(uid);
        if (dictDO == null) {
            throw exception(DICT_NOT_EXISTS, uid);
        }
        return dictDO;
    }
}
