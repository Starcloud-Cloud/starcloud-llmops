package com.starcloud.ops.business.app.dal.redis.template;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.convert.TemplateConvert;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateDO;
import com.starcloud.ops.business.app.dal.mysql.template.TemplateMapper;
import com.starcloud.ops.business.app.dal.redis.RedisKeyConstants;
import com.starcloud.ops.business.app.enums.template.TemplateTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-30
 */
@Repository
public class RecommendedTemplatesRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private TemplateMapper templateMapper;

    /**
     * 获取推荐的模板
     *
     * @return 推荐的模板
     */
    public List<TemplateDTO> get() {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(getKey()))) {
            String rec = stringRedisTemplate.opsForValue().get(getKey());
            if (StringUtils.isNotBlank(rec)) {
                JSON.parseArray(stringRedisTemplate.opsForValue().get(getKey()), TemplateDTO.class);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 设置推荐的模板
     */
    public List<TemplateDTO> set() {
        // 查询推荐的模版列表
        List<TemplateDO> templates = templateMapper.selectList(Wrappers.lambdaQuery(TemplateDO.class)
                .eq(TemplateDO::getType, TemplateTypeEnum.SYSTEM_TEMPLATE.name())
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, 0)
                .orderByDesc(TemplateDO::getUpdateTime)
        );
        List<TemplateDTO> templateList = CollectionUtil.emptyIfNull(templates).stream().map(TemplateConvert::convert).collect(Collectors.toList());
        // 缓存到redis
        stringRedisTemplate.opsForValue().set(getKey(), JSON.toJSONString(templateList));

        return templateList;
    }

    /**
     * 根据模版类型重置推荐的模板缓存
     *
     * @param type 模版类型
     */
    public void resetByType(String type) {
        if (TemplateTypeEnum.SYSTEM_TEMPLATE.name().equals(type)) {
            set();
        }
    }

    /**
     * 设置推荐的模板
     *
     * @param templates 推荐的模板
     */
    public void set(List<TemplateDTO> templates) {
        stringRedisTemplate.opsForValue().set(getKey(), JSON.toJSONString(templates));
    }

    /**
     * 删除推荐的模板
     */
    public void delete() {
        stringRedisTemplate.delete(getKey());
    }

    /**
     * 获取推荐的模板的 Redis Key
     *
     * @return 推荐的模板的 Redis Key
     */
    private String getKey() {
        return RedisKeyConstants.RECOMMENDED_TEMPLATES_KEY;
    }

}
