package com.starcloud.ops.business.user.service.notify.params;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyTemplateDO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.FilterUserReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.dal.dataobject.notify.PurchaseExperienceParamsDTO;
import com.starcloud.ops.business.user.dal.mysql.notify.NotifyParamsMapper;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.PARAMS_ERROR;

/**
 * 提醒新用户购买体验包
 */
@Slf4j
@Service
public class PurchaseExperienceServiceImpl extends NotifyParamsAbstractService {

    @Resource
    private NotifyParamsMapper paramsMapper;


    @Override
    public String support() {
        return NotifyTemplateEnum.NOTIFY_PURCHASE_EXPERIENCE.getCode();
    }

    @Override
    public List<NotifyContentRespVO> prepareParams() {
        NotifyTemplateDO template = getTemplate();
        List<PurchaseExperienceParamsDTO> paramsList = paramsMapper.purchaseExperienceParams();
        List<NotifyContentRespVO> result = new ArrayList<>(paramsList.size());
        for (PurchaseExperienceParamsDTO paramsDTO : paramsList) {
            NotifyContentRespVO notifyContent = new NotifyContentRespVO();
            notifyContent.setReceiverName(paramsDTO.getNickname());
            notifyContent.setReceiverId(paramsDTO.getUserId());
            Map<String, Object> params = BeanUtil.beanToMap(paramsDTO);
            notifyContent.setTemplateParams(params);
            for (String key : template.getParams()) {
                Object value = params.get(key);
                if (value == null) {
                    log.warn("{} 收信人 {} 模板参数({})缺失", support(), notifyContent.getReceiverId(), key);
                    throw exception(PARAMS_ERROR, notifyContent.getReceiverId(), key);
                }
            }
            notifyContent.setContent(formatContent(template.getContent(), params));
            result.add(notifyContent);
        }

        return result;
    }

    @Override
    public PageResult<NotifyContentRespVO> pageFilterNotifyContent(FilterUserReqVO reqVO) {
        long start = System.currentTimeMillis();
        Long count = paramsMapper.countPurchaseExperienceParams();
        if (count == null || count <= 0) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        List<NotifyContentRespVO> result = new ArrayList<>(reqVO.getPageSize());
        List<PurchaseExperienceParamsDTO> paramsList = paramsMapper.pagePurchaseExperienceParams(PageUtils.getStart(reqVO), reqVO.getPageSize());
        long end = System.currentTimeMillis();
        log.info("page notify prepare params, {} ms", end - start);
        NotifyTemplateDO template = getTemplate();
        for (PurchaseExperienceParamsDTO paramsDTO : paramsList) {
            NotifyContentRespVO notifyContent = new NotifyContentRespVO();
            notifyContent.setReceiverName(paramsDTO.getNickname());
            notifyContent.setReceiverId(paramsDTO.getUserId());
            Map<String, Object> params = BeanUtil.beanToMap(paramsDTO);
            notifyContent.setTemplateParams(params);
            for (String key : template.getParams()) {
                Object value = params.get(key);
                if (value == null) {
                    log.warn("收信人 {} 模板参数({})缺失", notifyContent.getReceiverId(), key);
                    throw exception(PARAMS_ERROR, notifyContent.getReceiverId(), key);
                }
            }
            notifyContent.setContent(formatContent(template.getContent(), params));
            result.add(notifyContent);
        }
        return new PageResult<>(result, count);
    }
}
