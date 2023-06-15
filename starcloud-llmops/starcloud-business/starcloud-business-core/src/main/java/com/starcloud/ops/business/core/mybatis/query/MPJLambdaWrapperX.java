package com.starcloud.ops.business.core.mybatis.query;

import cn.iocoder.yudao.framework.common.util.collection.ArrayUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

public class MPJLambdaWrapperX<T> extends MPJLambdaWrapper<T> {


    public MPJLambdaWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (MPJLambdaWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (MPJLambdaWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (MPJLambdaWrapperX<T>) le(column, val2);
        }
        return this;
    }

    public MPJLambdaWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }



}
