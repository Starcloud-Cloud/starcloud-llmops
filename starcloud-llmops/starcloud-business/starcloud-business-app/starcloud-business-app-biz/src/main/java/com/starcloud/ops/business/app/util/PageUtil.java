package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public class PageUtil {

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页数量
     * @param <T>  分页对象类型
     * @return 分页对象
     */
    public static <T> Page<T> page(Integer page, Integer size) {
        return new Page<>(page, size);
    }

    /**
     * 分页查询
     *
     * @param query 分页查询参数
     * @param <T>   分页对象类型
     * @return 分页对象
     */
    public static <T> Page<T> page(PageQuery query) {
        Page<T> page = new Page<>(query.getPageNo(), query.getPageSize());
        List<OrderItem> sorts = CollectionUtil.emptyIfNull(query.getSorts()).stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(item.getField());
            orderItem.setAsc(!SortType.DESC.name().equalsIgnoreCase(item.getType()));
            return orderItem;
        }).collect(Collectors.toList());
        page.setOrders(sorts);
        return page;
    }

}
