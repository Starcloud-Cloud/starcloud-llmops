package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialType;
import org.springframework.stereotype.Component;

/**
 * 书单资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Component
@MaterialType(MaterialTypeEnum.BOOK_LIST)
class BookListMaterialHandler extends AbstractMaterialHandler<BookListCreativeMaterialDTO> {


}