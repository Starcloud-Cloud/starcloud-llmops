package com.starcloud.ops.business.app.service.template;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.TemplateRecordRespVO;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateRecordDO;
import com.starcloud.ops.business.app.dal.mysql.template.TemplateRecordMapper;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
        List<String> templateCodes = CreativeUtils.getPosterTemplateCodes(posterStyleDTOList);
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
        List<String> templateCodes = CreativeUtils.getPosterTemplateCodes(posterStyleDTOList);
        List<TemplateRecordDO> recordDOList = recordMapper.selectList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<String> recordCodeList = recordDOList.stream().map(TemplateRecordDO::getTemplateCode).collect(Collectors.toList());

        int addNum = 0;
        for (String templateCode : templateCodes) {
            if (!recordCodeList.contains(templateCode)) {
                addNum++;
            }
        }

        Integer originalFixedRightsSums = rightsApi.getOriginalFixedRightsSums(AdminUserRightsTypeEnum.TEMPLATE.getType());
        if (addNum > originalFixedRightsSums) {
            throw exception(NOT_TEMPLATE_RESOURCE);
        }

    }
}
