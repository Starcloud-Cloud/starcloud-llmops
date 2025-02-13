package com.starcloud.ops.business.app.service.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.TemplateRecordRespVO;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateRecordDO;
import com.starcloud.ops.business.app.dal.mysql.template.TemplateRecordMapper;
import com.starcloud.ops.business.app.exception.TemplateErrorData;
import com.starcloud.ops.business.app.exception.TemplateException;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.NOT_TEMPLATE_RESOURCE;

@Slf4j
@Service
public class TemplateRecordServiceImpl implements TemplateRecordService {

    @Resource
    private TemplateRecordMapper recordMapper;

    @Resource
    private AdminUserRightsApi rightsApi;

    @Override
    public void addRecord(List<PosterStyleDTO> posterStyleDTOList) {
        Set<String> templateCodes = CreativeUtils.getPosterTemplateCodes(posterStyleDTOList);
        List<TemplateRecordDO> recordDOList = recordMapper.selectList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<TemplateRecordDO> addTemplateRecords = new ArrayList<>();

        List<String> recordCodeList = recordDOList.stream().map(TemplateRecordDO::getTemplateCode).collect(Collectors.toList());
        for (String templateCode : templateCodes) {
            if (!recordCodeList.contains(templateCode)) {
                TemplateRecordDO addRecord = new TemplateRecordDO();
                addRecord.setUid(IdUtil.fastSimpleUUID());
                addRecord.setTemplateCode(templateCode);
                addTemplateRecords.add(addRecord);
            }
        }
        checkRecordNum(posterStyleDTOList);
        recordMapper.insertBatch(addTemplateRecords);
    }

    @Override
    public List<TemplateRecordRespVO> listRecord() {
        return recordMapper.templateList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
    }

    @Override
    public void checkRecordNum(List<PosterStyleDTO> posterStyleDTOList) {
        List<TemplateRecordDO> recordDOList = recordMapper.selectList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<String> recordCodeList = recordDOList.stream().map(TemplateRecordDO::getTemplateCode).collect(Collectors.toList());

        Set<TemplateErrorData> addTemplateDataList = new HashSet<>();
        for (PosterStyleDTO posterStyleDTO : posterStyleDTOList) {
            if (Objects.isNull(posterStyleDTO.getSaleConfig())
                    || BooleanUtils.isNotTrue(posterStyleDTO.getSaleConfig().getOpenSale())
                    || CollectionUtil.isEmpty(posterStyleDTO.getTemplateList())) {
                continue;
            }

            for (PosterTemplateDTO posterTemplateDTO : posterStyleDTO.getTemplateList()) {
                if ((!recordCodeList.contains(posterTemplateDTO.getCode()))) {
                    TemplateErrorData templateErrorData = new TemplateErrorData(posterTemplateDTO.getCode(), posterStyleDTO.getSaleConfig().getDemoId());
                    addTemplateDataList.add(templateErrorData);
                }
            }
        }
        long count = addTemplateDataList.stream().map(TemplateErrorData::getTemplateCode).distinct().count();

        Integer originalFixedRightsSums = rightsApi.getOriginalFixedRightsSums(WebFrameworkUtils.getLoginUserId(), AdminUserRightsTypeEnum.TEMPLATE.getType());
        if (count == 0) {
            return;
        }
        int total = recordDOList.size() + (int) count;
        if (Objects.isNull(originalFixedRightsSums) || total > originalFixedRightsSums) {
            throw new TemplateException(NOT_TEMPLATE_RESOURCE, addTemplateDataList);
        }

    }
}
