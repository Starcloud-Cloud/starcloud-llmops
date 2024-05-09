package com.starcloud.ops.business.app.dal.databoject.xhs.material;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_material", autoResultMap = true)
public class CreativeMaterialDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    /**
     * 素材类型 {@link MaterialTypeEnum#getTypeCode()}
     */
    private String type;

    /**
     * 素材内容摘要 用于筛选
     */
    private String content;

    /**
     * 素材详情
     */
    private String materialDetail;

    /**
     * 标签用于筛选 格式: tag1,tag2,tag3
     */

    @TableField(typeHandler = TagsHandler.class)
    private List<String> tags;


    public static class TagsHandler extends AbstractJsonTypeHandler<List<String>> {

        private static final String separator = ",";

        @Override
        protected List<String> parse(String json) {
            if (StringUtils.isBlank(json)) {
                return Collections.emptyList();
            }
            return Arrays.stream(json.split(separator)).distinct().collect(Collectors.toList());
        }

        @Override
        protected String toJson(List<String> obj) {
            if (CollectionUtils.isEmpty(obj)) {
                return StringUtils.EMPTY;
            }
            return obj.stream().collect(Collectors.joining(separator));
        }
    }
}
