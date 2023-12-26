package com.starcloud.ops.business.product.convert.property;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.product.controller.admin.property.vo.value.ProductPropertyValueCreateReqVO;
import com.starcloud.ops.business.product.controller.admin.property.vo.value.ProductPropertyValueRespVO;
import com.starcloud.ops.business.product.controller.admin.property.vo.value.ProductPropertyValueUpdateReqVO;
import com.starcloud.ops.business.product.dal.dataobject.property.ProductPropertyValueDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 属性值 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductPropertyValueConvert {

    ProductPropertyValueConvert INSTANCE = Mappers.getMapper(ProductPropertyValueConvert.class);

    ProductPropertyValueDO convert(ProductPropertyValueCreateReqVO bean);

    ProductPropertyValueDO convert(ProductPropertyValueUpdateReqVO bean);

    ProductPropertyValueRespVO convert(ProductPropertyValueDO bean);

    List<ProductPropertyValueRespVO> convertList(List<ProductPropertyValueDO> list);

    PageResult<ProductPropertyValueRespVO> convertPage(PageResult<ProductPropertyValueDO> page);

}
