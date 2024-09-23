package com.starcloud.ops.biz.service.elementtype;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypePageReqVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeRespVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeSaveReqVO;
import com.starcloud.ops.biz.convert.ElementTypeConvert;
import com.starcloud.ops.biz.dal.dataobject.elementtype.ElementtypeDO;
import com.starcloud.ops.biz.dal.mysql.elementtype.ElementtypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
// import static com.starcloud.ops.api.enums.ErrorCodeConstants.ELEMENTTYPE_NOT_EXISTS;


/**
 * 海报元素类型 Service 实现类
 *
 * @author xhsadmin
 */
@Service
public class ElementTypeServiceImpl implements ElementTypeService {

    @Resource
    private ElementtypeMapper elementTypeMapper;

    @Override
    public ElementTypeRespVO createElementtype(ElementTypeSaveReqVO createReqVO) {
        // 插入
        ElementtypeDO elementtype = ElementTypeConvert.INSTANCE.convert(createReqVO);
        elementTypeMapper.insert(elementtype);
        // 返回
        return ElementTypeConvert.INSTANCE.convert(elementtype);
    }

    @Override
    public void updateElementtype(ElementTypeSaveReqVO updateReqVO) {
        // 校验存在
        validateElementtypeExists(updateReqVO.getUid());
        // 更新
        ElementtypeDO updateObj = ElementTypeConvert.INSTANCE.convert(updateReqVO);
        elementTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteElementtype(String uid) {
        // 校验存在
        validateElementtypeExists(uid);
        // 删除
        elementTypeMapper.deleteByUid(uid);
    }

    private void validateElementtypeExists(String uid) {
        if (getElementtype(uid) == null) {
            // throw exception(ELEMENTTYPE_NOT_EXISTS);
        }
    }

    @Override
    public ElementtypeDO getElementtype(String uid) {
        return elementTypeMapper.selectByUid(uid);
    }

    @Override
    public PageResult<ElementtypeDO> getElementtypePage(ElementTypePageReqVO pageReqVO) {
        return elementTypeMapper.selectPage(pageReqVO);
    }

}