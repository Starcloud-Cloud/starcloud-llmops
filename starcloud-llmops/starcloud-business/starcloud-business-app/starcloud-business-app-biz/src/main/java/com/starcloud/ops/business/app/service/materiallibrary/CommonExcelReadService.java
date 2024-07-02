package com.starcloud.ops.business.app.service.materiallibrary;

import java.util.List;

/**
 *
 * 公共的excel读取接口
 * 实现接口并重写saveBatchData方法即可保存自定义的业务数据
 */
public interface CommonExcelReadService {

    /**
     * 批量保存数据
     * @param list 要保存的数据
     * @param <T> 数据类型实体，可以根据具体的业务实体转换
     * @return Integer 保存成功的条数
     */
    public <T> Integer saveBatchData(List<T> list);
}

