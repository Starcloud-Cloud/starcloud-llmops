package com.starcloud.ops.business.app.dal.databoject.materiallibrary;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import lombok.*;

import java.util.List;

/**
 * 素材知识库数据 DO
 *
 * @author starcloudadmin
 */
@TableName(value = "llm_material_library_slice", autoResultMap = true)
@KeySequence("llm_material_library_slice_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialLibrarySliceDO extends DeptBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 素材库ID
     */
    private Long libraryId;
    /**
     * 字符数
     */
    private Long charCount;
    /**
     * 总使用次数
     */
    private Long usedCount;
    /**
     * 内容
     */
    @TableField(typeHandler = TableContentHandler.class)
    private List<TableContent> content;
    /**
     * 序列
     */
    private Long sequence;

    /**
     * 状态
     * <p>
     * 枚举
     */
    private Boolean status;
    /**
     * 是否开启数据共享
     */
    private Boolean isShare;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableContent {
        /**
         * 列ID
         */
        private Long columnId;

        /**
         * 列ID
         */
        private String columnCode;

        /**
         * 列名
         */
        private String columnName;
        /**
         * 值
         */
        private String value;
        /**
         * 描述
         */
        private String description;
        /**
         * 标签
         */
        private List<String> tags;
        
        /**
         * 扩展数据
         */
        private String extend;

    }


    public static class TableContentHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseArray(json, TableContent.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}