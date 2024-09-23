package com.starcloud.ops.biz.service.element;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementPageReqVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementRespVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementSaveReqVO;
import com.starcloud.ops.biz.convert.ElementConvert;
import com.starcloud.ops.biz.dal.dataobject.element.ElementDO;
import com.starcloud.ops.biz.dal.mysql.element.ElementMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
// import static com.starcloud.ops.api.enums.ErrorCodeConstants.ELEMENT_NOT_EXISTS;


/**
 * 海报元素 Service 实现类
 *
 * @author xhsadmin
 */
@Service
@Validated
public class ElementServiceImpl implements ElementService {

    @Resource
    private ElementMapper elementMapper;

    @Override
    public ElementRespVO createElement(ElementSaveReqVO createReqVO) {
        // 插入
        ElementDO element = ElementConvert.INSTANCE.convert(createReqVO);
        elementMapper.insert(element);
        // 返回
        return ElementConvert.INSTANCE.convert(element);
    }

    @Override
    public void updateElement(ElementSaveReqVO updateReqVO) {
        // 校验存在
        validateElementExists(updateReqVO.getUid());
        // 更新
        ElementDO updateObj = ElementConvert.INSTANCE.convert(updateReqVO);
        elementMapper.updateById(updateObj);
    }

    @Override
    public void deleteElement(String uid) {
        // 校验存在
        validateElementExists(uid);
        // 删除
        elementMapper.deleteByUid(uid);
    }

    private void validateElementExists(String uid) {
        if (getElement(uid) == null) {
            // throw exception(ELEMENT_NOT_EXISTS);
        }
    }

    @Override
    public ElementDO getElement(String uid) {
        return elementMapper.selectByUid(uid);
    }

    @Override
    public PageResult<ElementDO> getElementPage(ElementPageReqVO pageReqVO) {
        return elementMapper.selectPage(pageReqVO);
    }

}