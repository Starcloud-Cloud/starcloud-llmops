package com.starcloud.ops.business.app.dal.databoject.materiallibrary;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 素材知识库数据 DO
 *
 * @author starcloudadmin
 */
@TableName("llm_material_library_slice")
@KeySequence("llm_material_library_slice_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialLibrarySliceDO extends BaseDO {

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
     * 链接
     */
    private String url;
    /**
     * 状态
     * <p>
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Boolean status;
    /**
     * 是否开启数据共享
     */
    private Boolean isShare;


    @Schema(description = "列属性")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableContent {
        /**
         * 列ID
         */
        @Schema(description = " 列 ID", example = " 1")
        private String columnId;
        /**
         * 值
         */
        private String value;
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