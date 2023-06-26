package com.starcloud.ops.business.app.dal.redis.app;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.redis.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-30
 */
@SuppressWarnings("unused")
@Repository
public class RecommendedAppRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private AppMapper appMapper;

    /**
     * 获取推荐的模板
     *
     * @return 推荐的模板
     */
    public List<AppRespVO> get() {

        return Collections.emptyList();
    }

    /**
     * 设置推荐的模板
     */
    public List<AppRespVO> set() {
//
        return null;
    }

    /**
     * 根据模版类型重置推荐的模板缓存
     *
     * @param type 模版类型
     */
    public void resetByType(String type) {

    }

    /**
     * 设置推荐的模板
     *
     * @param templates 推荐的模板
     */
    public void set(List<AppDTO> templates) {
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
