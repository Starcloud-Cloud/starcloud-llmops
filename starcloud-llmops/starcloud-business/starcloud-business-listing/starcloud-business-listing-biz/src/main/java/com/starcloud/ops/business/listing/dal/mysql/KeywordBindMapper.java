package com.starcloud.ops.business.listing.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordBindDO;
import com.starcloud.ops.business.listing.dto.KeywordBindDTO;
import com.starcloud.ops.business.listing.enums.KeywordBindTypeEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeywordBindMapper extends BaseMapperX<KeywordBindDO> {

    default List<KeywordBindDO> getByDraftId(Long draftId) {
        LambdaQueryWrapper<KeywordBindDO> wrapper = Wrappers.lambdaQuery(KeywordBindDO.class)
                .eq(KeywordBindDO::getDraftId, draftId);
        return selectList(wrapper);
    }

    default List<KeywordBindDO> getByDictId(Long dictId) {
        LambdaQueryWrapper<KeywordBindDO> wrapper = Wrappers.lambdaQuery(KeywordBindDO.class)
                .eq(KeywordBindDO::getDictId, dictId);
        return selectList(wrapper);
    }

    default void deleteDraftKey(List<String> keys, Long draftId) {
        LambdaQueryWrapper<KeywordBindDO> wrapper = Wrappers.lambdaQuery(KeywordBindDO.class)
                .eq(KeywordBindDO::getDraftId, draftId)
                .in(KeywordBindDO::getKeyword,keys)
                ;
        delete(wrapper);
    }

    default void deleteDictKey(List<String> keys, Long dictId) {
        LambdaQueryWrapper<KeywordBindDO> wrapper = Wrappers.lambdaQuery(KeywordBindDO.class)
                .eq(KeywordBindDO::getDictId, dictId)
                .in(KeywordBindDO::getKeyword,keys)
                ;
        delete(wrapper);
    }

    default List<KeywordBindDO> getList(KeywordBindDTO bindDTO) {
        LambdaQueryWrapper<KeywordBindDO> wrapper = Wrappers.lambdaQuery(KeywordBindDO.class)
                .eq(KeywordBindTypeEnum.draft.name().equals(bindDTO.getType()),KeywordBindDO::getDraftId, bindDTO.getDraftId())
                .eq(KeywordBindTypeEnum.dict.name().equals(bindDTO.getType()),KeywordBindDO::getDictId, bindDTO.getDictId());
        return selectList(wrapper);
    }

}
