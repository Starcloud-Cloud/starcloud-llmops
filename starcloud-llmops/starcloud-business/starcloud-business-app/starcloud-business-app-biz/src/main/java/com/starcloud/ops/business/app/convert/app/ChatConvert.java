package com.starcloud.ops.business.app.convert.app;

import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * 我的应用转换类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Mapper
@SuppressWarnings("all")
public interface ChatConvert {

    /**
     * AppConvert
     */
    ChatConvert INSTANCE = Mappers.getMapper(ChatConvert.class);

    /**
     * AppReqVO 转 AppEntity
     *
     * @param appRequest 我的应用请求对象
     * @return AppEntity
     */
    ChatConfigEntity convert(Map ChatConfigEntityMap);


    /**
     * handlerSkillVO 转 HandlerSkill
     *
     * @param handlerSkillVO HandlerSkillVO
     * @return AppMarketEntity
     */
    default HandlerSkill convert(HandlerSkillVO handlerSkillVO) {

        HandlerSkill handlerSkill = HandlerSkill.of(handlerSkillVO.getName());
        handlerSkill.setEnabled(handlerSkill.getEnabled());
        return handlerSkill;
    }

}
