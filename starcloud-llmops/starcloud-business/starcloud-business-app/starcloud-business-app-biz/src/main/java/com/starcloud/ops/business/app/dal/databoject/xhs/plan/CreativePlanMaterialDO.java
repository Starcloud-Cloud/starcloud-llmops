package com.starcloud.ops.business.app.dal.databoject.xhs.plan;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "llm_creative_plan", autoResultMap = true)
public class CreativePlanMaterialDO extends CreativePlanDO {

    /**
     * 素材列表
     */
    @TableField(typeHandler = MaterialHandler.class)
    private List<Map<String, Object>> materialList;


    public static class MaterialHandler extends AbstractJsonTypeHandler<List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> parse(String json) {
            if (StringUtils.isBlank(json)) {
                return Collections.emptyList();
            }
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
            };
            return JSONUtil.toBean(json, typeReference, true);
        }

        @Override
        protected String toJson(List<Map<String, Object>> obj) {
            if (CollectionUtils.isEmpty(obj)) {
                return StrUtil.EMPTY_JSON;
            }
            return JSONUtil.toJsonStr(obj);
        }
    }
}
